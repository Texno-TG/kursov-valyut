package org.example.service;

public interface FileService {
    void writeFile(String path, String text);

    String readFile(String path);
}
