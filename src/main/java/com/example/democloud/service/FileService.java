package com.example.democloud.service;

import com.example.democloud.entity.InputFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

    List<InputFile> uploadFiles(MultipartFile[] files);
}
