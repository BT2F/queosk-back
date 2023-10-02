package com.bttf.queosk.entity;

import com.bttf.queosk.entity.baseentity.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;

import javax.persistence.*;

@Entity(name = "review")
@AuditOverride(forClass = BaseTimeEntity.class)
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Review extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "restorant_id")
    private Restaurant restaurant;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String subject;

    private String content;

    private Double rate;

    private String imageUrl;

    private Boolean isDeleted;

    private Integer commentNum;

    public void setReview(String subject, String content, Double rate) {
        this.subject = subject;
        this.content = content;
        this.rate = rate;
    }

    public void addComment() {
        this.commentNum ++;
    }

    public void deleteComment() {
        this.commentNum--;
    }

    public void delete() {
        this.isDeleted = true;
    }

}
