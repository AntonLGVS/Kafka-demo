package com.lgvs.dev.kafka.trusted;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
@Value
public class Envelope<T> {
    String correlationId;
    String businessKey = "undefined";
    EventType type = EventType.MESSAGE;
    boolean start = true;
    T payload;

    public static <T> Envelope<T> create(String correlationId, T payload) {
        return Envelope.<T>builder()
                .correlationId(correlationId)
                .payload(payload)
                .build();
    }

    public enum EventType {ACTIVITY, MESSAGE, SIGNAL, CONDITION, INCIDENT}
}
