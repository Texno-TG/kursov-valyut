package org.example.models;

public class Lang {

    public String name;
    public String key;

    public Lang(String name, String key) {
        this.name = name;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }
}
