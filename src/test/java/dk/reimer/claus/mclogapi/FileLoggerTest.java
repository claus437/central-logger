package dk.reimer.claus.mclogapi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

class FileLoggerTest {
    private final Path logfile = Paths.get("target/test.log").toAbsolutePath();

    @BeforeEach
    public void cleanup() throws IOException  {
        if (Files.exists(logfile)) {
            Files.delete(logfile);
        }

        if (!Files.isDirectory(logfile.getParent())) {
            Files.createDirectories(logfile.getParent());
        }
    }

    @Test
    public void test() throws IOException {
        CentralLoggerEntry entry = new CentralLoggerEntry("appId", "tid", "info", new Date().toString(), "hello", "com", "rid");
        try (FileLogger logger = new FileLogger(logfile.toString())) {
            logger.log(entry);
        }

        assertEquals(entry.toString(), Files.readString(logfile).trim());
    }

}