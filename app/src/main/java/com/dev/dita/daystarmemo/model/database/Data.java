package com.dev.dita.daystarmemo.model.database;

import android.content.Context;
import android.util.Log;

import com.dev.dita.daystarmemo.R;

import java.util.Date;
import java.util.Random;

import io.realm.Realm;

public class Data {
    final int DATA_SIZE = 20;
    final String[] status = {
            "read",
            "unread"
    };
    String src;

    public Data(Context context) {
        src = context.getResources().getString(R.string.large_text);
    }

    public void fillData() {
        Realm realm = Realm.getDefaultInstance();
        //realm.where(Memo.class).findAll().deleteAllFromRealm();
        int count = realm.where(Memo.class).findAll().size();
        if (count < DATA_SIZE) {
            int size = DATA_SIZE - count;
            String[] words = src.split(" ");
            String[] sentences = src.split("\n");
            for (String s : sentences) {
                Log.i(getClass().getName(), s);
            }
            Random rand = new Random();
            realm.beginTransaction();
            User user = realm.createObject(User.class);
            user.username = "Default";
            user.name = "Default";
            realm.commitTransaction();
            for (int i = 0; i < size; i++) {
                String word = words[rand.nextInt(words.length)];
                String sentence = sentences[rand.nextInt(sentences.length)].trim();
                realm.beginTransaction();
                Memo memo = realm.createObject(Memo.class);
                memo.sender = user;
                memo.subject = word;
                memo.body = sentence;
                memo.status = status[rand.nextInt(status.length)];
                memo.date = new Date();
                realm.commitTransaction();
            }

        }
        realm.close();
    }
}
