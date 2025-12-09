package com.example;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Calculator测试类 - 展示JUnit 5的核心功能
 */
@DisplayName("计算器测试")
class CalculatorTest {

    private Calculator calculator;

    @BeforeAll
    static void initAll() {
        System.out.println("开始执行所有测试");
    }

    @BeforeEach
    void init() {
        calculator = new Calculator();
        System.out.println("初始化Calculator实例");
    }

    @Test
    @DisplayName("测试加法运算")
    void testAdd() {
        // Given
        int a = 5;
        int b = 3;

        // When
        int result = calculator.add(a, b);

        // Then
        assertEquals(8, result, "5 + 3 应该等于 8");
    }

    @Test
    @DisplayName("测试减法运算")
    void testSubtract() {
        assertEquals(2, calculator.subtract(5, 3));
        assertEquals(-2, calculator.subtract(3, 5));
        assertEquals(0, calculator.subtract(5, 5));
    }

    @Test
    @DisplayName("测试乘法运算")
    void testMultiply() {
        assertAll("乘法测试组",
            () -> assertEquals(15, calculator.multiply(3, 5)),
            () -> assertEquals(0, calculator.multiply(0, 5)),
            () -> assertEquals(-15, calculator.multiply(-3, 5))
        );
    }

    @Test
    @DisplayName("测试除法运算")
    void testDivide() {
        assertEquals(2.0, calculator.divide(10, 5), 0.001);
        assertEquals(3.333, calculator.divide(10, 3), 0.001);
    }

    @Test
    @DisplayName("测试除零异常")
    void testDivideByZero() {
        // 测试抛出异常
        ArithmeticException exception = assertThrows(
            ArithmeticException.class,
            () -> calculator.divide(10, 0),
            "除以零应该抛出ArithmeticException"
        );

        // 验证异常消息
        assertEquals("Division by zero", exception.getMessage());
    }

    @ParameterizedTest(name = "{0}的平方应该是{1}")
    @CsvSource({
        "1, 1",
        "2, 4",
        "3, 9",
        "4, 16",
        "5, 25"
    })
    @DisplayName("参数化测试平方运算")
    void testSquareParameterized(int input, int expected) {
        assertEquals(expected, calculator.square(input));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, 1.0, 4.0, 9.0, 16.0, 25.0})
    @DisplayName("测试平方根运算")
    void testSqrt(double number) {
        double result = calculator.sqrt(number);
        assertEquals(number, result * result, 0.001);
    }

    @Test
    @DisplayName("测试负数平方根异常")
    void testSqrtNegative() {
        assertThrows(
            IllegalArgumentException.class,
            () -> calculator.sqrt(-1),
            "负数平方根应该抛出IllegalArgumentException"
        );
    }

    @Test
    @DisplayName("测试超时")
    void testTimeout() {
        assertTimeout(Duration.ofMillis(100), () -> {
            // 模拟一个快速操作
            calculator.add(1, 1);
        });
    }

    @Test
    @Disabled("暂时禁用这个测试")
    @DisplayName("被禁用的测试")
    void testDisabled() {
        fail("这个测试不应该被执行");
    }

    @Nested
    @DisplayName("嵌套测试类")
    class NestedTest {

        @Test
        @DisplayName("嵌套测试方法")
        void testNested() {
            assertTrue(calculator.add(1, 1) > 0);
        }
    }

    @AfterEach
    void tearDown() {
        System.out.println("清理测试环境");
    }

    @AfterAll
    static void tearDownAll() {
        System.out.println("所有测试执行完成");
    }
}