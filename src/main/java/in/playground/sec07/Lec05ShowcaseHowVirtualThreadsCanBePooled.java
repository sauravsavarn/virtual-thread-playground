package in.playground.sec07;

import in.playground.externalservice.PlaygroundClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static in.playground.externalservice.PlaygroundClient.readAPI;

/**
 * NOTE: as per Java Doc, Virtual Threads are not supposed to be pooled. This example showcase how though
 *       this can be pooled.
 *
 *
 * NOTE: running this we get output something like as below ---
 *      [virtual-thread1] INFO in.playground.externalservice.PlaygroundClient - reading API URL: https://692ed47e91e00bafccd58716.mockapi.io/api/v1//employee/1
 *      [virtual-thread1] INFO in.playground.sec07.Lec05ShowcaseHowVirtualThreadsCanBePooled - Employee with id: 1 has details as {
         *   "createdAt": "2025-12-02T04:47:57.348Z",
         *   "name": "Naomi Leffler",
         *   "address": "address 1",
         *   "email": "email 1",
         *   "id": "1"
         * }
 *      [virtual-thread1] INFO in.playground.externalservice.PlaygroundClient - reading API URL: https://692ed47e91e00bafccd58716.mockapi.io/api/v1//employee/5
 *
 *      thus from above output we can see that at first - virtual-thread1 calls to readAPI for empId = 1
 *      then once this prints output as per line 19, then
 *      it again call (i mean same virtual thread 'virtual-thread1') to fetch employee with empId = 5
 *      Thus from this it is evident that the thread is into ThreadPool and the same thread once completed the task takes another task and execute.
 *
 *      NOTE:: this is against the docs and VirtualThread should not be pooled instead it should dispose once the assigned Task to that VirtualThread is completed.
 *
 *
 */
public class Lec05ShowcaseHowVirtualThreadsCanBePooled {
    private static final Logger logger = LoggerFactory.getLogger(Lec05ShowcaseHowVirtualThreadsCanBePooled.class);

    static void main(String[] args) {
        var factory = Thread.ofVirtual().name("virtual-thread", 1).factory();
        execute(Executors.newFixedThreadPool(3, factory), 20); // passing factory which here is VirtualThread Factory.
    }

    private static void execute(ExecutorService executorService, int taskCount) {
        try(executorService) {
            for (int i = 1; i < taskCount; i++) {
                int finalI = i;
                executorService.execute(() -> printEmployeeInfo(finalI));
            }
        }
    }

    /**
     * assume this is 3rd party service to call and fetch the emp info. Thus, this is blocking I/O call.
     * @param id
     */
    private static void printEmployeeInfo(int id) {
       logger.info("Employee with id: {} has details as {}", id, PlaygroundClient.prettyPrint(readAPI(PlaygroundClient.getBASE_URL()+ "/employee/" + id)));
    }

}
