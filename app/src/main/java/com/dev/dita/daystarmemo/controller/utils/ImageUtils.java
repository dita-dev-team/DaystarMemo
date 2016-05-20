package com.dev.dita.daystarmemo.controller.utils;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class ImageUtils {

    public static Bitmap decodeBitmapfromString(String src) {
        Bitmap bitmap;
        byte[] imageAsBytes = Base64.decode(src.getBytes(), Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
        return bitmap;
    }

    public static String decodeBitmaptoString(Bitmap bitmap) {
        String imageString;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
        byte[] image = stream.toByteArray();
        imageString = Base64.encodeToString(image, 0);
        return imageString;
    }
}
