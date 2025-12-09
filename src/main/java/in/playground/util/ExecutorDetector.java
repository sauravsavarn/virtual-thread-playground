package in.playground.util;

import java.util.concurrent.*;

public class ExecutorDetector {

    public enum ExecutorType {
        SINGLE_THREAD,
        FIXED_THREAD_POOL,
        CACHED_THREAD_POOL,
        FORK_JOIN_POOL,
        VIRTUAL_THREAD_PER_TASK,
        SCHEDULED_THREAD_POOL,
        UNKNOWN
    }

    public static ExecutorType detect(ExecutorService es) {

        // 1. ForkJoinPool
        if (es instanceof ForkJoinPool) {
            return ExecutorType.FORK_JOIN_POOL;
        }

        // 2. ScheduledExecutor
        if (es instanceof ScheduledExecutorService) {
            return ExecutorType.SCHEDULED_THREAD_POOL;
        }

        // 3. Try unwrap underlying executor
        ThreadPoolExecutor tpe = unwrapTPE(es);

        if (tpe != null) {
            int core = tpe.getCorePoolSize();
            int max = tpe.getMaximumPoolSize();
            long keepAlive = tpe.getKeepAliveTime(TimeUnit.MILLISECONDS);

            if (core == 1 && max == 1) {
                return ExecutorType.SINGLE_THREAD;
            }
            if (core == max && keepAlive == 0) {
                return ExecutorType.FIXED_THREAD_POOL;
            }
            if (core == 0 && max == Integer.MAX_VALUE) {
                return ExecutorType.CACHED_THREAD_POOL;
            }
        }

        // 4. Virtual thread
        if (isVirtualThreadExecutor(es)) {
            return ExecutorType.VIRTUAL_THREAD_PER_TASK;
        }

        return ExecutorType.UNKNOWN;
    }

    private static ThreadPoolExecutor unwrapTPE(ExecutorService es) {
        if (es instanceof ThreadPoolExecutor) {
            return (ThreadPoolExecutor) es;
        }

        // Try reflection (auto-shutdown wrapper around TPE)
        try {
            for (var field : es.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object inner = field.get(es);
                if (inner instanceof ThreadPoolExecutor) {
                    return (ThreadPoolExecutor) inner;
                }
            }
        } catch (Exception ignored) {}

        return null;
    }

    private static boolean isVirtualThreadExecutor(ExecutorService es) {
        String name = es.getClass().getName();
        return name.contains("VirtualThread");
    }
}
