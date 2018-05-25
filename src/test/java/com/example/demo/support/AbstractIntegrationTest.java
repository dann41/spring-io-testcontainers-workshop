package com.example.demo.support;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;

import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.datasource.url=jdbc:tc:postgresql:10-alpine://springio/workshop",
        "spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver"
})
public abstract class AbstractIntegrationTest {


    static {
        GenericContainer redis = new GenericContainer("redis:3-alpine")
                .withExposedPorts(6379);

        KafkaContainer kafka = new KafkaContainer("4.0.0");

        Stream.of(redis, kafka).parallel().forEach(GenericContainer::start);

        System.setProperty("spring.redis.host", redis.getContainerIpAddress());
        System.setProperty("spring.redis.port", redis.getFirstMappedPort() + "");
        System.setProperty("spring.kafka.bootstrap-servers", kafka.getBootstrapServers());
    }

    protected RequestSpecification requestSpecification;

    @LocalServerPort
    private int localServerPort;

    @Before
    public void setUpAbstractIntegrationTest() {
        requestSpecification = new RequestSpecBuilder()
                .setPort(localServerPort)
                .addHeader(
                        HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON_VALUE
                )
                .build();
    }


}
