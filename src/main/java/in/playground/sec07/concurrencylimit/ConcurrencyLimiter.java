package in.playground.sec07.concurrencylimit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

/**
 *
 */
public class ConcurrencyLimiter implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(ConcurrencyLimiter.class);
    private final ExecutorService executor;
    private final Semaphore semaphore;

    /**
     *
     * @param executor
     * @param limit
     */
    public ConcurrencyLimiter(ExecutorService executor, int limit) {
        this.executor = executor;
        this.semaphore = new Semaphore(limit);
    }

    /**
     *
     * @param callable
     * @param <T>
     * @return
     */
    public <T> Future<T> submit(Callable<T> callable) {
        return executor.submit(() -> wrapCallable(callable));
    }

    /**
     *
     * @param callable
     * @param <T>
     * @return
     */
    private <T> T wrapCallable(Callable<T> callable) {
        try {
            semaphore.acquire();
            return callable.call();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            semaphore.release();
        }

        return null;
    }

    /**
     *
     */
    @Override
    public void close() {
        this.executor.close();
    }
}
