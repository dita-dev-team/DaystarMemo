package com.dev.dita.daystarmemo.controller.bus;

import com.dev.dita.daystarmemo.model.database.Memo;

import java.util.List;

public class MemoBus {
    public static class SendMemoResult {
        public Boolean error;
        public List<Memo> memos;
    }
}
