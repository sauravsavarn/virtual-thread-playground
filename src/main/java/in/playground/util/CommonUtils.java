package in.playground.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class CommonUtils {

    private static final Logger log = LoggerFactory.getLogger(CommonUtils.class);

    public static void sleep(String taskName, Duration duration){
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            log.info("{} task is cancelled", taskName);
        }
    }

    public static void sleep(Duration duration){
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static long timer(Runnable runnable){
        var start = System.currentTimeMillis();
        runnable.run();
        var end = System.currentTimeMillis();
        return (end - start);
    }

    /**
     * encode url
     * @param input
     * @return
     */
    public static String urlEncoderUTF8(String input) {
        return URLEncoder.encode(input, StandardCharsets.UTF_8);
    }

}