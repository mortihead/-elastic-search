package ru.mortihead.elastic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import ru.mortihead.elastic.model.Book;

import java.util.Random;

@Configuration
public class BeanConfig {

    @Bean
    public Random random() {
        return new Random();
    }

    @Bean
    public IndexOperations indexOperations(ElasticsearchOperations operations) {
        return operations.indexOps(Book.class);
    }
}
