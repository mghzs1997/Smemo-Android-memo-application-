package com.lfwl.smemo;

/**
 * Created by mgh on 2017/11/25.
 */

public class Memo {
    long id;
    String text, date;

    Memo() {}

    Memo(String text, String date) {
        this.text = text;
        this.date = date;
    }

}
