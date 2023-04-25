package com.nowcoder.community.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;

//es配置类
@Configuration
public class ElasticsearchConfig {
    @Value("${spring.elasticsearch.uris}")
    private String esUrl;
    //localhost:9200 写在配置文件中,直接用 <- spring.elasticsearch.uris
    @Bean
    RestHighLevelClient client() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(esUrl)//elasticsearch地址
                .build();
        return RestClients.create(clientConfiguration).rest();
    }
}

