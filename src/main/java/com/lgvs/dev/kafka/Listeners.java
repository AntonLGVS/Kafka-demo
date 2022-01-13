package com.lgvs.dev.kafka;

import com.lgvs.dev.kafka.trusted.Envelope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class Listeners {

    private final RuntimeService camundaService;

    @KafkaListener(id = "bus-event", topics = "#{'${app.kafka.topic.broadcast}'}")
    public void consumeEventTopic(@Payload Envelope<Map<String, String>> payload,
                                  @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key) {
        log.info("{}. Catch event: {} [type - {}, business key - {}]", key, payload.getCorrelationId(),
                payload.getType(), payload.getBusinessKey());

        camundaService.createMessageCorrelation(payload.getCorrelationId())
                .processInstanceBusinessKey(payload.getBusinessKey())
                .setVariable("payload", payload.getPayload())
                //Simple way. Starting business process
                .correlateStartMessage();
    }
}
