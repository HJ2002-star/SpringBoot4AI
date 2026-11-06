package me.shinsunyoung.springbootdeveloper.service;

import me.shinsunyoung.springbootdeveloper.dto.UploadResponse;

public interface FileStorageService {
    UploadResponse store(byte[] bytes, String filename);
}
