// added notes on 2024.07.13

package com.luv2code.spring_boot_library.service;

import com.luv2code.spring_boot_library.dao.BookRepository;
import com.luv2code.spring_boot_library.dao.CheckoutRepository;
import com.luv2code.spring_boot_library.dao.HistoryRepository;
import com.luv2code.spring_boot_library.dao.PaymentRepository;
import com.luv2code.spring_boot_library.entity.Book;
import com.luv2code.spring_boot_library.entity.Checkout;
import com.luv2code.spring_boot_library.entity.History;
import com.luv2code.spring_boot_library.entity.Payment;
import com.luv2code.spring_boot_library.responsemodels.ShelfCurrentLoansResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

// Annotates the class with @Service to indicate that it is a Spring service component. It is used to define a
// service layer, which contains business logic.
// Annotates the class with @Transactional to indicate that methods in this class should be transactional by default.
@Service
@Transactional
public class BookService {
    // declares private member variables to interact with the database
    private BookRepository bookRepository;
    private CheckoutRepository checkoutRepository;
    private HistoryRepository historyRepository;
    private PaymentRepository paymentRepository;
    // constructor
    public BookService(BookRepository bookRepository, CheckoutRepository checkoutRepository, HistoryRepository historyRepository, PaymentRepository paymentRepository) {
        this.bookRepository = bookRepository;
        this.checkoutRepository = checkoutRepository;
        this.historyRepository = historyRepository;
        this.paymentRepository = paymentRepository;
    }
    /*
    The following operations are part of a single transaction:
    1. Retrieving the book information from the database.
    2. Validating if the user has already checked out the book.
    3. Checking if the book exists and has available copies.
    4. Checking if the user has overdue books or outstanding fees.
    5. Updating the book's available copies and saving the updated information.
    6. Creating a new checkout record and saving it.
    If any of these operations fail, @Transactional ensures that all the previous operations are rolled back,
    maintaining the database's consistency and integrity.
    */
    public Book checkoutBook(String userEmail, Long bookId) throws Exception {
        // Retrieves a book by its ID from the repository. The result is wrapped in an 'Optional' to handle the
        // possibility of the book not being found.
        Optional<Book> book = bookRepository.findById(bookId);

        // Queries the database to check if there is already a checkout record for the user and the bookID.
        Checkout validateCheckout = (Checkout) checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);

        // check if the book exists, if the user has already checked out the book, and if there are available copies of the book.
        // if any of these conditions are not met, an exception is thrown.
        if (!book.isPresent() || validateCheckout != null || book.get().getCopiesAvailable() <= 0) {
            throw new Exception("Book doesn't exist or already checked out by user");
        }

        // added for payment: if a user has books overdue, do not allow the user to check out any more books
        // Retrieves a list of cooks currently checked out by the user.
        List<Checkout> currentBooksCheckedOut = checkoutRepository.findBooksByUserEmail(userEmail);
        // create a date formatter for comparing dates
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // initializes a flag to check if any books need to be returned.
        boolean bookNeedsReturned = false;
        // iterates over the list of checked-out books and calculates the difference in days between the return date
        // and the current date.
        for (Checkout checkout: currentBooksCheckedOut) {
            Date d1 = sdf.parse(checkout.getReturnDate());
            Date d2 = sdf.parse(LocalDate.now().toString());
            TimeUnit time = TimeUnit.DAYS;
            double differenceInTime = time.convert(d1.getTime() - d2.getTime(), TimeUnit.MILLISECONDS);
            // if overdue, set the flag to true and breaks the loop.
            if (differenceInTime < 0) {
                bookNeedsReturned = true;
                break;
            }
        }
        // retrieve user payment information and check for outstanding fees or overdue books.
        Payment userPayment = paymentRepository.findByUserEmail(userEmail);
        if ((userPayment != null && userPayment.getAmount() > 0) || (userPayment != null && bookNeedsReturned)) {
            throw new Exception("Outstanding fees");
        }
        // create a new payment record
        if (userPayment == null) {
            Payment payment = new Payment();
            payment.setAmount(00.00);
            payment.setUserEmail(userEmail);
            paymentRepository.save(payment);
        }
        // decreases the number of available copies of the book by one and saves the updated book information to the database
        book.get().setCopiesAvailable(book.get().getCopiesAvailable() - 1);
        bookRepository.save(book.get());

        // create a new checkout object to record the user's checkout information
        Checkout checkout = new Checkout(
                userEmail,
                LocalDate.now().toString(),
                LocalDate.now().plusDays(7).toString(),
                book.get().getId()
        );

        // save the new checkout record to the database
        checkoutRepository.save(checkout);
        // return the checkout book
        return book.get();
    }

    /* check for whether a book has been checked out by the user */
    public Boolean checkoutBookByUser(String userEmail, Long bookId) {
        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);
        if (validateCheckout != null) {
            return true;
        } else {
            return false;
        }
    }

    /* to check how many books have been checked out altogether*/
    public int currentLoansCount(String userEmail) {
        return checkoutRepository.findBooksByUserEmail(userEmail).size();
    }

    /* This method gathers all the books loaned to a user along with how many days remain until they need to return each book. */
    public List<ShelfCurrentLoansResponse> currentLoans(String userEmail) throws Exception {
        // This list will be used to store the response objects that contain information about each loaned book.
        List<ShelfCurrentLoansResponse> shelfCurrentLoansResponses = new ArrayList<>();
        // Retrieves a list of Checkout objects for the specified user email. Each Checkout object represents a book checked out by the user.
        List<Checkout> checkoutList = checkoutRepository.findBooksByUserEmail(userEmail);
        // get a list of bookIds
        List<Long> bookIdList = new ArrayList<>();
        for (Checkout i: checkoutList) {
            bookIdList.add(i.getBookId());
        }
        // select all books based on bookIds
        List<Book> books = bookRepository.findBooksByBookIds(bookIdList);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (Book book: books) {
            // for each book, it searches checkoutList to find the matching Checkout object using a stream that filters on bookID.
            Optional<Checkout> checkout = checkoutList.stream()
                    .filter(x -> x.getBookId() == book.getId()).findFirst();

            if (checkout.isPresent()) {
                // parse the return date and the current date to Date objects
                Date d1 = sdf.parse(checkout.get().getReturnDate());
                Date d2 = sdf.parse(LocalDate.now().toString());
                // calculate the diff in days between return date and current date
                TimeUnit time = TimeUnit.DAYS;
                long difference_In_Time = time.convert(d1.getTime() - d2.getTime(), TimeUnit.MILLISECONDS);
                // create a new 'ShelfCurrentLoansResponse' object with the 'Book' and the calculated difference in days, and adds this to 'shelfCurrentLoansResponses'
                shelfCurrentLoansResponses.add(new ShelfCurrentLoansResponse(book, (int) difference_In_Time));
            }
        }
        return  shelfCurrentLoansResponses;
    }

    public void returnBook(String userEmail, Long bookId) throws Exception {
        // get the book
        Optional<Book> book = bookRepository.findById(bookId);

        // get the book information from checkourRepository
        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail,bookId);

        // check for validity
        if (!book.isPresent() || validateCheckout == null) {
            throw new Exception("Book does not exist or not checked out by user");
        }

        // update # of copies available
        book.get().setCopiesAvailable(book.get().getCopiesAvailable() + 1);

        // update the new book to bookRepository and checkoutRepository
        bookRepository.save(book.get());

        // added for payment
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = sdf.parse(validateCheckout.getReturnDate());
        Date d2 = sdf.parse(LocalDate.now().toString());
        TimeUnit time = TimeUnit.DAYS;
        double differenceInTime = time.convert(d1.getTime() - d2.getTime(), TimeUnit.MILLISECONDS);
        if (differenceInTime < 0) {
            Payment payment = paymentRepository.findByUserEmail(userEmail);
            payment.setAmount(payment.getAmount() + (differenceInTime * -1));
            paymentRepository.save(payment);
        }

        checkoutRepository.deleteById(validateCheckout.getId());

        // updated for History
        History history = new History(
                userEmail,
                validateCheckout.getCheckoutDate(),
                LocalDate.now().toString(),
                book.get().getTitle(),
                book.get().getAuthor(),
                book.get().getDescription(),
                book.get().getImg()
        );
        historyRepository.save(history);
    }

    public void renewLoan(String userEmail, Long bookId) throws  Exception {
        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);
        if (validateCheckout == null) {
            throw new Exception("Book does not exist or not checked out by user");
        }
        // Only renew the loan if the book is not late
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = sdFormat.parse(validateCheckout.getReturnDate());
        Date d2 = sdFormat.parse(LocalDate.now().toString());
        if (d1.compareTo(d2) > 0 || d1.compareTo(d2) == 0) {
            validateCheckout.setReturnDate(LocalDate.now().plusDays(7).toString());
            checkoutRepository.save(validateCheckout);
        }
    }
}
