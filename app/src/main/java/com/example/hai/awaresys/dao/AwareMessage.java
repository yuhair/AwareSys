package com.example.hai.awaresys.dao;

import android.content.Context;
import android.telephony.SmsManager;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Hai on 2016/4/1.
 */
public class AwareMessage {

    Context context;

    public AwareMessage(Context context){
        this.context = context;

    }

    public boolean sendEmails(String message) {
        message = message + "This is an automated message from Aware System";
        Mail m = new Mail("awaresystem@gmail.com", "60563799");
        ContactDao dao = new ContactDao(context.getApplicationContext());
        List<String> emails = dao.getEmails();
        String[] toArr =  emails.toArray(new String[0]);
        m.set_to(toArr);
        m.set_from("awaresystem@gmail.com");
        m.set_subject("Aware System!");
        m.setBody(message);
        try {
            if(m.send()) {
                return true;
            } else {
                Toast.makeText(context, "EMAIL WAS NOT SENT", Toast.LENGTH_LONG).show();
            }
        } catch(Exception e) {
            Toast.makeText(context, "EMAIL WAS NOT SENT", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }

    /**
     * this method is used to send message.
     */
    public boolean sendMessages(String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            ContactDao dao = new ContactDao(context.getApplicationContext());
            List<String> numbers = dao.getNumbers();
            message = message + "This is an automated message from Aware System";
            for (String number : numbers) {
                number = getNumber(number);
                if (number != null)
                    smsManager.sendTextMessage(number, null, message, null, null);
            }
        } catch (Exception e) {
            Toast.makeText(context, "SMS WAS NOT SENT", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public String getNumber(String number) {
        StringBuilder res = new StringBuilder();
        if (number != null && !number.isEmpty()) {
            for (char i : number.toCharArray()) {
                if (Character.isDigit(i))
                    res.append(i);
            }
            if (res.length() == 10) {
                return res.toString();
            }
        }
        return null;
    }

}
