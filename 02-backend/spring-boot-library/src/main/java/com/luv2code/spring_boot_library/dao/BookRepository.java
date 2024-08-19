// added notes on 2024.07.12

package com.luv2code.spring_boot_library.dao;
import com.luv2code.spring_boot_library.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

// second parameter in <> -> type of Book's PK -> id : Long
public interface BookRepository extends JpaRepository<Book, Long> {

    Page<Book> findByTitleContaining(@RequestParam("title") String title, Pageable pageable);
    Page<Book> findByCategory(@RequestParam("Category") String category, Pageable pageable);

    @Query("select o from Book o where id in :book_ids")
    List<Book> findBooksByBookIds(@Param("book_ids") List<Long> bookId);
}

/*
 * Interface Declaration:
 * extends JpaRepositroy:
 * inherits several methods including CRUD operations: Create, Read, Update, Delete
 * Book: the entity type this repository will manage
 * Long: the type of the primary key of the entity
 *
 * findByTitleContaining:
 * Page<Book>: indicates that the results are paginated.
 * Page is a Spring Data class that contains a list of entities (Book) and additional information about the pagination.
 * @RequestParam: This annotation tells Spring to use the value of a request parameter named 'title' as the method's 'title' parameter.
 * e.g., URL: 'http://yourapi/books?title=Java'
 * Pageable: This is a parameter used to control pagination information like page number, size, and sorting.
 */