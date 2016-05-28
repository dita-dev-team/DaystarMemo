package com.dev.dita.daystarmemo.controller.utils;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";

    public static String dateToString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(date);
    }

    public static Date stringToDate(String str) {
        Date date = null;
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            date = dateFormat.parse(str);
        } catch (ParseException e) {

        }

        return date;
    }
}
