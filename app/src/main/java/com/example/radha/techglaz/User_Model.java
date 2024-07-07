package com.example.radha.techglaz;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User_Model {
    private String name;
    private String email;
    private String phone_no;
    private String password;
    private Boolean isLoggeIn;

    public User_Model(String name, String email, String phone_no,String password,Boolean isLoggeIn) {
        this.name = name;
        this.email = email;
        this.phone_no = phone_no;
        this.password = password;
        this.isLoggeIn =isLoggeIn;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public String getPassword() {
        return password;
    }

    public Boolean getLoggeIn() {
        return isLoggeIn;
    }

    public void setLoggeIn(Boolean loggeIn) {
        isLoggeIn = loggeIn;
    }
}

