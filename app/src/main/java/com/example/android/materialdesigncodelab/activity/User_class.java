package com.example.android.materialdesigncodelab.activity;

/**
 * Created by studio kami on 06/03/2018.
 */

public class User_class {
    public String Email;
    public String Pin;
    public String Token;
    public String Type;
    public String Uid;
    public String Last_message;
    public int New_message;
    public String Images;

    public User_class() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User_class(String Email, String Pin, String Token, String Type, String Uid, String Last_message, int New_message, String Images) {
        this.Email = Email;
        this.Pin = Pin;
        this.Token = Token;
        this.Type = Type;
        this.Uid = Uid;
        this.Last_message = Last_message;
        this.New_message = New_message;
        this.Images = Images;
    }

    public String Get_user_id() {
        return Uid;
    }

    public String Get_email() {
        return Email;
    }

    public String Get_pin() {
        return Pin;
    }

    public String Get_token() {
        return Token;
    }

    public String Get_type() {
        return Type;
    }

    public String Get_last() {
        return Last_message;
    }

    public int Get_new() {
        return New_message;
    }

    public String Get_images() {
        return Images;
    }
}
