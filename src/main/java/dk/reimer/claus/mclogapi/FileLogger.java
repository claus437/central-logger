package dk.reimer.claus.mclogapi;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;


@Component()
@Scope("singleton")
public class FileLogger implements CentralLogger, Closeable {
    private final Logger logger = LoggerFactory.getLogger(FileLogger.class);
    private final String filename;
    private final Writer out;

    public FileLogger(@Value("${central-logger.file-logger.filename:out.log}") String filename) throws IOException {
        this.filename = filename;
        out = Files.newBufferedWriter(Paths.get(filename), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    @Override
    public void log(CentralLoggerEntry entry) {
        try {
            synchronized (out) {
                out.write(entry.toString() + "\n");
                out.flush();
            }
        } catch (RuntimeException | IOException x) {
            logger.error("failed to write the log entry in " + filename + " " + x, x);
        }
    }

    @PreDestroy
    public void close() throws IOException  {
        synchronized (out) {
            out.close();
        }
    }
}
