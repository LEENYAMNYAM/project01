package com.example.pro.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Log4j2
public class FileServiceImpl implements FileService {

    private static final String UPLOAD_DIR = "D:/JMT/Project01/projerct01/src/main/resources/static/assets";

    @Override
    public String saveFile(MultipartFile file) throws IOException {
        String originalName = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String saveName = uuid + "_" + originalName;
        Path savePath = Paths.get(UPLOAD_DIR, saveName);

        Files.copy(file.getInputStream(), savePath);
        return "/assets/" + saveName;
    }

    @Override
    public void deleteFile(String fileName) {

    }

    @Override
    public String getFileName(String fileUrl) {
        return "";
    }
}
