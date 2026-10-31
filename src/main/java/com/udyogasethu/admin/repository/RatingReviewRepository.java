package com.udyogasethu.admin.repository;

import com.udyogasethu.admin.domain.RatingReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface RatingReviewRepository extends JpaRepository<RatingReview, UUID> {
    Page<RatingReview> findByRating(Integer rating, Pageable pageable);
    Page<RatingReview> findByRevieweeId(UUID revieweeId, Pageable pageable);
}
