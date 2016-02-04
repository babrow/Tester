package com.babrow.tester.model;

import java.io.Serializable;

/**
 * Created by babrow on 31.01.2016.
 */
public class Account implements Serializable {
    private long id;
    private String email;
    private String password;
    private String FName;
    private String LName;
    private String SName;

    public Account() {

    }

    public Account(String email, String password) {
        this.setEmail(email);
        this.setPassword(password);
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFName() {
        return this.FName;
    }

    public void setFName(String FName) {
        this.FName = FName;
    }

    public String getLName() {
        return this.LName;
    }

    public void setLName(String LName) {
        this.LName = LName;
    }

    public String getSName() {
        return this.SName;
    }

    public void setSName(String SName) {
        this.SName = SName;
    }
}