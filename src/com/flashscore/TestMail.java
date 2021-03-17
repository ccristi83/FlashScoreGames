package com.flashscore;

import javax.mail.MessagingException;

public class TestMail {



    public static void main(String [] args) throws MessagingException {
        String msg = null;
        if (args[0].contains("today")) msg = "today";
        if (args[0].contains("tomorrow")) msg = "tomorrow";
        Mail mail = new Mail();
        mail.sendMail("Tets mail", msg);
    }

}
