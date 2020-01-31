package com.example.mayday;

import android.os.AsyncTask;
import android.util.Log;

public class MailSenderActivity extends AsyncTask<String, Void, Boolean> {
    /**
     * sender
     * password
     * recipients
     * subject
     * body
     */
    protected Boolean doInBackground(String... msgs) {
        try {
            GMailSender sender = new GMailSender(msgs[0], msgs[1]);
            sender.sendMail(msgs[3],
                    msgs[4],
                    msgs[0],
                    msgs[2]);
        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
            return false;
        }
        return true;
    }
}
