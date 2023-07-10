package org.example.service;

import java.io.*;

public class FileServiceImpl implements FileService {

    @Override
    public void writeFile(String path, String text) {
        try {
            File file = new File(path);
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(text.getBytes());
            outputStream.close();
        } catch (Exception e) {

        }
    }

    @Override
    public String readFile(String path) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            File file = new File(path);
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String row;

            while ((row = bufferedReader.readLine()) != null) {
                stringBuilder.append(row);
            }

        } catch (Exception e) {
            System.out.println("Error File Read: " + e.getMessage());
        }

        return stringBuilder.toString();
    }
}
