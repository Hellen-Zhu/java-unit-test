#!/bin/bash

echo "=== JUnit 5 并行执行测试演示 ==="
echo ""

# 清理之前的构建
./gradlew clean

echo "运行并行测试..."
echo ""

# 运行测试并过滤输出
./gradlew test --tests "*ParallelTestExample" 2>&1 | grep -E "测试.*执行|并发数|线程|执行时间|PASSED|FAILED" || true

echo ""
echo "=== 测试完成 ==="
echo ""
echo "注意观察："
echo "1. 不同测试使用了不同的线程 (ForkJoinPool-1-worker-X)"
echo "2. 并发计数器显示多个测试同时执行"
echo "3. 资源锁测试按顺序执行，而其他测试并发执行"
echo ""
echo "如需查看完整报告，请访问: build/reports/tests/test/index.html"