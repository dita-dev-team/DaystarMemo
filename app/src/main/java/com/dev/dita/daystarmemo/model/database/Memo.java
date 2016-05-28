package com.dev.dita.daystarmemo.model.database;


import java.util.Date;

import io.realm.RealmObject;

public class Memo extends RealmObject {
    public User sender;
    public String subject;
    public String body;
    public String status;
    public Date date;
}
