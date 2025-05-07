package com.example.pro.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    private final String uploadDir = new File("src/main/resources/static/assets/uploads").getAbsolutePath();

    @Override
    public String saveFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // 파일명 중복 방지를 위해 UUID 붙임
        String originalFilename = file.getOriginalFilename();
        String savedFilename = UUID.randomUUID() + "_" + originalFilename;

        File savePath = new File(uploadDir, savedFilename);

        // 디렉토리 없으면 생성
        savePath.getParentFile().mkdirs();

        try {
            file.transferTo(savePath); // 실제 파일 저장
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패: " + originalFilename, e);
        }

        // 저장된 파일명 반환 (DB에는 이 경로를 저장)
        return "/assets/uploads/" + savedFilename;
    }
}
