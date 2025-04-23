package com.example.pro.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "notice")
public class NoticeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String writer;
    private String content;

    private boolean important; // 중요 여부 표시, true면 중요 공지, false면 일반 공지

    public void change(String title, String content, boolean important) {
        this.title = title;
        this.content = content;
        this.important = important;
    }

    public String getNoticeType() {
        return important ? "중요 공지사항" : "일반 공지사항";
    }

}
