# JUnit 5 & Mockito 学习项目

本项目使用 Gradle 作为构建工具。

## 项目结构

```
unit_test/
├── src/
│   ├── main/java/com/example/
│   │   ├── Calculator.java              # 基础计算器类
│   │   ├── model/
│   │   │   └── User.java               # 用户实体
│   │   ├── repository/
│   │   │   └── UserRepository.java     # 数据访问接口
│   │   ├── service/
│   │   │   ├── UserService.java        # 用户服务
│   │   │   └── EmailService.java       # 邮件服务接口
│   │   └── exception/
│   │       ├── UserNotFoundException.java
│   │       └── DuplicateEmailException.java
│   └── test/java/com/example/
│       ├── CalculatorTest.java         # JUnit 5 基础示例
│       └── service/
│           └── UserServiceTest.java    # Mockito 完整示例
├── build.gradle                         # Gradle 构建配置
├── settings.gradle                      # Gradle 设置
├── gradle.properties                    # Gradle 属性
├── JUnit-Mockito-一天速成计划.md        # 学习计划文档
└── README.md                            # 本文件
```

## 快速开始

### 1. 运行测试

#### 使用 Gradle

```bash
# 运行所有测试
gradle test
# 或者
./gradlew test

# 运行特定测试类
gradle testCalculator
gradle testUserService

# 生成测试报告（包含覆盖率）
gradle test jacocoTestReport

# 查看测试报告
open build/reports/tests/test/index.html
open build/reports/jacoco/index.html
```


### 2. 学习路径

1. **先学 JUnit 5**
   - 打开 `CalculatorTest.java`
   - 学习基本注解和断言
   - 运行测试，观察输出

2. **再学 Mockito**
   - 打开 `UserServiceTest.java`
   - 学习 Mock、When-Then、Verify
   - 理解依赖注入和隔离测试

3. **动手练习**
   - 为 Calculator 添加新方法和测试
   - 为 UserService 添加新功能和测试
   - 创建自己的类和测试

## 核心知识点

### JUnit 5 必会
- `@Test` - 标记测试方法
- `@BeforeEach` / `@AfterEach` - 测试前后执行
- `assertEquals()` / `assertTrue()` - 基本断言
- `assertThrows()` - 异常断言
- `@ParameterizedTest` - 参数化测试

### Mockito 必会
- `@Mock` - 创建Mock对象
- `@InjectMocks` - 注入Mock依赖
- `when().thenReturn()` - 定义行为
- `verify()` - 验证调用
- `any()` / `eq()` - 参数匹配器

## 常用命令

### Gradle 命令

```bash
# 编译项目
gradle build

# 运行测试
gradle test

# 跳过测试构建
gradle build -x test

# 查看依赖树
gradle dependencies

# 清理项目
gradle clean

# 持续测试（文件修改时自动运行）
gradle test --continuous
```

## 测试最佳实践

1. **命名规范**
   - 测试方法名要描述测试内容
   - 使用 @DisplayName 增强可读性

2. **AAA模式**
   - Arrange (准备)
   - Act (执行)
   - Assert (断言)

3. **隔离原则**
   - 每个测试独立运行
   - 不依赖测试执行顺序
   - 使用Mock隔离外部依赖

4. **覆盖率**
   - 追求有意义的覆盖率
   - 重点测试业务逻辑
   - 异常路径要覆盖

## 进阶学习

完成基础示例后，可以尝试：

1. 添加 Spring Boot Test 依赖
2. 学习 @WebMvcTest 和 MockMvc
3. 学习 @DataJpaTest 测试数据层
4. 实践 TDD (测试驱动开发)

## 参考资源

- [JUnit 5 官方文档](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito 官方文档](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [学习计划文档](./JUnit-Mockito-一天速成计划.md)
