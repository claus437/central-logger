package dk.reimer.claus.mclogapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
@Scope("singleton")
public class KafkaLogger implements CentralLogger {
    private final Logger logger = LoggerFactory.getLogger(KafkaLogger.class);
    private final KafkaTemplate<String, CentralLoggerEntry> kafka;

    @Autowired
    public KafkaLogger(KafkaTemplate<String, CentralLoggerEntry> kafka) {
        this.kafka = kafka;
    }

    public void log(CentralLoggerEntry entry) {
        kafka.send("logs", entry).whenComplete((r, x) -> {
            if (x != null) {
                logger.error("failed to send log message to kafka " + x, x);
            }
        });
    }
}
