package in.playground.sec07;

import in.playground.util.CommonUtils;
import in.playground.util.ExecutorDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class Lec02ExecutorServiceTypes {
    private static final Logger logger = LoggerFactory.getLogger(Lec02ExecutorServiceTypes.class);

    static void main(String[] args) {

        //execute(Executors.newSingleThreadExecutor(), 3);
        /// NOTE: this SingleThreadExecutor will run the Task one-by-one i.e. if when first task started and ended then only the 2nd or later tasks will run one after another.


        //execute(Executors.newFixedThreadPool(3), 10);
        /// NOTE: this will initialize pool of 3 threads to execute task in random fashion.


        //execute(Executors.newCachedThreadPool(), 20);
        /// NOTE: this will initialise 'n' number of Platform Threads - here n is 20. and depending on which platform thread is scheduled by the OS scheduler will run.
        /// Also we can see as per below logs output ::
        ///    Task started: 5. Thread Info: Thread[#30,pool-1-thread-5,5,main]
        ///    Task finished: 5. Thread Info: Thread[#30,pool-1-thread-5,5,main]
        /// this signifies that Task '5' is started by 'pool-1-thread-5' thread & finished by the same platform thread which is 'pool-1-thread-5'.
        /// Also, we can NOTE here as since taskCount==20, thus total number of Thread in the ThreadPool initialized == taskCount which is 20 here for this example.


        //execute(Executors.newVirtualThreadPerTaskExecutor(), 200);
        // NOTE: this will create 200 virtual-thread(s).
        //execute(Executors.newThreadPerTaskExecutor(Thread.ofVirtual().factory()), 5);
        // NOTE: there is also 1 convenient method - newThreadPerTaskExecutor() -
        //       Then what is the difference between newVirtualThreadPerTaskExecutor() v/s newThreadPerTaskExecutor?
        //       there is not much difference as - newVirtualThreadPerTaskExecutor() internally calls newThreadPerTaskExecutor() method only as -
        //       public static ExecutorService newVirtualThreadPerTaskExecutor() {
        //          ThreadFactory factory = Thread.ofVirtual().factory();
        //          return newThreadPerTaskExecutor(factory);
        //       }

        // scheduled - ExecutorService is slightly different that all above discussed.
        scheduled();

    }

    /**
     * showcase scheduled - using SingleThreadScheduledExecutor.
     * this is used to schedule a task periodically . here we can schedule at fixed-delay or at fixed-rate.
     */
    private static void scheduled() {
        try (var executorService = Executors.newSingleThreadScheduledExecutor()) {
            executorService.scheduleAtFixedRate(() -> {
                logger.info("scheduled - executing task");
            }, 0, 1, TimeUnit.SECONDS);

            /// sleep used otherwise this program will stop immediately
            CommonUtils.sleep(Duration.ofSeconds(5));
        }
    }

    /**
     * the idea here is that we will be passing different-different ExecutorService Impl. and then we are going to see
     * how they are all working. thus when this function exits post doing the task - ExecutorService will automatically stop.
     * <p>
     * <p>
     * Since ExecutorService extends AutoCloseable -  thus using try-with-resources.
     *
     * @param executor
     * @param taskCount
     */
    private static void execute(ExecutorService executor, int taskCount) {

        // detect type of ExecutorService started like single-thread-executor or fixed-thread-pool or chached-thread-pool or thread-per-task-executor, etc.
        logger.info("\n\n =================================================\nExecutorService type: {} ", ExecutorDetector.detect(executor));

        try (executor) {
            for (int i = 1; i <= taskCount; i++) {
                int finalI = i;
                executor.submit(() -> ioTask(finalI));
            }
            logger.info("task submitted"); ///logging to know that the for-loop block is completed.
        }
    }


    /**
     * to simulate a time-consuming operation.
     *
     * @param i
     */
    private static void ioTask(int i) {
        logger.info("Task started: {}. Thread Info: {} ", i, Thread.currentThread());
        CommonUtils.sleep(Duration.ofSeconds(5));
        logger.info("Task finished: {}. Thread Info: {} ", i, Thread.currentThread());
    }

}
