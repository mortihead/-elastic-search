package ru.mortihead.elastic.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.AbstractElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Service;
import ru.mortihead.elastic.model.Book;
import ru.mortihead.elastic.repository.BookRepository;
import ru.mortihead.elastic.utils.BookConstants;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AbstractElasticsearchTemplate esTemplate;
    private final Random WORD_RANDOM;
    private final IndexOperations indexOps;

    @Override
    public Book save(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public void delete(Book book) {
        bookRepository.delete(book);
    }

    @Override
    public Book findOne(String id) {
        Optional<Book> book = bookRepository.findById(id);
        return book.orElse(null);
    }

    @Override
    public Page<Book> findAll(PageRequest pageRequest) {
        return bookRepository.findAll(pageRequest);
    }

    @Override
    public Page<Book> findByAuthor(String author, PageRequest pageRequest) {
        return bookRepository.findByAuthor(author, pageRequest);
    }

    @Override
    public List<Book> findByTitle(String title, PageRequest pageRequest) {
        return bookRepository.findByTitle(title, pageRequest);
    }

    @Override
    public List<Book> findUseNativeSearchQueryInRepository(String content, PageRequest pageRequest) {
        return bookRepository.queryByContentIn(content, pageRequest);
    }

    @Override
    public List<Book> findUseCriteriaQuery(String author, PageRequest pageRequest) {
        Criteria criteria = new Criteria("author").is(author);
        Query query = new CriteriaQuery(criteria, pageRequest);
        return esTemplate.search(query, Book.class, indexOps.getIndexCoordinates())
                .getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());
    }

    @Override
    public List<Book> findUseNativeSearchQueryBuilder(String title, PageRequest pageRequest) {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchQuery("title", title).operator(Operator.OR))
                .withPageable(pageRequest)
                .build();
        return esTemplate.search(searchQuery, Book.class, indexOps.getIndexCoordinates())
                .getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());
    }

    @Override
    public List<Book> findUseBoolNativeSearchQueryBuilder(String title, String author, PageRequest pageRequest) {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery()
                        .must(QueryBuilders.matchPhraseQuery("title", title))
                        .should(QueryBuilders.matchPhraseQuery("author", author)))
                .withPageable(pageRequest)
                .build();
        return esTemplate.search(searchQuery, Book.class, indexOps.getIndexCoordinates())
                .getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());
    }

    @Override
    public long getCount() {
        return bookRepository.count();
    }

    @Override
    public void generateBooks(Integer count) {
        long booksCount = getCount();
        for (int i = 0; i < count; i++) {
            Book book = new Book(String.valueOf(booksCount + i),
                    BookConstants.TITLES.get(WORD_RANDOM.nextInt(10)),
                    BookConstants.AUTHORS.get(WORD_RANDOM.nextInt(10)),
                    generateText(),
                    LocalDate.now().toString());
            bookRepository.save(book);

            if (i % 100 == 0) {
                log.info(String.valueOf(i));
            }
        }
    }

    @Override
    public void clearBooks() {
        indexOps.delete();
    }

    private String generateText() {
        int countWords = WORD_RANDOM.nextInt(100);
        StringJoiner stringJoiner = new StringJoiner(" ");
        for (int i = 0; i < countWords; i++) {
            stringJoiner.add(BookConstants.WORDS.get(WORD_RANDOM.nextInt(10)));
        }
        return stringJoiner.toString();
    }

}