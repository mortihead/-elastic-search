package ru.mortihead.elastic.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import ru.mortihead.elastic.model.Book;

import java.util.List;

@Repository
public interface BookRepository extends ElasticsearchRepository<Book, String> {

    Page<Book> findByAuthor(String author, Pageable pageable);

    List<Book> findByTitle(String title, Pageable pageable);

    @Query("{\"match\": {\"content\": {\"query\": \"?0\",\"operator\" : \"and\"}}}")
    List<Book> queryByContentIn(String content, Pageable pageable);
}