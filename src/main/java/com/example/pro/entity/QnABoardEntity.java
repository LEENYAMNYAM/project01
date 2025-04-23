package com.example.pro.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name="q_board")
@NoArgsConstructor
@AllArgsConstructor
public class QnABoardEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long qno;
    private String title;
    private String writer;
    private String content;

    @CreationTimestamp
    @Column(name="regdate")
    @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    private Long hitcount;

    public void change(String title, String content) {
        this.title = title;
        this.content = content;
    }

    @PrePersist
    public void prePersist() {
        this.hitcount = this.hitcount == null ? 0 : this.hitcount;
    }

    public void updateHitcount() {
        this.hitcount += 1;
    }

}

