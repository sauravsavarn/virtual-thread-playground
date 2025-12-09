package in.playground.externalservice;

import com.google.gson.*;
import in.playground.model.Employee;
import in.playground.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * this class takes care for all CRUD HTTP Method call to mockapi.io projects 'EmployeeApp'.
 */
public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    private static final String BASE_URL = "https://692ed47e91e00bafccd58716.mockapi.io/api/v1/";
    private static final HttpClient httpClient = HttpClient.newHttpClient(); // Returns a new HttpClient with default settings.

    static void main(String[] args) throws InterruptedException, IOException {
        /* ************************************************* */
        /* ************************************************* */
        /* test read API */
        /* ************************************************* */
        /* ************************************************* */
        /// /read employee by id_
        logger.info("Fetch Employee by ID :: {} => \n{} ",  1, prettyPrint(readAPI(BASE_URL+ "/employee/1")));
        /// /read employee by name.
        logger.info("Fetch Employee by Name :: {} => \n{} ",  "Dr. Ginger Jast", prettyPrint(readAPI(BASE_URL+ "/employee/?name="+ CommonUtils.urlEncoderUTF8("Dr. Ginger Jast"))));
        /// /read employee by name - which might not be available.
        logger.info("Fetch Employee by Name :: {} => \n{} ",  "Cloud", prettyPrint(readAPI(BASE_URL+ "/employee/?name="+ CommonUtils.urlEncoderUTF8("Cloud"))));

        /* ************************************************* */
        /* ************************************************* */
        /* test delete API */
        /* ************************************************* */
        /* ************************************************* */
        /// /delete employee by id_
        //logger.info("Delete Employee by ID :: {} => \n{} ", 51, prettyPrint(deleteAPI(BASE_URL + "/employee/51")));

        /// /multi-delete employee. to help with multi-delete internally we are first fetching all Employee by name and then deleting one-by-one.
        //logger.info("Delete all Employee records by Name :: {} => \n{} ", "Cloud", prettyPrint(multiDelete(BASE_URL + "/employee/?name=" + CommonUtils.urlEncoderUTF8("Cloud"))));

        /* ************************************************* */
        /* ************************************************* */
        /* test create API */
        /* ************************************************* */
        /* ************************************************* */
        /// /create a new employee
        Employee employee = new Employee(LocalDateTime.now().toString(), "Cloud", "patliputra", "cloud@zohomail.in");
        logger.info("Create a new Employee :: => \n{}", prettyPrint(createAPI(BASE_URL + "/employee", new Gson().toJson(employee))));

        /* ************************************************* */
        /* ************************************************* */
        /* test update API */
        /* ************************************************* */
        /* ************************************************* */
        /// /update already existing Employee.
        Employee employeeUpdate = new Employee(LocalDateTime.now().toString(), "Cloud", "patliputra - patna - INDIA", "cloud@zohomail.in");
        logger.info("Update existing Employee :: {} => \n{}", 52, prettyPrint(updateAPI(BASE_URL + "/employee/52", new Gson().toJson(employeeUpdate))));


    }


    /**
     * for pretty-print we have replaced jsonObject with JsonElement as
     * JsonElement works because it can represent both:
     * <p>
     * a JSON object
     * <p>
     * a JSON array
     * <p>
     * a JSON primitive
     * <p>
     * null
     * <p>
     * JsonObject works only if the JSON string begins with { ... }.
     * If the input is a JSON array ([ ... ]), JsonObject will fail.
     *
     * @param obj
     * @return
     */
    public static String prettyPrint(Object obj) {
        String jsonString = obj.toString();

        // parse JSON string -> JsonObject.
        //JsonObject jsonObject = new JsonParser().parse(jsonString).getAsJsonObject();
        JsonElement element = new JsonParser().parse(jsonString);

        // pretty print
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        //return gson.toJson(jsonObject); //this will give pretty-json
        return gson.toJson(element); //this will give pretty-json
    }

    /**
     *
     * @param url
     * @return
     */
    public static Object readAPI(String url) {
        logger.info("reading API URL: {}", url);
        try (var stream = URI.create(url).toURL().openStream()) { //new URL(url).openStream()) {
            return new String(stream.readAllBytes());
        } catch (IOException e) {
            //throw new RuntimeException(e);
            return "{\"Output\":\"NotFound\"}";
        }
    }

    /**
     *
     * @param url
     * @return
     * @throws InterruptedException
     */
    public static Object deleteAPI(String url) throws InterruptedException {
        logger.info("deleting API URL: {}", url);
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).DELETE().build();
        HttpResponse<String> response = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            //throw new RuntimeException(e);
            return "{\"Output\":\"resource NotFound to Delete\"}";
        }

        return response.body(); // JSON as String.
    }

    public static Object multiDelete(String url) {
        logger.info("deleting API URL - first fetch all resource w.r.t specific name : {}", url.substring(url.lastIndexOf("/") + 2));

        /// /now fetch all the employee w.r.t the name.
        String jsonString = (String) readAPI(url);

        try {
            /// /now after fetch call here we can have 1 or more+ record(s) fetched...
            JsonArray jsonArray = JsonParser.parseString(jsonString).getAsJsonArray();

            List<String> empIds = StreamSupport.stream(jsonArray.spliterator(), false)
                    .map(employee -> employee.getAsJsonObject().get("id").getAsString())
                    .toList();

            empIds.stream().forEach(id -> {
                /// /now call to delete each & every record(s).
                try {
                    logger.info("Delete Employee by ID :: {} => \n{} ", id, prettyPrint(deleteAPI(BASE_URL + "/employee/" + id)));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });

            return "{\"Output\":\"resource with name : " + url.substring(url.lastIndexOf("/") + 2) + " is completely Delete\"}";
        } catch (IllegalStateException e) {
            return "{\"Output\":\"resource not found to DELETE\"}";
        }

    }

    /**
     * takes care for Creating the resource.
     *
     * @param url
     * @param jsonBody
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static Object createAPI(String url, String jsonBody) throws IOException, InterruptedException {
        logger.info("creating API URL: {}", url);
        HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        // here 2nd parameter - httpresponse body handlers means -
        // Here: ofString() means:
        //“Read the response body as text (String) using UTF-8 or charset from headers.”
        //
        //The type of the response becomes:
        //
        //HttpResponse<String>
        //
        //So the body is available as: String responseBody = response.body();
        return response.body(); // JSON as String
    }

    /**
     * takes care for Updating the resource using the HTTP PUT call.
     *
     * @param url
     * @param jsonBody
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static Object updateAPI(String url, String jsonBody) throws IOException, InterruptedException {
        logger.info("updating API URL: {}", url);
        HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(url))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return response.body(); // JSON as String.
    }
}
