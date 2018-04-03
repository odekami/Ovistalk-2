package com.example.android.materialdesigncodelab.activity;

/**
 * Created by studio kami on 06/03/2018.
 */

public class User_data {
    public String Email;
    public String Pin;
    public String Token;
    public String Uid;
    public String Images;

    public User_data() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User_data(String Email, String Pin, String Token, String Uid, String Images) {
        this.Email = Email;
        this.Pin = Pin;
        this.Token = Token;
        this.Uid = Uid;
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

    public String Get_images() {
        return Images;
    }

}
