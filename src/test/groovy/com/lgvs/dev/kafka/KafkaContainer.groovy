package com.lgvs.dev.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.spock.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import spock.lang.Shared;
import spock.lang.Specification;

@Profile("test")
@Testcontainers
class KafkaContainer extends Specification {
    @Shared
    static org.testcontainers.containers.KafkaContainer kafka = new org.testcontainers.containers.KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"))
    static {
        kafka.start()
    }

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        def servers = () -> kafka.bootstrapServers
        registry.add("spring.kafka.producer.bootstrap-servers", servers)
        registry.add("spring.kafka.consumer.bootstrap-servers", servers)
        registry.add("spring.kafka.bootstrap-servers", servers)
    }

    @TestConfiguration
    static class TestConfig{
        @Bean
        NewTopic createTopic(@Value('${app.kafka.topic.broadcast}') String topic) {
            return new NewTopic(topic, 1, 1.shortValue())
        }
    }
}
