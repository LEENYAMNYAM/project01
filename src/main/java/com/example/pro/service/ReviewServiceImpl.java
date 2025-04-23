package com.example.pro.service;

import com.example.pro.dto.ReviewDTO;
import com.example.pro.entity.ReviewEntity;
import com.example.pro.repository.ReviewRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;
    @Override
    public void registerReview(ReviewDTO reviewDTO) {
        ReviewEntity review = dtoToEntity(reviewDTO);
        reviewRepository.save(review);
    }

    @Override
    public ReviewEntity readReview(Long id) {
        ReviewEntity foundReview = reviewRepository.findById(id).orElse(null);
        return foundReview;
    }

    @Override
    public void updateReview(Long id, ReviewDTO reviewDTO) {
        ReviewEntity reviewEntity = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("리뷰가 존재하지 않습니다."));
        reviewEntity.change1(reviewDTO.getContent(), reviewDTO.getRating());
        reviewRepository.save(reviewEntity);
    }


    @Override
    public void deleteReview(Long id) {reviewRepository.deleteById(id);
    }
}
