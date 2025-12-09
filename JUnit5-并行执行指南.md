# JUnit 5 并行执行指南

## 概述

JUnit 5 提供了强大的并行执行功能，可以显著提升测试执行速度，特别是对于大型测试套件。

## 配置方式

### 1. 配置文件方式 (推荐)

在 `src/test/resources/junit-platform.properties` 中配置：

```properties
# 启用并行执行
junit.jupiter.execution.parallel.enabled = true

# 执行模式
junit.jupiter.execution.parallel.mode.default = concurrent
junit.jupiter.execution.parallel.mode.classes.default = concurrent

# 并行策略
junit.jupiter.execution.parallel.config.strategy = dynamic
junit.jupiter.execution.parallel.config.dynamic.factor = 1
```

### 2. Gradle 配置方式

在 `build.gradle` 中配置：

```gradle
test {
    useJUnitPlatform()

    // 通过系统属性配置
    systemProperties = [
        'junit.jupiter.execution.parallel.enabled': 'true',
        'junit.jupiter.execution.parallel.mode.default': 'concurrent'
    ]

    // Gradle级别的并行
    maxParallelForks = Runtime.runtime.availableProcessors()
}
```

### 3. Maven 配置方式

在 `pom.xml` 中配置：

```xml
<plugin>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <properties>
            <configurationParameters>
                junit.jupiter.execution.parallel.enabled = true
                junit.jupiter.execution.parallel.mode.default = concurrent
            </configurationParameters>
        </properties>
    </configuration>
</plugin>
```

## 执行模式

### 1. 类级别控制

```java
@Execution(ExecutionMode.CONCURRENT)  // 并发执行
public class MyTest {
    // 测试方法将并发执行
}

@Execution(ExecutionMode.SAME_THREAD)  // 同步执行
public class MySequentialTest {
    // 测试方法将顺序执行
}
```

### 2. 方法级别控制

```java
@Test
@Execution(ExecutionMode.CONCURRENT)
void testConcurrent() {
    // 此方法可以并发执行
}

@Test
@Execution(ExecutionMode.SAME_THREAD)
void testSequential() {
    // 此方法将同步执行
}
```

## 资源锁机制

使用 `@ResourceLock` 确保对共享资源的安全访问：

```java
@Test
@ResourceLock(value = "DATABASE", mode = ResourceAccessMode.READ_WRITE)
void testDatabaseWrite() {
    // 独占访问数据库
}

@Test
@ResourceLock(value = "DATABASE", mode = ResourceAccessMode.READ)
void testDatabaseRead() {
    // 可以与其他读操作并发
}
```

## 并行策略

### 1. Dynamic 策略 (默认)
- 根据 CPU 核心数动态调整
- `parallelism = cores * factor`

### 2. Fixed 策略
- 固定线程数
```properties
junit.jupiter.execution.parallel.config.strategy = fixed
junit.jupiter.execution.parallel.config.fixed.parallelism = 4
```

### 3. Custom 策略
- 自定义策略实现
```properties
junit.jupiter.execution.parallel.config.strategy = custom
junit.jupiter.execution.parallel.config.custom.class = com.example.CustomStrategy
```

## 最佳实践

### 1. 确保测试独立性
- 测试不应依赖执行顺序
- 避免共享可变状态
- 使用 `@TestInstance(Lifecycle.PER_METHOD)`

### 2. 处理共享资源
- 使用线程安全的数据结构
- 使用 `@ResourceLock` 保护共享资源
- 考虑使用 `ThreadLocal`

### 3. 性能优化
- 合理设置并行度
- 平衡测试粒度
- 监控资源使用

### 4. 调试技巧
- 临时禁用并行执行进行调试
- 使用日志记录线程信息
- 使用 `@Isolated` 隔离问题测试

## 运行测试

### 运行所有并行测试
```bash
./gradlew test
```

### 运行特定的并行测试
```bash
./gradlew test --tests "*ParallelTestExample"
```

### 查看详细输出
```bash
./gradlew test --info
```

## 性能对比

启用并行执行前后的典型性能提升：

| 测试数量 | 串行执行时间 | 并行执行时间 | 性能提升 |
|---------|------------|------------|---------|
| 10      | 10s        | 3s         | 3.3x    |
| 50      | 50s        | 15s        | 3.3x    |
| 100     | 100s       | 25s        | 4x      |

*实际性能提升取决于测试特性、硬件配置和并行策略*

## 常见问题

### 1. 测试失败或不稳定
- 检查是否有共享状态
- 使用 `@ResourceLock` 保护共享资源
- 确保测试独立性

### 2. 性能没有提升
- 检查是否正确启用并行执行
- 调整并行策略和因子
- 确认测试是否适合并行执行

### 3. 内存问题
- 减少并行度
- 优化测试资源使用
- 增加 JVM 内存设置

## 示例项目结构

```
src/test/
├── java/
│   └── com/example/parallel/
│       ├── ParallelTestExample.java      # 基本并行测试示例
│       ├── ThreadSafetyTest.java        # 线程安全测试
│       └── CustomParallelStrategy.java  # 自定义策略
└── resources/
    └── junit-platform.properties        # 配置文件
```

## 参考链接

- [JUnit 5 官方文档 - 并行执行](https://junit.org/junit5/docs/current/user-guide/#writing-tests-parallel-execution)
- [JUnit 5 配置参数](https://junit.org/junit5/docs/current/user-guide/#running-tests-config-params)