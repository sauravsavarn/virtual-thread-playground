package in.playground.sec07;

import in.playground.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *  ExecutorService now extends AutoCloseable.
 *
 */
public class Lec01AutoClosable {
    private static final Logger logger = LoggerFactory.getLogger(Lec01AutoClosable.class);

    static void main(String[] args) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(Lec01AutoClosable::task);
        logger.info("task submitted");
        executorService.shutdown(); //no new task will be accepting and it will complete the task already submitted. i.e. wait for the existing task to complete.

        ///  NOTE:: above example for ExecutorService is for a short-lived application and cannot exit automatically
        ///         But since now ExecutorService extends AutoCloseable, we can re-write the above program using try-with-resources.

        try(var executorServ = Executors.newSingleThreadExecutor()) {
            executorServ.submit(Lec01AutoClosable::task);
            logger.info("task submitted");
        } catch (Exception e) {
            logger.error("task failed", e);
            throw new RuntimeException(e);
        }
        /// / Now when this 2nd way of ExecutorService run and all task is submitted, ExecutorService will exit automatically.


    }

    /**
     * Below method 'task' is created to Simulate I/O Task.
     */
    private static void task() {
        CommonUtils.sleep(Duration.ofSeconds(1));
        logger.info("task executed");
    }

}
