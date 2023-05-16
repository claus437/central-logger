package dk.reimer.claus.mclogapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CentralLoggerEntryTest {

    @Test
    public void readFullRecord() throws Exception {
        CentralLoggerEntry expected = new CentralLoggerEntry("app", "trace", "info", "10oct", "hello world", "myapp", "1002");

        ObjectMapper mapper = new ObjectMapper();
        CentralLoggerEntry actual = mapper.readValue("{\"applicationID\":\"app\",\"traceID\":\"trace\",\"severity\":\"info\",\"timestamp\":\"10oct\",\"message\":\"hello world\",\"componentName\":\"myapp\",\"requestID\":\"1002\"}", CentralLoggerEntry.class);
        assertEquals(expected.toString(), actual.toString());
    }

    @Test
    public void readOptionalRecord() throws Exception {
        CentralLoggerEntry expected = new CentralLoggerEntry("app", "trace", "info", "10oct", "hello world", null, null);

        ObjectMapper mapper = new ObjectMapper();
        CentralLoggerEntry actual = mapper.readValue("{\"applicationID\":\"app\",\"traceID\":\"trace\",\"severity\":\"info\",\"timestamp\":\"10oct\",\"message\":\"hello world\"}", CentralLoggerEntry.class);
        assertEquals(expected.timestamp, actual.timestamp);
    }

    @Test
    public void readMissingValueRecord() {
        ObjectMapper mapper = new ObjectMapper();
        assertThrows(MismatchedInputException.class, () -> {
            mapper.readValue("{\"applicationID\":\"app\",\"traceID\":\"trace\",\"severity\":\"info\",\"timestamp\":\"10oct\"}", CentralLoggerEntry.class);
        });
    }
}