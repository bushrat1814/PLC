package com.example.plc.pojo;

public class JsonString {
    public String key1;
    public String value1;
    public String key2;
    public String value2;

    public JsonString(String key, String value) {
        this.key1 = key;
        this.value1 = value;
    }

    public JsonString(String key1, String value1, String key2, String value2) {
        this.key1 = key1;
        this.value1 = value1;
        this.key2 = key2;
        this.value2 = value2;
    }

}