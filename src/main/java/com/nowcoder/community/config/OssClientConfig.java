package com.nowcoder.community.config;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OssClientConfig {
    @Value("${aliyun.endpoint}")
    private String endpoint;
    @Value("${aliyun.accessKeyId}")
    private String accessKeyId;
    @Value("${aliyun.accessKeySecret}")
    private String accessKeySecret;

    @Bean
    public ClientBuilderConfiguration builderConfiguration() {
        ClientBuilderConfiguration configuration = new ClientBuilderConfiguration();
        configuration.setMaxConnections(500);
        return configuration;
    }

    @Bean
    public OSS buildClient(ClientBuilderConfiguration configuration) {
        OSS client = new OSSClientBuilder().build(endpoint, accessKeyId,accessKeySecret, configuration);
        return client;
    }
}
