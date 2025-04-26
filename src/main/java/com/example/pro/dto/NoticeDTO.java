package com.example.pro.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NoticeDTO {
    public Long id;
    public String title;
    public String writer;
    public String content;
    public boolean important;


    public String getNoticeType() {
        return important ? "중요 공지사항" : "일반 공지사항";
    }
}
