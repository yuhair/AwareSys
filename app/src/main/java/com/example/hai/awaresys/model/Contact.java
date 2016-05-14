package com.example.hai.awaresys.model;


import com.example.hai.awaresys.dao.UUIDGenerator;

import java.io.Serializable;

/**
 * this class is the object-oriented process for contact table
 *
 * @author Hai Yu
 */
public class Contact{

    private String id;
    private String name;
    private String number;
    private String email;

    public Contact()
    {
        id = UUIDGenerator.getUUID();
        name = "";
        number = "";
        email = "";
    }

    public Contact(String id, String name, String number, String email) {
        super();
        this.id = id;
        this.name = name;
        this.number = number;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}
