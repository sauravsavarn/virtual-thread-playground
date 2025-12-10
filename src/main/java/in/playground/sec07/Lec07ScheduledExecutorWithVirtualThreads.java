package in.playground.sec07;

import in.playground.externalservice.PlaygroundClient;
import in.playground.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static in.playground.externalservice.PlaygroundClient.readAPI;

/**
 *
 */
public class Lec07ScheduledExecutorWithVirtualThreads {
    private static final Logger logger = LoggerFactory.getLogger(Lec07ScheduledExecutorWithVirtualThreads.class);

    static void main(String[] args) {
        scheduled();
    }

    private static void scheduled() {
        var scheduler = Executors.newSingleThreadScheduledExecutor();
        var executor = Executors.newVirtualThreadPerTaskExecutor();

        try(scheduler; executor) { //NOTE: try-with-resources can handle & manage many auto-closeable impl.
            scheduler.scheduleAtFixedRate(() -> { //this will handle scheduling and this is the PlatformThreads pooled which offloads task execution the Virtual Threads. This scheduler will schedule every 3 seconds but this will not run the task instead task execution is the role of the Virtual Threads spawned by the Platform Thread.
                executor.execute(() -> printEmployeeInfo(1));
            }, 0, 3, TimeUnit.SECONDS);

            CommonUtils.sleep(Duration.ofSeconds(5));
        }

    }

    /**
     * assume this is 3rd party service to call and fetch the emp info. Thus, this is blocking I/O call.
     * @param id
     */
    private static void printEmployeeInfo(int id) {
        logger.info("Thread {} executing Employee with id: {} has details as {}", Thread.currentThread(), id, PlaygroundClient.prettyPrint(readAPI(PlaygroundClient.getBASE_URL()+ "/employee/" + id)));
    }

}
