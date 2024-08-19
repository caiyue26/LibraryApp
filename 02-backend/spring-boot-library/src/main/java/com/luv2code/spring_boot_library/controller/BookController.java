// added notes on 2024.07.14

package com.luv2code.spring_boot_library.controller;

import com.luv2code.spring_boot_library.entity.Book;
import com.luv2code.spring_boot_library.responsemodels.ShelfCurrentLoansResponse;
import com.luv2code.spring_boot_library.service.BookService;
import com.luv2code.spring_boot_library.utils.ExtractJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

// This annotation allows cross-origin requests from 3000. This is useful for enabling FE applications running on a
// different domain to access this API.
@CrossOrigin("https://localhost:3000")
// This annotation marks the class as a REST controller, meaning it will handle HTTP requests and return JSON responses.
@RestController
// This annotation specifies that all the endpoints in this controller will be prefixed with /api/books
@RequestMapping("/api/books")
public class BookController {

    private BookService bookService;

    // This annotation is used for dependency injection. It tells Spring to inject the BookService bean into the
    // controller.
    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // added for ShelfCurrentLoanResponse
    @GetMapping("/secure/currentloans")
    public List<ShelfCurrentLoansResponse> currentLoans(@RequestHeader(value = "Authorization") String token) throws Exception {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "https://claims.yuecai.com/email");
        return bookService.currentLoans(userEmail);
    }

    @GetMapping("/secure/currentloans/count")
    public int currentLoansCount(@RequestHeader(value = "Authorization") String token) {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "https://claims.yuecai.com/email");
        return bookService.currentLoansCount(userEmail);
    }

    @GetMapping("/secure/ischeckedout/byuser")
    public Boolean checkoutBookByUser(@RequestHeader(value = "Authorization") String token, @RequestParam Long bookId) {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "https://claims.yuecai.com/email");
        //String userEmail = "testuser@email.com";
        return bookService.checkoutBookByUser(userEmail, bookId);
    }

    @PutMapping("/secure/checkout")
    public Book checkoutBook (@RequestHeader(value = "Authorization") String token, @RequestParam Long bookId) throws Exception {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "https://claims.yuecai.com/email");
        return bookService.checkoutBook(userEmail, bookId);
    }

    @PutMapping("/secure/return")
    public void returnBook(@RequestHeader(value = "Authorization") String token, @RequestParam Long bookId) throws Exception {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "https://claims.yuecai.com/email");
        bookService.returnBook(userEmail, bookId);
    }

    @PutMapping("/secure/renew/loan")
    public void renewLoan(@RequestHeader (value = "Authorization") String token, @RequestParam Long bookId) throws Exception {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "https://claims.yuecai.com/email");
        bookService.renewLoan(userEmail,bookId);
    }
}
