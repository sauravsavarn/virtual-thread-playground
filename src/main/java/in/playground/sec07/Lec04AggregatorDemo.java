package in.playground.sec07;

import in.playground.sec07.aggregator.AggregatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class Lec04AggregatorDemo {
    private static final Logger logger = LoggerFactory.getLogger(Lec04AggregatorDemo.class);

    static void main(String[] args) throws ExecutionException, InterruptedException {

        //
        var executorService = Executors.newVirtualThreadPerTaskExecutor();
        /// /NOTE: here we are not using try-with-resource because for the Virtual Thread Executor there are
        /// /      no platform threads as part of this Executor. Thus, we have only carrier Threads and these
        /// /      are like daemon threads so they will just run and it will exit. Thus, explicitly shutdown or
        /// /      shutdownnow() or nor the ExecutorService within the try-with-resources is not required.
        var aggregator = new AggregatorService(executorService);

        logger.info("Employee {} L&D associated with {} ", 51, aggregator.getLAndD("51"));
    }

}
