package ru.mortihead.elastic.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.mortihead.elastic.model.Book;

import java.util.List;

public interface BookService {

    Book save(Book book);

    void delete(Book book);

    Book findOne(String id);

    Page<Book> findAll(PageRequest pageRequest);

    Page<Book> findByAuthor(String author, PageRequest pageRequest);

    List<Book> findByTitle(String title, PageRequest pageRequest);

    List<Book> findUseCriteriaQuery(String author, PageRequest pageRequest);

    void generateBooks(Integer count);

    void clearBooks();

    List<Book> findUseNativeSearchQueryBuilder(String title, PageRequest pageRequest);

    List<Book> findUseBoolNativeSearchQueryBuilder(String title, String author, PageRequest pageRequest);

    List<Book> findUseNativeSearchQueryInRepository(String content, PageRequest pageRequest);

    long getCount();
}