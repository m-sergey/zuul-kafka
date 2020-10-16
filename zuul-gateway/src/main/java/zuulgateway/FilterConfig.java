package zuulgateway;

import org.springframework.cloud.netflix.zuul.filters.ProxyRequestHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public KafkaFilter kafkaFilter(ProxyRequestHelper helper){
        return new KafkaFilter(helper);
    }
}
