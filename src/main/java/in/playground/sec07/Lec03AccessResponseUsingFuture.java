package in.playground.sec07;

import in.playground.externalservice.PlaygroundClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static in.playground.externalservice.PlaygroundClient.*;

/**
 *
 */
public class Lec03AccessResponseUsingFuture {
    private static final Logger logger = LoggerFactory.getLogger(Lec03AccessResponseUsingFuture.class);


    static void main(String[] args) throws ExecutionException, InterruptedException {
        //
        try(var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            /// //// get a single employee information having _id = 1.
            Future<?> future = executor.submit(() -> logger.info("response received while fetching information of employee having _id: {} is => :: {}", 1, PlaygroundClient.prettyPrint(readAPI(getBASE_URL()+ "/employee/1"))));
            future.get();

            /// //// get all employee information
            Future<?> futureAllEmps = executor.submit(() -> logger.info("response received while fetching information of all employees is => :: {}", prettyPrint(readAPI(getBASE_URL() + "/employee/"))));
            futureAllEmps.get();

        }
    }
}
