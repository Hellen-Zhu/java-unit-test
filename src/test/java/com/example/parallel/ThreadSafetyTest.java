package com.example.parallel;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 线程安全测试示例
 * 演示如何在并行测试中处理共享状态和线程安全问题
 */
@DisplayName("线程安全测试")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ThreadSafetyTest {

    // 线程安全的共享状态
    private static final AtomicInteger safeCounter = new AtomicInteger(0);
    private static final ConcurrentLinkedQueue<String> safeQueue = new ConcurrentLinkedQueue<>();

    // 非线程安全的共享状态（仅用于演示，实际应避免）
    private static int unsafeCounter = 0;
    private static final List<String> unsafeList = new ArrayList<>();

    @BeforeAll
    static void setUp() {
        System.out.println("=== 线程安全测试开始 ===");
    }

    @AfterAll
    static void tearDown() {
        System.out.println("\n=== 测试结果统计 ===");
        System.out.printf("安全计数器最终值: %d%n", safeCounter.get());
        System.out.printf("安全队列元素数: %d%n", safeQueue.size());
        System.out.printf("非安全计数器值: %d (可能不准确)%n", unsafeCounter);
        System.out.printf("非安全列表元素数: %d (可能不准确)%n", unsafeList.size());
        System.out.println("==================");
    }

    /**
     * 测试线程安全的原子操作
     */
    @RepeatedTest(10)
    @DisplayName("原子计数器测试")
    @Execution(ExecutionMode.CONCURRENT)
    void testAtomicCounter(RepetitionInfo repetitionInfo) {
        int value = safeCounter.incrementAndGet();
        System.out.printf("[%s] 原子计数器值: %d (重复 %d/%d)%n",
            Thread.currentThread().getName(),
            value,
            repetitionInfo.getCurrentRepetition(),
            repetitionInfo.getTotalRepetitions());

        // 验证计数器值在合理范围内
        assertTrue(value > 0 && value <= 100);
    }

    /**
     * 测试并发队列操作
     */
    @RepeatedTest(10)
    @DisplayName("并发队列测试")
    @Execution(ExecutionMode.CONCURRENT)
    void testConcurrentQueue(RepetitionInfo repetitionInfo) {
        String element = String.format("Element-%d-%s",
            repetitionInfo.getCurrentRepetition(),
            Thread.currentThread().getName());

        safeQueue.offer(element);

        System.out.printf("[%s] 添加元素到队列: %s%n",
            Thread.currentThread().getName(), element);

        // 验证元素已添加
        assertTrue(safeQueue.contains(element));
    }

    /**
     * 演示非线程安全操作的问题（不推荐在实际测试中使用）
     * 使用 @Isolated 注解确保测试类单独执行
     */
    @Test
    @DisplayName("非线程安全操作演示")
    @Execution(ExecutionMode.SAME_THREAD)  // 强制同步执行以避免问题
    void testUnsafeOperations() {
        // 这个测试演示了为什么需要线程安全
        for (int i = 0; i < 100; i++) {
            unsafeCounter++;
            unsafeList.add("item-" + i);
        }

        System.out.printf("[%s] 非安全操作完成，计数器: %d%n",
            Thread.currentThread().getName(), unsafeCounter);
    }

    /**
     * 测试同步集合
     */
    @RepeatedTest(5)
    @DisplayName("同步集合测试")
    @Execution(ExecutionMode.CONCURRENT)
    void testSynchronizedCollection() {
        List<String> syncList = Collections.synchronizedList(new ArrayList<>());

        // 并发添加元素
        for (int i = 0; i < 10; i++) {
            syncList.add(String.format("sync-%d-%s", i, Thread.currentThread().getName()));
        }

        System.out.printf("[%s] 同步列表大小: %d%n",
            Thread.currentThread().getName(), syncList.size());

        assertFalse(syncList.isEmpty());
    }

    /**
     * 测试线程局部变量
     */
    private static final ThreadLocal<Integer> threadLocal = ThreadLocal.withInitial(() -> 0);

    @RepeatedTest(5)
    @DisplayName("ThreadLocal 测试")
    @Execution(ExecutionMode.CONCURRENT)
    void testThreadLocal(RepetitionInfo repetitionInfo) {
        // 每个线程有自己的变量副本
        threadLocal.set(repetitionInfo.getCurrentRepetition() * 100);

        System.out.printf("[%s] ThreadLocal 值: %d%n",
            Thread.currentThread().getName(), threadLocal.get());

        // 验证ThreadLocal值
        assertEquals(repetitionInfo.getCurrentRepetition() * 100, threadLocal.get());

        // 清理ThreadLocal
        threadLocal.remove();
    }

    /**
     * 使用 @TestInstance 注解控制测试实例生命周期
     * PER_METHOD: 每个测试方法创建新实例（默认，线程安全）
     * PER_CLASS: 所有测试方法共享一个实例（需要注意线程安全）
     */
    @Nested
    @DisplayName("测试实例生命周期")
    @TestInstance(TestInstance.Lifecycle.PER_METHOD)
    class LifecycleTest {

        private int instanceCounter = 0;

        @RepeatedTest(3)
        @DisplayName("每方法新实例测试")
        @Execution(ExecutionMode.CONCURRENT)
        void testPerMethodLifecycle() {
            instanceCounter++;
            System.out.printf("[%s] 实例计数器: %d (应该始终为1)%n",
                Thread.currentThread().getName(), instanceCounter);

            // 每个测试方法都有新实例，所以计数器应该为1
            assertEquals(1, instanceCounter);
        }
    }

    /**
     * 演示如何使用断言来验证并发条件
     */
    @Test
    @DisplayName("并发断言测试")
    @Execution(ExecutionMode.CONCURRENT)
    void testConcurrentAssertions() {
        assertAll("并发断言组",
            () -> assertTrue(safeCounter.get() >= 0, "计数器应该非负"),
            () -> assertNotNull(safeQueue, "队列不应为空"),
            () -> assertDoesNotThrow(() -> {
                // 模拟可能抛出异常的并发操作
                Thread.sleep(100);
            }, "不应该抛出异常")
        );
    }
}