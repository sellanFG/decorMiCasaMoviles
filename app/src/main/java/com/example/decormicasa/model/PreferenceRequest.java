package com.example.decormicasa.model;

public class PreferenceRequest {
    private int code;
    private String message;
    private Data data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private String init_point;

        public String getInitPoint() {
            return init_point;
        }

        public void setInitPoint(String init_point) {
            this.init_point = init_point;
        }
    }
}