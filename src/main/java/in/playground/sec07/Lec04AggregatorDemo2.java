package in.playground.sec07;

import in.playground.sec07.aggregator.AggregatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * this aggregator demo uses IntStream to call the api for range of 1 to 50
 */
public class Lec04AggregatorDemo2 {
    private static final Logger logger = LoggerFactory.getLogger(Lec04AggregatorDemo2.class);

    static void main(String[] args) {
        //
        var executorService = Executors.newVirtualThreadPerTaskExecutor();
        var aggregator = new AggregatorService(executorService);

        var listOfEmps = IntStream.rangeClosed(1, 50)
                .mapToObj(counter -> {
                    try {
                        return aggregator.getLAndD(String.valueOf(counter));
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
        //now loop and call to print the list
        logger.info("List of Employees : {}", listOfEmps);

        /// /NOTE: here we are also not using try-with-resource because for the Virtual Thread Executor there are
        /// /      no platform threads as part of this Executor. Thus, we have only carrier Threads and these
        /// /      are like daemon threads so they will just run and it will exit. Thus, explicitly shutdown or
        /// /      shutdownnow() or nor the ExecutorService within the try-with-resources is not required and thus
        /// /      program will stop once logger.info at line 34 is printed automatically.


    }


}
