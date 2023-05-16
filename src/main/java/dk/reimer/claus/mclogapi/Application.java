package dk.reimer.claus.mclogapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;


@SpringBootApplication
@RestController
public class Application {
    private final Logger logger = LoggerFactory.getLogger(Application.class);
    private final List<CentralLogger> loggers;

    @Autowired
    public Application(List<CentralLogger> loggers) {
        this.loggers = loggers;
        logger.info("known log handlers " + loggers);
    }

    @RequestMapping(value = "/log", method = RequestMethod.POST)
    public void log(@RequestBody CentralLoggerEntry entry) throws Exception {
        for (CentralLogger cl : loggers) {
            cl.log(entry);
        }
    }

    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
    }
}
