package com.example.service;

import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.exception.UserNotFoundException;
import com.example.exception.DuplicateEmailException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

/**
 * UserService测试类 - 展示Mockito的核心功能
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("用户服务测试")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private User testUser;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        testUser = new User(1L, "Tom", "tom@example.com");
    }

    @Test
    @DisplayName("测试成功注册新用户")
    void testRegisterSuccess() {
        // Given
        User newUser = new User(null, "Tom", "tom@example.com");
        User savedUser = new User(1L, "Tom", "tom@example.com");

        when(userRepository.existsByEmail("tom@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        doNothing().when(emailService).sendWelcomeEmail(anyString());

        // When
        User result = userService.register(newUser);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Tom", result.getName());
        assertEquals("tom@example.com", result.getEmail());

        // Verify interactions
        verify(userRepository).existsByEmail("tom@example.com");
        verify(userRepository).save(newUser);
        verify(emailService).sendWelcomeEmail("tom@example.com");

        // 验证调用顺序
        InOrder inOrder = inOrder(userRepository, emailService);
        inOrder.verify(userRepository).existsByEmail("tom@example.com");
        inOrder.verify(userRepository).save(newUser);
        inOrder.verify(emailService).sendWelcomeEmail("tom@example.com");
    }

    @Test
    @DisplayName("测试注册时邮箱已存在")
    void testRegisterDuplicateEmail() {
        // Given
        User newUser = new User(null, "Tom", "tom@example.com");
        when(userRepository.existsByEmail("tom@example.com")).thenReturn(true);

        // When & Then
        DuplicateEmailException exception = assertThrows(
            DuplicateEmailException.class,
            () -> userService.register(newUser)
        );

        assertEquals("Email already exists: tom@example.com", exception.getMessage());

        // Verify
        verify(userRepository).existsByEmail("tom@example.com");
        verify(userRepository, never()).save(any());
        verify(emailService, never()).sendWelcomeEmail(anyString());
    }

    @Test
    @DisplayName("测试查找存在的用户")
    void testFindUserExists() {
        // Given
        when(userRepository.findById(1L)).thenReturn(testUser);

        // When
        User result = userService.findUser(1L);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getName(), result.getName());

        // Verify
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("测试查找不存在的用户")
    void testFindUserNotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(null);

        // When & Then
        UserNotFoundException exception = assertThrows(
            UserNotFoundException.class,
            () -> userService.findUser(999L)
        );

        assertEquals("User not found with id: 999", exception.getMessage());

        // Verify
        verify(userRepository).findById(999L);
    }

    @Test
    @DisplayName("测试更新用户信息")
    void testUpdateUser() {
        // Given
        User updatedData = new User(null, "Tom Updated", "newemail@example.com");
        User updatedUser = new User(1L, "Tom Updated", "newemail@example.com");

        when(userRepository.findById(1L)).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        doNothing().when(emailService).sendUpdateNotification(anyString());

        // When
        User result = userService.updateUser(1L, updatedData);

        // Then
        assertEquals("Tom Updated", result.getName());
        assertEquals("newemail@example.com", result.getEmail());

        // 使用ArgumentCaptor捕获参数
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertEquals("Tom Updated", capturedUser.getName());
        assertEquals("newemail@example.com", capturedUser.getEmail());

        verify(emailService).sendUpdateNotification("newemail@example.com");
    }

    @Test
    @DisplayName("测试删除用户")
    void testDeleteUser() {
        // Given
        when(userRepository.findById(1L)).thenReturn(testUser);
        doNothing().when(emailService).sendAccountDeletionEmail(anyString());
        doNothing().when(userRepository).delete(1L);

        // When
        userService.deleteUser(1L);

        // Then
        verify(userRepository).findById(1L);
        verify(emailService).sendAccountDeletionEmail("tom@example.com");
        verify(userRepository).delete(1L);
    }

    @Test
    @DisplayName("测试获取用户统计信息")
    void testGetUserStats() {
        // Given
        when(userRepository.findById(1L)).thenReturn(testUser);
        when(userRepository.getPostCount(1L)).thenReturn(10);
        when(userRepository.getFollowerCount(1L)).thenReturn(100);

        // When
        UserService.UserStats stats = userService.getUserStats(1L);

        // Then
        assertNotNull(stats);
        assertEquals(testUser, stats.getUser());
        assertEquals(10, stats.getPostCount());
        assertEquals(100, stats.getFollowerCount());

        // Verify all interactions
        verify(userRepository).findById(1L);
        verify(userRepository).getPostCount(1L);
        verify(userRepository).getFollowerCount(1L);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("使用Spy测试部分Mock")
    void testWithSpy() {
        // 创建一个Spy对象
        UserService spyService = spy(userService);

        // 部分Mock - 只mock特定方法
        doReturn(testUser).when(spyService).findUser(1L);

        // 其他方法保持真实实现
        when(userRepository.getPostCount(1L)).thenReturn(5);
        when(userRepository.getFollowerCount(1L)).thenReturn(50);

        // When
        UserService.UserStats stats = spyService.getUserStats(1L);

        // Then
        assertEquals(5, stats.getPostCount());
        assertEquals(50, stats.getFollowerCount());
    }

    @Test
    @DisplayName("测试连续调用返回不同值")
    void testConsecutiveCalls() {
        // Given - 连续调用返回不同值
        when(userRepository.findById(1L))
            .thenReturn(testUser)
            .thenReturn(null)
            .thenThrow(new RuntimeException("Database error"));

        // First call
        User firstCall = userService.findUser(1L);
        assertNotNull(firstCall);

        // Second call
        assertThrows(UserNotFoundException.class,
            () -> userService.findUser(1L));

        // Third call
        assertThrows(RuntimeException.class,
            () -> userService.findUser(1L));
    }

    @Test
    @DisplayName("使用Answer自定义返回逻辑")
    void testWithAnswer() {
        // Given - 使用Answer动态返回值
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(System.currentTimeMillis()); // 动态生成ID
            return user;
        });

        // When
        User newUser = new User(null, "Dynamic", "dynamic@example.com");
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        doNothing().when(emailService).sendWelcomeEmail(anyString());

        User result = userService.register(newUser);

        // Then
        assertNotNull(result.getId());
        assertTrue(result.getId() > 0);
    }

    @AfterEach
    void tearDown() {
        // 重置mocks（使用@ExtendWith(MockitoExtension.class)时自动完成）
        // reset(userRepository, emailService);
    }
}