package com.example.pro.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    public String saveFile(MultipartFile file) throws IOException;
    public void deleteFile(String fileName);
    public String getFileName(String fileUrl);

}
