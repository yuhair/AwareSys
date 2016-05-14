package com.example.hai.awaresys.model;

/**
 * this class is the object-oriented process for emotion table
 *
 * @author Hai Yu
 */

public class Emotion {
    String id;
    String date;
    String time;
    String emotion;
    String value;


    public Emotion(String id, String date, String time, String emotion, String value) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.emotion = emotion;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String toString(){
        String res = id+ "/"+ date +"/"+ time + "/" + emotion + "/"+value+"/";
        return res;
    }
}
