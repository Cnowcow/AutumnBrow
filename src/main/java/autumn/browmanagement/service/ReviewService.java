package autumn.browmanagement.service;

import autumn.browmanagement.DTO.ReviewDTO;
import autumn.browmanagement.Entity.Review;
import autumn.browmanagement.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;


    // 리뷰 조회

    public List<ReviewDTO> reviewList(){
        List<Review> reviews = reviewRepository.findAllByOrderByReviewIdDesc();
        List<ReviewDTO> reviewDTOS = new ArrayList<>();

        for (Review review : reviews){
            ReviewDTO reviewDTO = new ReviewDTO();
            reviewDTO.setReviewId(review.getReviewId());
            reviewDTO.setTitle(review.getTitle());
            reviewDTO.setContent(review.getContent());
            reviewDTO.setReviewUrl(review.getReviewUrl());
            reviewDTO.setRevieDate(review.getRevieDate());

            reviewDTOS.add(reviewDTO);
        }
        return reviewDTOS;
    }
}
