package com.evil.framework.core.support;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 提供lambda表达式通用方法
 *
 * @author  kongzheng
 * @since 1.0.0.RELEASE
 */
public class LambdaSupport {

    /**
     * 获取对象
     *
     * @param supplier 对象生产者
     * @param <T>      返回值对象类型
     * @return supplier.get()
     */
    public static <T> T supplyIfNotExistThrowException(Supplier<T> supplier) {
        return Optional.ofNullable(supplier.get()).orElseThrow(() -> new IllegalStateException("不存在的值"));
    }

    /**
     * 如果给定的处理对象 t ,符合断言predicate逻辑，则执行consumer消费逻辑
     *
     * @param t         处理对象
     * @param predicate 是否匹配逻辑
     * @param consumer  消费处理逻辑
     * @param <T>       处理对象类型
     */
    public static <T> void consumerIfPredicate(T t, Predicate<T> predicate, Consumer<T> consumer) {
        if (predicate.test(t)) {
            consumer.accept(t);
        }
    }

}
