package dk.reimer.claus.mclogapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PerformanceManual {

    @Value(value="http://localhost:${local.server.port}/log")
    private String address;

    // ------------------------------------------------------------
    // high:        971 ms
    // low:           3 ms
    // avg:          24 ms
    // total:       500 entries
    // failed:        0 entries
    // ------------------------------------------------------------
    @Test
    public void run10Clients() throws Exception {
        Result result = run(1000 * 60 * 5, 10, 50, 20);
        System.out.println(result);
    }


    // ------------------------------------------------------------
    // high:       1124 ms
    // low:           5 ms
    // avg:          67 ms
    // total:      5000 entries
    // failed:        0 entries
    // ------------------------------------------------------------
    @Test
    public void run100Clients() throws Exception {
        Result result = run(1000 * 60 * 5, 100, 50, 20);
        System.out.println(result);
    }


    // ------------------------------------------------------------
    //    high:       5343 ms
    //    low:         128 ms
    //    avg:         471 ms
    //    total:     28648 entries
    //    failed:    21352 entries
    // ------------------------------------------------------------
    @Test
    public void run1000Clients() throws Exception {
        Result result = run(1000 * 60 * 10, 1000, 50, 20);
        System.out.println(result);
    }


    private Result run(long timeout, int clients, int entries, long interval) throws InterruptedException, TimeoutException {
        RestTemplate rest = new RestTemplate();

        List<Future<Result>> futures = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(clients);
        for (int i = 0; i < clients; i++) {
            futures.add(executor.submit(new Client(rest, address, i, entries, interval)));
        }
        executor.shutdown();


        Result result = new Result();
        if (executor.awaitTermination(timeout, TimeUnit.MILLISECONDS)) {
            for (Future<Result> future : futures) {
                try {
                    result.add(future.get());
                } catch (ExecutionException x) {
                    result.incrementFailed(entries);
                }
            }
        } else {
            throw new TimeoutException();
        }

        return result;
    }


    private static class Client implements Callable<Result> {
        private final RestTemplate rest;
        private final int clientId;
        private final int entries;
        private final long interval;
        private final String address;

        public Client(RestTemplate rest, String address, int clientId, int entries, long interval) {
            this.rest = rest;
            this.clientId = clientId;
            this.entries = entries;
            this.interval = interval;
            this.address = address;
        }

        @Override
        public Result call() throws Exception {
            CentralLoggerEntry entry = new CentralLoggerEntry();
            entry.applicationID = "performanceTest";
            entry.traceID = "client" + clientId;
            entry.severity = "info";

            Result result = new Result();
            for (int i = 0; i < entries; i++) {
                entry.timestamp = new Date().toString();
                entry.message = "log entry #" + i;

                try {
                    long now = System.currentTimeMillis();
                    rest.postForEntity(address, entry, Void.class);
                    result.add(System.currentTimeMillis() - now);
                } catch (Exception x) {
                    System.err.println(x);
                    result.incrementFailed(1);
                }

                Thread.sleep(interval);
            }
            return result;
        }
    }

    private static class Result {
        private long hi = -1;
        private long lo = -1;
        private long total;
        private int count;
        private int failed;

        public long high() {
            return hi;
        }

        public long low() {
            return lo;
        }

        public long average() {
            return count == 0 ? -1 : total / count;
        }

        public int count() {
            return count;
        }

        public int failed() {
            return failed;
        }

        public void incrementFailed(int failed) {
            this.failed += failed;
        }

        public void add(long time) {
            if (hi == -1 || hi < time) hi = time;
            if (lo == -1 || lo > time) lo = time;
            count ++;
            total += time;
        }

        public void add(Result result) {
            if (this.hi == -1 || this.hi < result.hi) this.hi = result.hi;
            if (this.lo == -1 || this.lo < result.lo) this.lo = result.lo;
            this.count += result.count;
            this.total += result.total;
            this.failed += result.failed;
        }

        public String toString() {
            return ("------------------------------------------------------------%n"
                 + "high:   %8s ms%n"
                 + "low:    %8s ms%n"
                 + "avg:    %8s ms%n"
                 + "total:  %8s entries%n"
                 + "failed: %8s entries%n"
                 + "------------------------------------------------------------%n")
                 .formatted(high(), low(), average(), count(), failed());
        }
    }
}
