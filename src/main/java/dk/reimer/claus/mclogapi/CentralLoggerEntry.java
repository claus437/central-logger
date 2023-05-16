package dk.reimer.claus.mclogapi;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CentralLoggerEntry {
    public String applicationID;
    public String traceID;
    public String severity;
    public String timestamp;
    public String message;
    public String componentName;
    public String requestID;

    public CentralLoggerEntry() {
    }

    @JsonCreator
    public CentralLoggerEntry(
        @JsonProperty(value = "applicationID",  required = true) String applicationID,
        @JsonProperty(value = "traceID",        required = true) String traceID,
        @JsonProperty(value = "severity",       required = true) String severity,
        @JsonProperty(value = "timestamp",      required = true) String timestamp,
        @JsonProperty(value = "message",        required = true) String message,
        @JsonProperty(value = "componentName",  required = false) String componentName,
        @JsonProperty(value = "requestID",      required = false) String requestID
    ) {
        this.applicationID = applicationID;
        this.traceID = traceID;
        this.severity = severity;
        this.timestamp = timestamp;
        this.message = message;
        this.componentName = componentName;
        this.requestID = requestID;
    }

    @Override
    public String toString() {
        return "LogEntry{" +
                "applicationID='" + applicationID + '\'' +
                ", traceID='" + traceID + '\'' +
                ", severity='" + severity + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", message='" + message + '\'' +
                ", componentName='" + componentName + '\'' +
                ", requestID='" + requestID + '\'' +
                '}';
    }
}
