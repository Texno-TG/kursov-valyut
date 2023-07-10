package org.example.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class ConnectionImpl implements Connection {
    @Override
    public String openUrl(String url) {
        StringBuilder str = new StringBuilder();
        try {
            URL new_url = new URL(url);
            URLConnection urlConnection = new_url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                str.append(line);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str.toString();
    }
}
