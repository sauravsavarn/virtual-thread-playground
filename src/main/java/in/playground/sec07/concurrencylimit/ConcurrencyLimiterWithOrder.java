package in.playground.sec07.concurrencylimit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.*;

/**
 * Since we know that executor service when of type is VirtualThreadPerTaskExecutor this do not manage any queue
 * by the ExecutorService internally for task execution hence the task is not performed in Order like task1 first executed
 * followed by task2 and so on.
 *
 * To make the task executed in order when used VirtualThreadPerTaskExecutor or newThreadPerTaskExecutor
 * we are maintaing a queue ourself here and instead of submitting task to the executorService directly,
 * add the Task to the queue and then poll the queue to and give task to the executorService to execute.
 * Thus in this way we will execute the task in order one by one.
 */
public class ConcurrencyLimiterWithOrder implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(ConcurrencyLimiterWithOrder.class);
    private final ExecutorService executor;
    private final Semaphore semaphore;
    private final Queue<Callable<?>> queue;

    /**
     *
     * @param executor
     * @param limit
     */
    public ConcurrencyLimiterWithOrder(ExecutorService executor, int limit) {
        this.executor = executor;
        this.semaphore = new Semaphore(limit);
        this.queue = new ConcurrentLinkedQueue<>();
    }

    /**
     *
     * @param callable
     * @param <T>
     * @return
     */
    public <T> Future<T> submit(Callable<T> callable) {
        this.queue.add(callable);
        return executor.submit(() -> executeTask());
    }

    /**
     *
     * @return
     * @param <T>
     */
    private <T> T executeTask() {

        try {
            semaphore.acquire();
            return (T) this.queue.poll().call(); // first poll the queue and then call the callable.
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            /// /also in case of any exception we are adding back the callable to the queue, such that this will
            /// /be executed next time.

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
