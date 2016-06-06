package com.dev.dita.daystarmemo.model.objects;

import java.io.Serializable;

/**
 * The type Recipient.
 */
public class Recipient implements Serializable {
    public String username;
    public String name;

    public Recipient(String username, String name) {
        this.username = username;
        this.name = name;
    }

    @Override
    public String toString() {
        return username;
    }
}
