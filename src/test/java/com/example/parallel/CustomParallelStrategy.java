package com.example.parallel;

import org.junit.platform.engine.ConfigurationParameters;
import org.junit.platform.engine.support.hierarchical.ParallelExecutionConfiguration;
import org.junit.platform.engine.support.hierarchical.ParallelExecutionConfigurationStrategy;

import java.util.concurrent.ForkJoinPool;
import java.util.function.Predicate;

/**
 * 自定义并行执行策略
 * 可以根据具体需求调整并行度和执行策略
 */
public class CustomParallelStrategy implements ParallelExecutionConfigurationStrategy {

    @Override
    public ParallelExecutionConfiguration createConfiguration(ConfigurationParameters configurationParameters) {
        return new CustomParallelExecutionConfiguration();
    }

    /**
     * 自定义并行执行配置
     */
    static class CustomParallelExecutionConfiguration implements ParallelExecutionConfiguration {

        @Override
        public int getParallelism() {
            // 自定义并行度计算
            int processors = Runtime.getRuntime().availableProcessors();
            // 例如：使用处理器数量的75%
            return Math.max(1, (int) (processors * 0.75));
        }

        @Override
        public int getMinimumRunnable() {
            // 最小可运行任务数
            return 1;
        }

        @Override
        public int getMaxPoolSize() {
            // 最大线程池大小
            int processors = Runtime.getRuntime().availableProcessors();
            // 例如：处理器数量的2倍
            return processors * 2;
        }

        @Override
        public int getCorePoolSize() {
            // 核心线程池大小
            return getParallelism();
        }

        @Override
        public int getKeepAliveSeconds() {
            // 线程空闲保持时间（秒）
            return 30;
        }

        @Override
        public Predicate<? super ForkJoinPool> getSaturatePredicate() {
            // 饱和判断条件
            return pool -> pool.getQueuedTaskCount() > 100;
        }
    }
}