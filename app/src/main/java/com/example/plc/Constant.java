package com.example.plc;

import okhttp3.MediaType;

public class Constant {
    public static String apiURL = "http://livingroom.lan:8080/status";
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
}
