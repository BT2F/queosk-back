package com.bttf.queosk.repository;

import com.bttf.queosk.entity.Comment;
import com.bttf.queosk.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Comment findByIdAndIsDeletedFalse(Long id);

    List<Comment> findByReviewAndIsDeletedFalse(Review review);
}
