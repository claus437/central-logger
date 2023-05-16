package dk.reimer.claus.mclogapi;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.test.context.EmbeddedKafka;
import java.util.Date;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(properties = {
    "spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
    "spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer",
    "spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer",
    "spring.kafka.consumer.properties.spring.json.trusted.packages=dk.reimer.claus.mclogapi",
    "spring.kafka.consumer.auto-offset-reset=earliest",
    "spring.kafka.consumer.group-id=test"
})
@EmbeddedKafka(partitions = 1, topics = {"logs"}, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092"})
class KafkaLoggerTest {
    private final Logger log = Logger.getLogger(KafkaLoggerTest.class);
    private final Exchanger<CentralLoggerEntry> exchanger = new Exchanger<>();

    @Autowired
    KafkaLogger logger;

    @KafkaListener(topics = "logs", groupId = "test")
    public void receive(ConsumerRecord<?, CentralLoggerEntry> consumerRecord) throws InterruptedException {
        exchanger.exchange(consumerRecord.value());
    }

    @Test
    public void log() throws Exception {
        CentralLoggerEntry entry = new CentralLoggerEntry("appId", "tid", "info", new Date().toString(), "Hello", null, null);

        logger.log(entry);

        CentralLoggerEntry received = exchanger.exchange(null, 10, TimeUnit.SECONDS);
        assertEquals(entry.toString(), received.toString());
    }
}