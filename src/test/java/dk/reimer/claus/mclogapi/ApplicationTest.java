package dk.reimer.claus.mclogapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationTest {

    @Test
    public void test() throws Exception {
        CentralLoggerEntry entry = new CentralLoggerEntry("appId", "tid", "info", new Date().toString(), "Hello", "com", "rid");

        List<CentralLoggerEntry> logged = new ArrayList<>();

        List<CentralLogger> loggers = new ArrayList<>();
        loggers.add((e) -> logged.add(e));
        loggers.add((e) -> logged.add(e));

        Application application = new Application(loggers);
        application.log(entry);

        assertEquals(2, logged.size());
        assertEquals(entry, logged.get(0));
        assertEquals(entry, logged.get(1));
    }
}