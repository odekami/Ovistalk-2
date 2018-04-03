package com.example.android.materialdesigncodelab.activity;

/**
 * Created by studio kami on 06/03/2018.
 */

public class items {
    public String email;
    public String message;
    public String time;
    public String status;

    public items() {
    }

    public items(String email, String message, String time, String status) {
        this.email = email;
        this.message = message;
        this.time = time;
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }
}
