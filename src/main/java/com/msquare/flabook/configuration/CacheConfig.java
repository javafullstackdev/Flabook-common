package com.msquare.flabook.configuration;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.SimpleCacheResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(CacheRedisProperties.class)
public class CacheConfig {

    @Value("${spring.redis.host:localhost}")
    public String redisHost;

    @Value("${spring.redis.port:6379}")
    public int redisPort;

    @Value("${spring.redis.password:#{null}}")
    private String redisPassword;

    @NonNull
    private final CacheRedisProperties cacheRedisProperties;

    @Bean
    @Qualifier("localCacheManager")
    public CacheManager localCacheManager() {
        EhCacheManagerFactoryBean bean = ehCacheCacheManager();
        if(bean == null) {
            throw new IllegalArgumentException("ehCacheCacheManagern is null");
        }

        EhCacheCacheManager manager = new EhCacheCacheManager();
        manager.setCacheManager(bean.getObject());
        return manager;
    }

    @Bean
    public EhCacheManagerFactoryBean ehCacheCacheManager() {
        EhCacheManagerFactoryBean cacheManagerFactoryBean = new EhCacheManagerFactoryBean();
        cacheManagerFactoryBean.setConfigLocation(new ClassPathResource("ehcache.xml"));
        cacheManagerFactoryBean.setShared(true);
        return cacheManagerFactoryBean;
    }

    @Bean
    @Primary
    public CacheManager cacheManager() {
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(redisConnectionFactory());
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()

                /*.serializeValuesWith(RedisSerializationContext
                .SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))//.prefixKeysWith("imhere:")*/
                .entryTtl(cacheRedisProperties.getDefaultExpireTime());
        builder.cacheDefaults(configuration);

        log.info("cacheRedisProperties : {}", cacheRedisProperties.getExpireTime());

        Map<String, RedisCacheConfiguration> cacheConfigurationMap = new HashMap<>();
        for(Map.Entry<String, Duration> entry : cacheRedisProperties.getExpireTime().entrySet()) {


            cacheConfigurationMap.put(entry.getKey(),   RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(entry.getValue()));
        }
        builder.withInitialCacheConfigurations(cacheConfigurationMap);
        return builder.build();
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration standaloneConfiguration = new RedisStandaloneConfiguration(redisHost, redisPort);
        if(redisPassword != null && !redisPassword.isEmpty()) {
            standaloneConfiguration.setPassword(RedisPassword.of(redisPassword));
        } else {
            standaloneConfiguration.setPassword(RedisPassword.none());
        }
        return new LettuceConnectionFactory(standaloneConfiguration);
    }

    @Bean
    @Qualifier("localCacheResolver")
    public CacheResolver localCacheResolver() {
        return new SimpleCacheResolver(localCacheManager());
    }

}
