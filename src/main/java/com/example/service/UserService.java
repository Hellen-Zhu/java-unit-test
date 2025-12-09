package com.example.service;

import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.exception.UserNotFoundException;
import com.example.exception.DuplicateEmailException;

/**
 * 用户服务类 - 用于Mockito练习
 */
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    /**
     * 注册新用户
     */
    public User register(User user) {
        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateEmailException("Email already exists: " + user.getEmail());
        }

        // 保存用户
        User savedUser = userRepository.save(user);

        // 发送欢迎邮件
        emailService.sendWelcomeEmail(savedUser.getEmail());

        return savedUser;
    }

    /**
     * 根据ID查找用户
     */
    public User findUser(Long id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        return user;
    }

    /**
     * 更新用户信息
     */
    public User updateUser(Long id, User updatedUser) {
        User existingUser = findUser(id);

        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());

        User savedUser = userRepository.save(existingUser);

        // 发送更新通知
        emailService.sendUpdateNotification(savedUser.getEmail());

        return savedUser;
    }

    /**
     * 删除用户
     */
    public void deleteUser(Long id) {
        User user = findUser(id);

        // 发送账户删除通知
        emailService.sendAccountDeletionEmail(user.getEmail());

        // 删除用户
        userRepository.delete(id);
    }

    /**
     * 获取用户统计信息
     */
    public UserStats getUserStats(Long userId) {
        User user = findUser(userId);
        int postCount = userRepository.getPostCount(userId);
        int followerCount = userRepository.getFollowerCount(userId);

        return new UserStats(user, postCount, followerCount);
    }

    /**
     * 用户统计信息类
     */
    public static class UserStats {
        private final User user;
        private final int postCount;
        private final int followerCount;

        public UserStats(User user, int postCount, int followerCount) {
            this.user = user;
            this.postCount = postCount;
            this.followerCount = followerCount;
        }

        // Getters
        public User getUser() { return user; }
        public int getPostCount() { return postCount; }
        public int getFollowerCount() { return followerCount; }
    }
}