package com.msquare.flabook.common.configurations;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;
import java.util.Map;

@Data
//@Component
@EnableConfigurationProperties
@ConfigurationProperties("feed")
@PropertySources(value = {
        //@PropertySource(value = {"classpath:/feed.yaml"}, factory = FeedScoreProperties.YamlPropertySourceFactory.class),
        //@PropertySource(value = {"classpath:/feed.yml"}, factory = FeedScoreProperties.YamlPropertySourceFactory.class, ignoreResourceNotFound = true)
        /*"classpath:/feed-${spring.profiles.active}.yaml",
        "classpath:/feed-${spring.profiles.active}.yml"*/

})
public class FeedScoreProperties {

    @Data
    @NoArgsConstructor
    public static class FeedStatusConfiguration {
        private BaseScore min;
        private BaseScore max;
        private BaseScore mid;

        private int defaultscore;
        private int plantcarescore;
        private int baseuserlevel;
        private int receiving;
        private int commentpointper;
        private Weight weight;

        private Answer answered;
        private Rank ranked;
    }

    @Data
    @NoArgsConstructor
    public static class BaseScore {
        private int read;
        private int receiving;
        private int comment;
    }

    @Data
    @NoArgsConstructor
    public static class Answer {
        private Map<String, AnswerScore> per;
    }

    @Data
    @NoArgsConstructor
    public static class Weight {
        private int level;
        private int status;
    }

    @Data
    @NoArgsConstructor
    public static class AnswerScore {
        private int popular;
        private int hot;
    }


    @Data
    @NoArgsConstructor
    public static class Rank {
        private Map<String, Integer> per;
        private int receiving;
    }

    @Getter
    @Setter
    private FeedStatusConfiguration status;

    @SuppressWarnings("unused")
    public static class YamlPropertySourceFactory implements PropertySourceFactory {
        @Override
        public org.springframework.core.env.@NonNull PropertySource<?> createPropertySource(String name, @NonNull EncodedResource resource) throws IOException {
            return name != null ? new YamlPropertySourceLoader().load(name, resource.getResource()).get(0) : new YamlPropertySourceLoader().load(
                    getNameForResource(resource.getResource()), resource.getResource()).get(0);
        }

        private static String getNameForResource(Resource resource) {
            String name = resource.getDescription();
            if (!name.isEmpty()) {
                name = resource.getClass().getSimpleName() + "@" + System.identityHashCode(resource);
            }

            return name;
        }
    }

}
