package com.dev.dita.daystarmemo.model.database;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject {
    @PrimaryKey
    public String username;
    public String name;
    public String image;
    public Boolean isGroup;
    public RealmList<Memo> memos;
}
