package com.example.mayday;

import android.os.AsyncTask;
import android.util.Log;

public class MailSenderActivity extends AsyncTask<String, Void, Boolean> {
    protected Boolean doInBackground(String... msgs) {
        try {
            GMailSender sender = new GMailSender("babyyodaisop@gmail.com", "starwars@2016");
            sender.sendMail("This is Subject",
                    "This is Body",
                    "babyyodaisop@gmail.com",
                    "nikhil.celtics@gmail.com");
        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
            return false;
        }
        return true;
    }
}
