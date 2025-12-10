package in.playground.sec07.aggregator;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import in.playground.externalservice.PlaygroundClient;
import in.playground.model.Course;
import in.playground.model.Employee;
import in.playground.model.L_AND_D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import static in.playground.externalservice.PlaygroundClient.readAPI;

public class AggregatorService {
    private static final Logger logger = LoggerFactory.getLogger(AggregatorService.class);
    private final ExecutorService executorService;
    public AggregatorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    /**
     * aggregator service method that aggregates and finally return the Employee & the course he is associated with based on the passed employee_id.
     *
     * @param empId
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public L_AND_D getLAndD(String empId) throws ExecutionException, InterruptedException {
        var employee = executorService.submit(() -> PlaygroundClient.prettyPrint(readAPI(PlaygroundClient.getBASE_URL()+ "/employee/" + empId)));
        var course = executorService.submit(() -> PlaygroundClient.prettyPrint(readAPI(PlaygroundClient.getBASE_URL()+ "/course?empId=" + empId)));


        /// /get the Employee Object
        Employee employeeObject = new Gson().fromJson(employee.get(), Employee.class);

        /// /get the Course Object. We are using JsonElement as we can get 1 course as jsonObject or multiple course as array of course objects.
        JsonElement element = JsonParser.parseString(course.get());
        List<Course> coursesObject;
        if (element.isJsonArray()) {
            coursesObject = new Gson().fromJson(element, new TypeToken<List<Course>>(){}.getType());
        } else {
            Course single = new Gson().fromJson(element, Course.class);
            coursesObject = List.of(single);
        }

        //return new L_AND_D(empId, new Gson().fromJson(employee.get(), Employee.class), new Gson().fromJson(course.get(), Course.class));
        return new L_AND_D(empId, employeeObject, coursesObject);
    }
}
