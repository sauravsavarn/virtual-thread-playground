package in.playground.sec07;

import in.playground.externalservice.PlaygroundClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Gatherers;
import java.util.stream.IntStream;

import static in.playground.externalservice.PlaygroundClient.readAPI;

/**
 * Here requirement is that - for each employeeID we have to get the employee record, and we sent multiple concurrent request.
 *
 * This implementation examples uses MapConcurrent gatherer implementation
 * to limit concurrent I/O calls. this is alternative and best way to code
 * and avoid using semaphores. NOte: this requires java 24 and above.
 *
 * Also take an important note that it uses Virtual Thread under the hood which
 * is visible from the logs provided below -
 * ------------------------------------ logs copied from the console -----------------------------
 * [] INFO in.playground.externalservice.PlaygroundClient - reading API URL: https://692ed47e91e00bafccd58716.mockapi.io/api/v1//employee/1
 * [] INFO in.playground.externalservice.PlaygroundClient - reading API URL: https://692ed47e91e00bafccd58716.mockapi.io/api/v1//employee/2
 * [] INFO in.playground.externalservice.PlaygroundClient - reading API URL: https://692ed47e91e00bafccd58716.mockapi.io/api/v1//employee/0
 * [] INFO in.playground.sec07.Lec08MapConcurrent - Thread VirtualThread[#27]/runnable@ForkJoinPool-1-worker-3 executing Employee with id: 0 has details as {
 *   "Output": "NotFound"
 * }
 * [] INFO in.playground.sec07.Lec08MapConcurrent - Thread VirtualThread[#31]/runnable@ForkJoinPool-1-worker-2 executing Employee with id: 2 has details as {
 *   "createdAt": "2025-12-01T20:29:14.886Z",
 *   "name": "Dr. Ginger Jast",
 *   "address": "address 2",
 *   "email": "email 2",
 *   "id": "2"
 * }
 * [] INFO in.playground.sec07.Lec08MapConcurrent - Thread VirtualThread[#29]/runnable@ForkJoinPool-1-worker-1 executing Employee with id: 1 has details as {
 *   "createdAt": "2025-12-02T04:47:57.348Z",
 *   "name": "Naomi Leffler",
 *   "address": "address 1",
 *   "email": "email 1",
 *   "id": "1"
 * }
 * ------------------------------------ logs copied from the console -----------------------------
 */
public class Lec08MapConcurrent {
    private static final Logger logger = LoggerFactory.getLogger(Lec08MapConcurrent.class);
    private static final int MAX_CONCURRENCY = 3;

    static void main(String[] args) {
        IntStream.range(0, 50)
                .boxed()
                .gather(Gatherers.mapConcurrent(MAX_CONCURRENCY, currentIndex -> printEmployeeInfo(currentIndex)))//here we are making at max only 3 concurrent calls. Also, mapConcurrent is an implementation of Gatherers Interface.
                .forEach(System.out::println);

    }

    /**
     * assume this is 3rd party service to call and fetch the emp info. Thus, this is blocking I/O call.
     *
     * @param id
     */
    private static String printEmployeeInfo(int id) {
        final var employee = PlaygroundClient.prettyPrint(readAPI(PlaygroundClient.getBASE_URL() + "/employee/" + id));
        logger.info("Thread {} executing Employee with id: {} has details as {}", Thread.currentThread(), id, employee);
        return employee;
    }
}
