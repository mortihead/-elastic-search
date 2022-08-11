package ru.mortihead.elastic.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mortihead.elastic.model.Book;
import ru.mortihead.elastic.service.BookService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/book")
public class BookController {

    private final BookService bookService;

    @GetMapping(value = "generateBooks")
    public void generateBooks(Integer count) {
        if (count == null) {
            throw new RuntimeException();
        }
        bookService.generateBooks(count);
    }

    @GetMapping(value = "clearBooks")
    public void clearBooks() {
        bookService.clearBooks();
    }

    @GetMapping(value = "findOne")
    public Book findOne(String id) {
        return bookService.findOne(id);
    }

    @GetMapping(value = "findAll")
    public Iterable<Book> findAll(Integer page, Integer size) {
        return bookService.findAll(PageRequest.of(page, size));
    }

    @GetMapping(value = "findByAuthor")
    public Page<Book> findByAuthor(String author, Integer page, Integer size) {
        return bookService.findByAuthor(author, PageRequest.of(page, size));
    }

    @GetMapping(value = "findByTitle")
    public List<Book> findByTitle(String title, Integer page, Integer size) {
        return bookService.findByTitle(title, PageRequest.of(page, size));
    }

    @GetMapping(value = "findUseCriteriaQuery")
    public List<Book> findUseCriteriaQuery(String author, Integer page, Integer size) {
        return bookService.findUseCriteriaQuery(author, PageRequest.of(page, size));
    }

    @GetMapping(value = "findUseNativeSearchQueryBuilder")
    public List<Book> findUseNativeSearchQueryBuilder(String title, Integer page, Integer size) {
        return bookService.findUseNativeSearchQueryBuilder(title, PageRequest.of(page, size));
    }

    @GetMapping(value = "findUseBoolNativeSearchQueryBuilder")
    public List<Book> findUseBoolNativeSearchQueryBuilder(String title, String author, Integer page, Integer size) {
        return bookService.findUseBoolNativeSearchQueryBuilder(title, author, PageRequest.of(page, size));
    }

    @GetMapping(value = "findUseNativeSearchQueryInRepository")
    public List<Book> findUseNativeSearchQueryInRepository(String content, Integer page, Integer size) {
        return bookService.findUseNativeSearchQueryInRepository(content, PageRequest.of(page, size));
    }

    @GetMapping(value = "getCount")
    public Long getCount() {
        return bookService.getCount();
    }

}
