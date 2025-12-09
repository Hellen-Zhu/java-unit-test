# JUnit 5 & Mockito 一天速成学习计划

## 学习目标

在一天内掌握 JUnit 5 和 Mockito 的核心功能，能够编写基本的单元测试和集成测试。

## 时间安排（8小时集中学习）

```mermaid
timeline
    title 一天速成时间线

    09:00-10:00 : 环境搭建 & 基础概念
                : Maven/Gradle配置
                : 第一个测试用例

    10:00-12:00 : JUnit 5 核心
                : 常用注解
                : 断言方法
                : 生命周期

    12:00-13:00 : 午休

    13:00-15:00 : Mockito 核心
                : Mock基础
                : when-then模式
                : verify验证

    15:00-17:00 : 实战练习
                : Service层测试
                : Controller测试
                : 集成测试
```

## 第一部分：快速入门（1小时）

### 1.1 Gradle 依赖配置

```groovy
dependencies {
    // JUnit 5
    testImplementation 'org.junit.jupiter:junit-jupiter-api:5.9.2'
    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.9.2'
    testImplementation 'org.junit.jupiter:junit-jupiter-params:5.9.2'

    // Mockito
    testImplementation 'org.mockito:mockito-core:5.1.1'
    testImplementation 'org.mockito:mockito-junit-jupiter:5.1.1'

    // AssertJ (可选，提供更流畅的断言)
    testImplementation 'org.assertj:assertj-core:3.24.2'
}
```

### 1.2 基本概念速览

- **单元测试**: 测试最小可测试单元（通常是方法）
- **Mock对象**: 模拟的假对象，用于隔离测试
- **断言**: 验证实际结果是否符合预期
- **测试覆盖率**: 代码被测试覆盖的百分比

## 第二部分：JUnit 5 核心（2小时）

### 2.1 必会注解（30分钟）

| 注解 | 用途 | 示例 |
|------|------|------|
| @Test | 标记测试方法 | 每个测试方法都需要 |
| @BeforeEach | 每个测试前执行 | 初始化测试数据 |
| @AfterEach | 每个测试后执行 | 清理资源 |
| @BeforeAll | 所有测试前执行一次 | 初始化静态资源 |
| @DisplayName | 测试显示名称 | 让测试更易读 |
| @Disabled | 禁用测试 | 临时跳过某个测试 |
| @ParameterizedTest | 参数化测试 | 用不同参数运行同一测试 |

### 2.2 核心断言方法（30分钟）

```java
// 基本断言
assertEquals(expected, actual)          // 相等
assertNotEquals(unexpected, actual)     // 不相等
assertTrue(condition)                   // 为真
assertFalse(condition)                  // 为假
assertNull(object)                      // 为null
assertNotNull(object)                   // 不为null

// 异常断言
assertThrows(Exception.class, () -> {   // 抛出异常
    // 会抛出异常的代码
});

// 组合断言
assertAll(                               // 全部通过
    () -> assertEquals(2, 1 + 1),
    () -> assertTrue(true)
);

// 超时断言
assertTimeout(Duration.ofSeconds(2), () -> {
    // 需要在2秒内完成的代码
});
```

### 2.3 完整示例（1小时练习）

```java
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {

    private Calculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }

    @Test
    @DisplayName("测试加法")
    void testAdd() {
        assertEquals(5, calculator.add(2, 3));
    }

    @Test
    @DisplayName("测试除法抛出异常")
    void testDivideByZero() {
        assertThrows(ArithmeticException.class,
            () -> calculator.divide(10, 0));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    @DisplayName("参数化测试平方")
    void testSquare(int number) {
        assertEquals(number * number, calculator.square(number));
    }
}
```

## 第三部分：Mockito 核心（2小时）

### 3.1 Mock 基础（30分钟）

```java
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;  // 创建Mock对象

    @InjectMocks
    private UserService userService;        // 注入Mock依赖
}
```

### 3.2 核心用法（1小时）

#### When-Then 模式

```java
// 定义Mock行为
when(userRepository.findById(1L)).thenReturn(new User("Tom"));
when(userRepository.save(any())).thenReturn(savedUser);
when(userRepository.delete(1L)).thenThrow(new RuntimeException());

// 对于void方法
doNothing().when(mockObject).voidMethod();
doThrow(new Exception()).when(mockObject).voidMethod();
```

#### Verify 验证

```java
// 验证方法调用
verify(userRepository).findById(1L);           // 验证调用了1次
verify(userRepository, times(2)).save(any());  // 验证调用了2次
verify(userRepository, never()).delete(any()); // 验证从未调用
verify(userRepository, atLeast(1)).findAll();  // 至少调用1次
```

#### 参数匹配器

```java
// 常用匹配器
any()           // 任意对象
anyInt()        // 任意整数
anyString()     // 任意字符串
eq(value)       // 等于特定值
isNull()        // null值
isNotNull()     // 非null值
```

### 3.3 完整Mock示例（30分钟练习）

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("测试用户注册")
    void testRegisterUser() {
        // Given - 准备数据
        User newUser = new User("test@email.com", "password");
        User savedUser = new User(1L, "test@email.com", "password");

        // When - 定义Mock行为
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        doNothing().when(emailService).sendWelcomeEmail(anyString());

        // Act - 执行测试
        User result = userService.register(newUser);

        // Then - 验证结果
        assertNotNull(result.getId());
        assertEquals("test@email.com", result.getEmail());

        // Verify - 验证交互
        verify(userRepository).save(any(User.class));
        verify(emailService).sendWelcomeEmail("test@email.com");
    }

    @Test
    @DisplayName("测试查找不存在的用户")
    void testFindUserNotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(null);

        // Act & Assert
        assertThrows(UserNotFoundException.class,
            () -> userService.findUser(999L));

        // Verify
        verify(userRepository).findById(999L);
    }
}
```

## 第四部分：实战演练（2小时）

### 4.1 Service层测试模板

```java
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private PaymentService paymentService;
    @Mock private NotificationService notificationService;

    @InjectMocks
    private OrderService orderService;

    @Test
    void testCreateOrder() {
        // 1. 准备测试数据
        OrderRequest request = new OrderRequest(/* ... */);
        Order savedOrder = new Order(/* ... */);

        // 2. 定义Mock行为
        when(orderRepository.save(any())).thenReturn(savedOrder);
        when(paymentService.processPayment(any())).thenReturn(true);

        // 3. 执行被测方法
        Order result = orderService.createOrder(request);

        // 4. 断言结果
        assertNotNull(result);
        assertEquals(OrderStatus.PAID, result.getStatus());

        // 5. 验证交互
        verify(orderRepository).save(any());
        verify(paymentService).processPayment(any());
        verify(notificationService).sendOrderConfirmation(any());
    }
}
```

### 4.2 Controller层测试（Spring Boot）

```java
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void testGetUser() throws Exception {
        // Given
        User user = new User(1L, "Tom", "tom@email.com");
        when(userService.findById(1L)).thenReturn(user);

        // When & Then
        mockMvc.perform(get("/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Tom"))
            .andExpect(jsonPath("$.email").value("tom@email.com"));

        verify(userService).findById(1L);
    }

    @Test
    void testCreateUser() throws Exception {
        // Given
        User newUser = new User(null, "Tom", "tom@email.com");
        User savedUser = new User(1L, "Tom", "tom@email.com");
        when(userService.create(any())).thenReturn(savedUser);

        // When & Then
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": "Tom",
                        "email": "tom@email.com"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1));
    }
}
```

## 快速参考卡片

### JUnit 5 快查表

```java
// 测试类结构
@TestInstance(Lifecycle.PER_CLASS)  // 可选：改变生命周期
class MyTest {
    @BeforeAll static void initAll() { }
    @BeforeEach void init() { }
    @Test void testMethod() { }
    @AfterEach void tearDown() { }
    @AfterAll static void tearDownAll() { }
}

// 常用断言
assertEquals(expected, actual, "失败消息")
assertAll(() -> {}, () -> {})
assertTimeout(Duration.ofSeconds(1), () -> {})
assertThrows(Exception.class, () -> {})
```

### Mockito 快查表

```java
// 创建Mock
@Mock Type mockObject;
Type mock = mock(Type.class);

// 定义行为
when(mock.method()).thenReturn(value);
when(mock.method()).thenThrow(exception);
doReturn(value).when(mock).method();
doThrow(exception).when(mock).voidMethod();

// 验证
verify(mock).method();
verify(mock, times(n)).method();
verify(mock, never()).method();
verifyNoMoreInteractions(mock);
```

## 实战项目练习清单

### 必做练习（2小时）

1. **基础练习**（30分钟）
   - [ ] 创建StringUtils类，测试isEmpty、reverse、capitalize方法
   - [ ] 创建Calculator类，测试四则运算和异常处理

2. **Service层测试**（45分钟）
   - [ ] 创建UserService，模拟Repository层
   - [ ] 测试CRUD操作
   - [ ] 测试业务逻辑异常

3. **集成测试**（45分钟）
   - [ ] 创建简单的REST API
   - [ ] 使用MockMvc测试Controller
   - [ ] 测试请求和响应

## 常见问题速查

### Q1: 如何测试私有方法？

**答**: 不直接测试私有方法，通过测试公有方法间接测试。

### Q2: 如何Mock静态方法？

**答**: 使用Mockito 3.4+的mockStatic功能：

```java
try (MockedStatic<Utils> utilities = mockStatic(Utils.class)) {
    utilities.when(() -> Utils.staticMethod()).thenReturn(value);
}
```

### Q3: 如何测试void方法？

**答**: 使用doNothing()或doThrow()，并通过verify验证调用。

### Q4: Mock和Spy的区别？

**答**:

- Mock: 完全模拟对象，所有方法返回默认值
- Spy: 部分模拟，保留真实方法实现

### Q5: 如何处理测试数据准备？

**答**: 使用@BeforeEach准备通用数据，或创建测试数据工厂类。

## 进阶学习路线

完成一天速成后，建议继续深入学习：

1. **第2天**: Spring Boot Test完整特性
2. **第3天**: 测试最佳实践和设计模式
3. **第4天**: 性能测试和集成测试
4. **第5天**: TDD开发实践

## 学习资源

### 在线文档

- [JUnit 5 官方文档](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito 官方文档](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)

### 练习平台

- GitHub上搜索"junit-tutorial"或"mockito-examples"
- 创建自己的练习项目并持续完善

## 总结

一天速成的关键是：

1. **专注核心**: 只学最常用的20%功能，能覆盖80%的场景
2. **动手实践**: 每个概念都要写代码验证
3. **循序渐进**: 从简单测试到复杂场景
4. **查表驱动**: 把快查表放在手边，随时参考

记住：**测试是一种习惯，而不是负担**。今天学会的是基础，在实际项目中持续实践才能真正掌握。

---

最后更新: 2024年12月
