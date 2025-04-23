package com.example.pro.dto.upload;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UploadFileDTO {
    private List<MultipartFile> files;
    /* uploadFrom.html 의 input type="file"의 name속성값이랑 반드시 맞춰야함. */

}
