package com.example.pro.dto;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeDTO {
    public Long id;
    public String title;
    public String writer;
    public String content;
    public boolean important;
    public Long hitcount;

    public String getNoticeType() {
        return important ? "중요 공지사항" : "일반 공지사항";
    }
}
