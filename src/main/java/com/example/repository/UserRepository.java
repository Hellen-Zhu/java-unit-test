package com.example.repository;

import com.example.model.User;

/**
 * 用户仓库接口 - 模拟数据访问层
 */
public interface UserRepository {

    /**
     * 保存用户
     */
    User save(User user);

    /**
     * 根据ID查找用户
     */
    User findById(Long id);

    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 删除用户
     */
    void delete(Long id);

    /**
     * 获取用户发帖数量
     */
    int getPostCount(Long userId);

    /**
     * 获取用户粉丝数量
     */
    int getFollowerCount(Long userId);
}