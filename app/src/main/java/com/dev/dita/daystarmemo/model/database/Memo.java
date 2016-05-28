package com.dev.dita.daystarmemo.model.database;


import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Index;

public class Memo extends RealmObject {
    public User sender = null;
    public String subject = "";
    public String body = "";
    public String status = "";
    public Date date;
    @Index
    public Boolean latest = false;
}
