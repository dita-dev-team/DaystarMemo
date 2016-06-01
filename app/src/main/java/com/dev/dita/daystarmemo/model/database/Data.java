package com.dev.dita.daystarmemo.model.database;

import android.content.Context;
import android.util.Log;

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
        //realm.beginTransaction();
        //realm.deleteAll();
        //realm.commitTransaction();
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

        long count = realm.where(Memo.class).count();
        if (count < DATA_SIZE) {
            Log.i("TAG", String.valueOf(count));
            int size = (int) (DATA_SIZE - count);
            String[] words = src.split(" ");
            String[] sentences = src.split("\n");
            Random rand = new Random();

            for (int i = 0; i < size; i++) {
                String word = words[rand.nextInt(words.length)];
                String sentence = sentences[rand.nextInt(sentences.length)].trim();
                User user = users.get(rand.nextInt(users.size()));
                if (rand.nextBoolean()) {
                    sendMemo(realm, word, sentence, user);
                } else {
                    receiveMemo(realm, word, sentence, user);
                }
            }
            count = realm.where(Memo.class).count();
            Log.i("TAG", String.valueOf(count));
        }
        realm.close();
    }

    public void sendMemo(Realm realm, String subject, String body, User user) {
        realm.beginTransaction();
        Memo memo = new Memo();
        memo.isMe = true;
        memo.recipient = user;
        if (memo.recipient.memos.size() > 0) {
            Memo temp = memo.recipient.memos.where().equalTo("latest", true).findFirst();
            temp.latest = false;
        }
        memo.latest = true;
        memo.subject = subject;
        memo.body = body;
        memo.status = "toBeSent";
        memo.date = new Date();
        memo.recipient.memos.add(memo);
        realm.commitTransaction();
    }

    public void receiveMemo(Realm realm, String subject, String body, User user) {
        realm.beginTransaction();
        Memo memo = new Memo();
        memo.isMe = false;
        memo.sender = user;
        if (memo.sender.memos.size() > 0) {
            Memo temp = memo.sender.memos.where().equalTo("latest", true).findFirst();
            temp.latest = false;
        }
        memo.latest = true;
        memo.subject = subject;
        memo.body = body;
        memo.status = status[new Random().nextInt(status.length)];
        memo.date = new Date();
        memo.sender.memos.add(memo);
        realm.commitTransaction();
    }

}
