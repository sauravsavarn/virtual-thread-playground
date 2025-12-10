package in.playground.sec07;

import in.playground.externalservice.PlaygroundClient;
import in.playground.sec07.concurrencylimit.ConcurrencyLimiterWithOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;

import static in.playground.externalservice.PlaygroundClient.readAPI;

/**
 * this examples shows that though we have submitted all tasks in line 28-30 to execute but since the
 * semaphore as per line 23 - can only allow to acquire and execute 3 tasks to execute at a time. Also,
 * here since we have implemented custom queue with ConcurrencyLimiterWithOrder class - thus we will see
 * that our task will be executed in some order .
 */
public class Lec06ConcurrencyLimitWithSemaphoreInOrder {
    private static final Logger logger = LoggerFactory.getLogger(Lec06ConcurrencyLimitWithSemaphoreInOrder.class);

    static void main(String[] args) {
        //var concurrencyLimiter = new ConcurrencyLimiter(Executors.newVirtualThreadPerTaskExecutor(), 3);
        // we can use above commented code but the virtual thread will not have custom name. SO to give
        // name to the Virtual Thread - we are writing below code in lines 26 - 27
        var factory = Thread.ofVirtual().name("virtual-thread", 1).factory();
        var concurrencyLimiter = new ConcurrencyLimiterWithOrder(Executors.newThreadPerTaskExecutor(factory), 3);
        execute(concurrencyLimiter, 20);
    }

    private static void execute(ConcurrencyLimiterWithOrder concurrencyLimiter, int taskCount) {
        try(concurrencyLimiter) {
            for (int i = 1; i < taskCount; i++) {
                int finalI = i;
                concurrencyLimiter.submit(() -> printEmployeeInfo(finalI));
            }
        }
    }

    /**
     * assume this is 3rd party service to call and fetch the emp info. Thus, this is blocking I/O call.
     * @param id
     */
    private static String printEmployeeInfo(int id) {
        var employee = PlaygroundClient.prettyPrint(readAPI(PlaygroundClient.getBASE_URL() + "/employee/" + id));
        logger.info("Employee with id: {} has details as {}", id, employee);
        return employee;
    }

}
