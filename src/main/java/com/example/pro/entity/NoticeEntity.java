package com.example.pro.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity(name = "notice")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String writer;
    @Column(length = 5000)
    private String content;
    private Long hitcount;

    private boolean important; // 중요 여부 표시, true면 중요 공지, false면 일반 공지

    public void change(String title, String content, boolean important, String writer) {
        this.title = title;
        this.content = content;
        this.important = important;
        this.writer = writer;
    }

    public String getNoticeType() {
        return important ? "중요 공지사항" : "일반 공지사항";
    }

    public void updateHitcount() {
        this.hitcount = (this.hitcount == null ? 0 : this.hitcount) + 1;
    }
}
