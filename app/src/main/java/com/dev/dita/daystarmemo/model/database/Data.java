package com.dev.dita.daystarmemo.model.database;

import android.content.Context;

import com.dev.dita.daystarmemo.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import io.realm.Realm;

public class Data {
    final int DATA_SIZE = 50;
    final String[] status = {
            "read",
            "unread"
    };
    final ArrayList<User> users = new ArrayList<>();
    String src;

    public Data(Context context) {
        src = context.getResources().getString(R.string.large_text);
    }

    public void fillData() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
        if (realm.where(User.class).count() == 0) {
            for (int i = 0; i < 15; i++) {
                String number = new Integer(i + 1).toString();
                String name = "Default" + number;
                realm.beginTransaction();
                User user = new User();
                user.username = name;
                user.name = name;
                user = realm.copyToRealm(user);
                realm.commitTransaction();
                users.add(user);
            }
        }

        int count = (int) realm.where(Memo.class).count();
        if (count < DATA_SIZE) {
            int size = DATA_SIZE - count;
            String[] words = src.split(" ");
            String[] sentences = src.split("\n");
            Random rand = new Random();

            for (int i = 0; i < size; i++) {
                String word = words[rand.nextInt(words.length)];
                String sentence = sentences[rand.nextInt(sentences.length)].trim();
                realm.beginTransaction();
                Memo memo = new Memo();
                memo.sender = users.get(rand.nextInt(users.size()));
                memo.subject = word;
                memo.body = sentence;
                memo.status = status[rand.nextInt(status.length)];
                memo.date = new Date();
                if (memo.sender.memos.size() > 0) {
                    Memo temp = memo.sender.memos.where().equalTo("latest", true).findFirst();
                    temp.latest = false;
                }
                memo.latest = true;
                memo = realm.copyToRealm(memo);
                memo.sender.memos.add(memo);
                realm.commitTransaction();
            }
        }
        realm.close();
    }

}
