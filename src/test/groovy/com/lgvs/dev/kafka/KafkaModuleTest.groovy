package com.lgvs.dev.kafka

import com.lgvs.dev.camunda.delegate.MyProc
import com.lgvs.dev.kafka.trusted.Envelope
import org.apache.kafka.common.TopicPartition
import org.camunda.bpm.engine.RuntimeService
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.camunda.bpm.engine.test.Deployment
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.context.annotation.Import
import org.springframework.kafka.config.KafkaListenerEndpointRegistry
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.utils.ContainerTestUtils

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(KafkaContainer)
class KafkaModuleTest extends KafkaContainer {

    @Value('${app.kafka.topic.broadcast}')
    String topic;

    @Autowired
    KafkaTemplate<String, Envelope<?>> template

    @Autowired
    KafkaListenerEndpointRegistry registry

    @Autowired
    RuntimeService runtimeService

    @SpringBean(name = "endTest")
    JavaDelegate endTest = Mock()

    def "exist BUS-EVENT @KafkaListener"() {
        given:
        def listenerName = "bus-event"
        def firstPartition = 0

        when:
        def listener = registry.getListenerContainer(listenerName)

        ContainerTestUtils.waitForAssignment(listener, 1)

        then:
        listener != null
        listener.assignedPartitions == [new TopicPartition(topic, firstPartition)]
    }


    @Deployment(resources = "test-process.bpmn")
    def "test event from Kafka and start business process"() {
        given:
        def traceKey = "process_definition:id:uuid"
        def targetEndpoint = "Kafka_test_msg"
        def latch = new CountDownLatch(1)

        when:
        template.send(topic, traceKey,
                Envelope.create(targetEndpoint, Map.of("a", "b")))

        latch.await(10, TimeUnit.SECONDS)

        then:
        1 * endTest.execute(_) >> { latch.countDown() }
    }
}
