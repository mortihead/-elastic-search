package ru.mortihead.elastic.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.AbstractElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

@Configuration
public class ElasticConfig {

    @Value("${elasticsearch.host}")
    private String esHost;

    @Value("${elasticsearch.http.port}")
    private int esHttpPort;

    @Value("${elasticsearch.login}")
    private String login;

    @Value("${elasticsearch.password}")
    private String password;

    @Value("${elasticsearch.timeout}")
    private long timeout;

    @Bean
    public RestHighLevelClient elasticsearchClient() {
        final ClientConfiguration configuration = ClientConfiguration.builder()
                .connectedTo(esHost + ":" + esHttpPort)
                .withBasicAuth(login, password)
                .withSocketTimeout(timeout)
                .build();
        return RestClients.create(configuration).rest();
    }

    @Bean
    public AbstractElasticsearchTemplate elasticsearchTemplate() {
        return new ElasticsearchRestTemplate(elasticsearchClient());
    }

}