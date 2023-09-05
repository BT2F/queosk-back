package com.bttf.queosk.config;

import com.bttf.queosk.entity.KakaoAuth;
import com.bttf.queosk.entity.RedisQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;

@Configuration
@EnableRedisRepositories
public class RedisConfig {

//    로컬에서 테스트 시 아래를 활성화 하여야 함

//    @Value("${spring.redis.host}")
//    private String redisHost;
//
//    @Value("${spring.redis.port}")
//    private int redisPort;
//
//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
//        return new LettuceConnectionFactory(redisHost, redisPort);
//    }

// 로컬에서 테스트시 아래를 비활성화 하여야 함

    @Value("${spring.redis.cluster.nodes}")
    private List<String> clusterNodes;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
        clusterNodes.forEach(
                node -> {
                    String[] url = node.split(":");
                    redisClusterConfiguration.clusterNode(url[0], Integer.parseInt(url[1]));
                }
        );


        return new LettuceConnectionFactory(redisClusterConfiguration);
    }

    //  여기까지 비활성화

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // RedisQueue 클래스의 직렬화 설정
        Jackson2JsonRedisSerializer<RedisQueue> queueSerializer =
                new Jackson2JsonRedisSerializer<>(RedisQueue.class);
        redisTemplate.setValueSerializer(queueSerializer);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(queueSerializer);

        // KakaoAuth 클래스의 직렬화 설정
        Jackson2JsonRedisSerializer<KakaoAuth> kakaoAuthSerializer =
                new Jackson2JsonRedisSerializer<>(KakaoAuth.class);
        redisTemplate.setValueSerializer(kakaoAuthSerializer);
        redisTemplate.setHashValueSerializer(kakaoAuthSerializer);

        return redisTemplate;
    }
}