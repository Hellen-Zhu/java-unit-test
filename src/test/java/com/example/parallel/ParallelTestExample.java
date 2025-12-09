package com.example.parallel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.ResourceAccessMode;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 并行执行示例
 * 演示不同的并行执行模式和配置
 */
@DisplayName("并行测试示例")
@Execution(ExecutionMode.CONCURRENT)  // 类级别并发执行
public class ParallelTestExample {

    // 用于统计并发执行的计数器
    private static final AtomicInteger concurrentCounter = new AtomicInteger(0);
    private static final ConcurrentHashMap<String, String> threadMap = new ConcurrentHashMap<>();

    @Test
    @DisplayName("并发测试 1 - 模拟耗时操作")
    void testConcurrent1() throws InterruptedException {
        String threadName = Thread.currentThread().getName();
        threadMap.put("test1", threadName);

        int currentCount = concurrentCounter.incrementAndGet();
        System.out.printf("[%s] 测试1开始执行，当前并发数: %d%n",
            threadName, currentCount);

        // 模拟耗时操作
        Thread.sleep(1000);

        concurrentCounter.decrementAndGet();
        System.out.printf("[%s] 测试1执行完成%n", threadName);

        assertTrue(true);
    }

    @Test
    @DisplayName("并发测试 2 - 模拟计算密集型操作")
    void testConcurrent2() {
        String threadName = Thread.currentThread().getName();
        threadMap.put("test2", threadName);

        int currentCount = concurrentCounter.incrementAndGet();
        System.out.printf("[%s] 测试2开始执行，当前并发数: %d%n",
            threadName, currentCount);

        // 模拟计算密集型操作
        long sum = 0;
        for (int i = 0; i < 100_000_000; i++) {
            sum += i;
        }

        concurrentCounter.decrementAndGet();
        System.out.printf("[%s] 测试2执行完成，计算结果: %d%n", threadName, sum);

        assertTrue(sum > 0);
    }

    @Test
    @DisplayName("并发测试 3 - 快速执行测试")
    void testConcurrent3() {
        String threadName = Thread.currentThread().getName();
        threadMap.put("test3", threadName);

        int currentCount = concurrentCounter.incrementAndGet();
        System.out.printf("[%s] 测试3开始执行，当前并发数: %d%n",
            threadName, currentCount);

        // 快速执行的测试
        String result = "Hello" + " " + "World";

        concurrentCounter.decrementAndGet();
        System.out.printf("[%s] 测试3执行完成%n", threadName);

        assertEquals("Hello World", result);
    }

    @RepeatedTest(value = 5, name = "重复测试 {currentRepetition}/{totalRepetitions}")
    @DisplayName("并发重复测试")
    void testRepeatedConcurrent() {
        String threadName = Thread.currentThread().getName();

        int currentCount = concurrentCounter.incrementAndGet();
        System.out.printf("[%s] 重复测试执行中，当前并发数: %d%n",
            threadName, currentCount);

        // 执行一些测试逻辑
        assertNotNull(threadName);

        concurrentCounter.decrementAndGet();
    }

    /**
     * 使用 @Execution 注解控制单个测试方法的执行模式
     * SAME_THREAD 表示在同一线程中顺序执行
     */
    @Test
    @DisplayName("同步执行测试（不参与并发）")
    @Execution(ExecutionMode.SAME_THREAD)
    void testSynchronous() {
        String threadName = Thread.currentThread().getName();
        System.out.printf("[%s] 同步测试执行%n", threadName);

        // 这个测试将在同一线程中执行，不会与其他测试并发
        assertDoesNotThrow(() -> {
            Thread.sleep(500);
        });
    }

    /**
     * 使用 @ResourceLock 注解确保对共享资源的独占访问
     * 具有相同资源锁的测试不会并发执行
     */
    @Test
    @DisplayName("资源锁测试 1 - 访问共享资源")
    @ResourceLock(value = "SHARED_RESOURCE", mode = ResourceAccessMode.READ_WRITE)
    void testWithResourceLock1() {
        String threadName = Thread.currentThread().getName();
        System.out.printf("[%s] 资源锁测试1 - 独占访问共享资源%n", threadName);

        // 模拟访问共享资源
        assertDoesNotThrow(() -> Thread.sleep(1000));
    }

    @Test
    @DisplayName("资源锁测试 2 - 访问共享资源")
    @ResourceLock(value = "SHARED_RESOURCE", mode = ResourceAccessMode.READ_WRITE)
    void testWithResourceLock2() {
        String threadName = Thread.currentThread().getName();
        System.out.printf("[%s] 资源锁测试2 - 独占访问共享资源%n", threadName);

        // 模拟访问共享资源
        assertDoesNotThrow(() -> Thread.sleep(1000));
    }

    @Test
    @DisplayName("只读资源锁测试 1")
    @ResourceLock(value = "READ_ONLY_RESOURCE", mode = ResourceAccessMode.READ)
    void testWithReadLock1() {
        String threadName = Thread.currentThread().getName();
        System.out.printf("[%s] 只读锁测试1 - 可以并发读取%n", threadName);

        assertDoesNotThrow(() -> Thread.sleep(500));
    }

    @Test
    @DisplayName("只读资源锁测试 2")
    @ResourceLock(value = "READ_ONLY_RESOURCE", mode = ResourceAccessMode.READ)
    void testWithReadLock2() {
        String threadName = Thread.currentThread().getName();
        System.out.printf("[%s] 只读锁测试2 - 可以并发读取%n", threadName);

        assertDoesNotThrow(() -> Thread.sleep(500));
    }

    /**
     * 测试执行时间，验证并行执行的效果
     */
    @Test
    @DisplayName("性能对比测试")
    void testPerformance() {
        Instant start = Instant.now();

        // 如果是串行执行，这些测试总共需要约3秒
        // 如果是并行执行，应该在1秒左右完成
        System.out.println("\n=== 执行时间统计 ===");
        System.out.println("如果启用并行执行，多个测试应该同时运行");
        System.out.println("检查不同测试使用的线程名称：");
        threadMap.forEach((test, thread) ->
            System.out.printf("%s -> %s%n", test, thread));

        Duration duration = Duration.between(start, Instant.now());
        System.out.printf("当前测试执行时间: %d ms%n", duration.toMillis());
        System.out.println("==================\n");

        assertTrue(true);
    }
}