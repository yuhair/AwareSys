package com.example.hai.awaresys.model;

import com.example.hai.awaresys.dao.UUIDGenerator;

/**
 * this class is the object-oriented process for skype table
 *
 * @author Hai Yu
 */

public class Skype {
    private String id;
    private String path;
    private String account;


    public Skype() {
        id = UUIDGenerator.getUUID();
        path = "";
        account = "";
    }

    public Skype(String id, String path, String account) {
        this.id = id;
        this.path = path;
        this.account = account;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }


}
