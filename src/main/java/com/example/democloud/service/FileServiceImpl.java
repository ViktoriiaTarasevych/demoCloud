package com.example.democloud.service;

import com.example.democloud.dto.FileDto;
import com.example.democloud.entity.InputFile;
import com.example.democloud.exception.BadRequestException;
import com.example.democloud.exception.GCPFileUploadException;
import com.example.democloud.repository.FileRepository;
import com.example.democloud.util.DataBucketUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FileServiceImpl implements FileService{

    private static final Logger LOGGER = LoggerFactory.getLogger(FileServiceImpl.class);

    private final FileRepository fileRepository;
    private final DataBucketUtil dataBucketUtil;

    public List<InputFile> uploadFiles(MultipartFile[] files) {

        LOGGER.debug("Start file uploading service");
        List<InputFile> inputFiles = new ArrayList<>();

        Arrays.asList(files).forEach(file -> {
            String originalFileName = file.getOriginalFilename();
            if(originalFileName == null){
                throw new BadRequestException("Original file name is null");
            }
            Path path = new File(originalFileName).toPath();

            try {
                String contentType = Files.probeContentType(path);
                FileDto fileDto = dataBucketUtil.uploadFile(file, originalFileName, contentType);

                if (fileDto != null) {
                    inputFiles.add(new InputFile(fileDto.getFileName(), fileDto.getFileUrl()));
                    LOGGER.debug("File uploaded successfully, file name: {} and url: {}",fileDto.getFileName(), fileDto.getFileUrl() );
                }
            } catch (Exception e) {
                LOGGER.error("Error occurred while uploading. Error: ", e);
                throw new GCPFileUploadException("Error occurred while uploading");
            }
        });

        fileRepository.saveAll(inputFiles);
        LOGGER.debug("File details successfully saved in the database");
        return inputFiles;
    }
}