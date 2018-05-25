package com.example.demo.repository;

import com.example.demo.support.AbstractIntegrationTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.testcontainers.containers.GenericContainer;

import static org.assertj.core.api.Assertions.assertThat;

public class RatingsRepositoryTest {

    final String talkId = "testcontainers";

    RatingsRepository repository;

    @Rule
    public GenericContainer redis = new GenericContainer("redis:3-alpine")
            .withExposedPorts(6379);

    @Before
    public void setUp() {
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(
                redis.getContainerIpAddress(), redis.getFirstMappedPort());
        connectionFactory.afterPropertiesSet();
        repository = new RatingsRepository(
                new ReactiveRedisTemplate<>(
                        connectionFactory,
                        RedisSerializationContext.string()
                )
        );
    }

    @Test
    public void testEmptyIfNoKey() {
        assertThat(repository.findAll(talkId).block()).isEmpty();
    }

    @Test
    public void testLimits() {
        repository.redisOperations.opsForHash()
                .put(repository.toKey(talkId), "5", Long.MAX_VALUE + "")
                .block();

        repository.add(talkId, 5).block();
    }
}