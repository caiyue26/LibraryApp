// reviewed on 2024.07.14

package com.luv2code.spring_boot_library.service;

import com.luv2code.spring_boot_library.dao.ReviewRepository;
import com.luv2code.spring_boot_library.entity.Review;
import com.luv2code.spring_boot_library.requestmodels.ReviewRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;


@Service
@Transactional
public class ReviewService {

    private ReviewRepository reviewRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    /* This is method is used for posting a new review but ensures that a duplicate review does not exist before saving the new review. if a review exists, it stops and reports an error; otherwise, it proceeds to save the new review*/
    public void postReview(String userEmail, ReviewRequest reviewRequest) throws Exception { // reviewRequest: an object containing the review data
        //Data retrieval: this check is to see if a review by this user for this specific book already exists.
        Review validateReview = reviewRepository.findByUserEmailAndBookId(userEmail, reviewRequest.getBookId());
        if (validateReview != null) {
            throw new Exception("Review already created");
        }

        Review review = new Review();
        // set the bookID of the review to the bookID from the reviewRequest
        review.setBookId(reviewRequest.getBookId());
        review.setRating(reviewRequest.getRating());
        review.setUserEmail((userEmail));
        // This line checks if there is a description provided in the reviewRequest.
        if (reviewRequest.getReviewDescription().isPresent()) {
            // if a description is present, it maps the description object to a string and sets it to the review.
            review.setReviewDescription(reviewRequest.getReviewDescription().map(
                    Object::toString
            ).orElse(null));
        }
        // set the date of the review to the current date
        review.setDate(Date.valueOf(LocalDate.now()));
        // save the newly created review to the reviewRepository
        reviewRepository.save(review);
    }

    /* check whether a user has posted a review for this book or not */
    public Boolean userReviewListed(String userEmail, Long bookId) {
        Review validateReview = reviewRepository.findByUserEmailAndBookId(userEmail, bookId);
        if (validateReview != null) {
            return true;
        } else {
            return false;
        }
    }
}
