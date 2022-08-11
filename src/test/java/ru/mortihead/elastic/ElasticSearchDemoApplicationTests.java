package ru.mortihead.elastic;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.test.context.junit4.SpringRunner;
import ru.mortihead.elastic.model.Book;
import ru.mortihead.elastic.service.BookService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ElasticSearchDemoApplication.class)
public class ElasticSearchDemoApplicationTests {

    @Autowired
    private BookService bookService;

    @Autowired
    private ElasticsearchOperations operations;

    @Before
    public void before() {
        IndexOperations indexOps = operations.indexOps(Book.class);
        indexOps.delete();
        indexOps.create();
        indexOps.putMapping();
        indexOps.refresh();
    }

    @Test
    public void testSave() {
        Book book = new Book("1001", "Elasticsearch in Action", "Radu Gheorghe", "SummaryElasticsearch in Action teaches you how to " +
                "build scalable search applications using Elasticsearch. " +
                "You'll ramp up fast, with an informative overview and " +
                "an engaging introductory example.",
                "23-FEB-2017");
        Book testBook = bookService.save(book);

        assertNotNull(testBook.getId());
        assertEquals(testBook.getTitle(), book.getTitle());
        assertEquals(testBook.getAuthor(), book.getAuthor());
        assertEquals(testBook.getReleaseDate(), book.getReleaseDate());
    }

    @Test
    public void testFindOne() {
        Book book = new Book("1001", "Elasticsearch in Action", "Radu Gheorghe", "No content", "23-FEB-2017");
        bookService.save(book);

        Book testBook = bookService.findOne(book.getId());

        assertNotNull(testBook.getId());
        assertEquals(testBook.getTitle(), book.getTitle());
        assertEquals(testBook.getAuthor(), book.getAuthor());
        assertEquals(testBook.getReleaseDate(), book.getReleaseDate());
    }

    @Test
    public void testFindByTitle() {
        Book book = new Book("1001", "Elasticsearch in Action", "Radu Gheorghe", "Content is this", "23-FEB-2017");
        bookService.save(book);

        List<Book> byTitle = bookService.findByTitle(book.getTitle(), PageRequest.of(0, 10));
        assertThat(byTitle.size(), is(1));
    }

    @Test
    public void testFindByAuthor() {
        List<Book> bookList = new ArrayList<>();
        bookList.add(new Book("1001", "Elasticsearch in Action", "Radu Gheorghe", "Content is this", "23-FEB-2017"));
        bookList.add(new Book("1002", "Apache Lucene Basics", "Radu Gheorghe", "Content is this", "13-MAR-2017"));
        bookList.add(new Book("1003", "Apache Solr Basics", "Radu Gheorghe", "Content is this", "21-MAR-2017"));
        bookList.add(new Book("1007", "Spring Data + ElasticSearch", "Radu Gheorghe", "Content is this", "01-APR-2017"));
        bookList.add(new Book("1008", "Spring Boot + MongoDB", "Mkyong", "Content is this", "25-FEB-2017"));

        for (Book book : bookList) {
            bookService.save(book);
        }

        Page<Book> byAuthor = bookService.findByAuthor("Radu Gheorghe", PageRequest.of(0, 10));
        assertThat(byAuthor.getTotalElements(), is(4L));

        Page<Book> byAuthor2 = bookService.findByAuthor("Mkyong", PageRequest.of(0, 10));
        assertThat(byAuthor2.getTotalElements(), is(1L));
    }

    @Test
    public void testDelete() {

        Book book = new Book("1001", "Elasticsearch in Action", "Radu Gheorghe", "Content is this", "23-FEB-2017");
        bookService.save(book);
        bookService.delete(book);
        Book testBook = bookService.findOne(book.getId());
        assertNull(testBook);
    }

    @Test
    public void testCriteriaSearch() {
        List<Book> bookList = new ArrayList<>();
        bookList.add(new Book("1001", "Elasticsearch in Action", "Radu Gheorghe", "Content is this", "23-FEB-2017"));
        bookList.add(new Book("1002", "Apache Lucene Basics", "Radu Gheorghe", "Content is this", "13-MAR-2017"));
        bookList.add(new Book("1003", "Apache Solr Basics", "Radu Gheorghe", "Content is this", "21-MAR-2017"));
        bookList.add(new Book("1007", "Spring Data + ElasticSearch", "Radu Gheorghe", "Content is this", "01-APR-2017"));
        bookList.add(new Book("1008", "Spring Boot + MongoDB", "Mkyong", "Content is this", "25-FEB-2017"));

        for (Book book : bookList) {
            bookService.save(book);
        }

        List<Book> byAuthor = bookService.findUseCriteriaQuery("Radu Gheorghe", PageRequest.of(0, 10));
        assertThat(byAuthor.size(), is(4));
    }

    @Test
    public void testNativeCriteriaSearch() {
        List<Book> bookList = new ArrayList<>();
        bookList.add(new Book("1001", "Elasticsearch in Action", "Radu Gheorghe", "Content is this", "23-FEB-2017"));
        bookList.add(new Book("1002", "Apache Lucene Basics", "Radu Gheorghe", "Content is this", "13-MAR-2017"));
        bookList.add(new Book("1003", "Apache Solr Basics", "Radu Gheorghe", "Content is this", "21-MAR-2017"));
        bookList.add(new Book("1007", "Spring Data + ElasticSearch", "Radu Gheorghe", "Content is this", "01-APR-2017"));
        bookList.add(new Book("1008", "Spring Boot + MongoDB", "Mkyong", "Content is this", "25-FEB-2017"));

        for (Book book : bookList) {
            bookService.save(book);
        }

        List<Book> byAuthor = bookService.findUseNativeSearchQueryBuilder("Spring", PageRequest.of(0, 10));
        assertThat(byAuthor.size(), is(2));
    }


    @Test
    public void testMustSearch() {
        List<Book> bookList = new ArrayList<>();
        bookList.add(new Book("1001", "Elasticsearch in Action", "Radu Gheorghe", "Content is this", "23-FEB-2017"));
        bookList.add(new Book("1002", "Apache Lucene Basics", "Radu Gheorghe", "Content is this", "13-MAR-2017"));
        bookList.add(new Book("1003", "Apache Solr Basics", "Radu Gheorghe", "Content is this", "21-MAR-2017"));
        bookList.add(new Book("1007", "Spring Data + ElasticSearch", "Radu Gheorghe", "Content is this", "01-APR-2017"));
        bookList.add(new Book("1008", "Spring Boot + MongoDB", "Mkyong", "Content is this", "25-FEB-2017"));

        for (Book book : bookList) {
            bookService.save(book);
        }

        List<Book> byAuthor = bookService.findUseBoolNativeSearchQueryBuilder("Spring", "Radu Gheorghe", PageRequest.of(0, 10));
        assertThat(byAuthor.size(), is(2));
    }

    @Test
    public void testQueryInRepository() {
        List<Book> bookList = new ArrayList<>();
        bookList.add(new Book("1001", "Elasticsearch in Action", "Radu Gheorghe", "Content is this", "23-FEB-2017"));
        bookList.add(new Book("1002", "Apache Lucene Basics", "Radu Gheorghe", "Content is this", "13-MAR-2017"));
        bookList.add(new Book("1003", "Apache Solr Basics", "Radu Gheorghe", "Content is not", "21-MAR-2017"));
        bookList.add(new Book("1007", "Spring Data + ElasticSearch", "Radu Gheorghe", "Content is this", "01-APR-2017"));
        bookList.add(new Book("1008", "Spring Boot + MongoDB", "Mkyong", "Content is not", "25-FEB-2017"));

        for (Book book : bookList) {
            bookService.save(book);
        }

        List<Book> byAuthor = bookService.findUseNativeSearchQueryInRepository("Content this", PageRequest.of(0, 10));
        assertThat(byAuthor.size(), is(3));
    }

}
