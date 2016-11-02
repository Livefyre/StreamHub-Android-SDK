package com.livefyre.streamhub_android_sdk.util;

import android.util.Base64;

import java.io.UnsupportedEncodingException;

public class Helpers {
    public static String generateBase64String(String inString)
            throws UnsupportedEncodingException
    {
        byte[] byteTransform = inString.getBytes("UTF-8");
        return Base64.encodeToString(byteTransform, Base64.NO_WRAP);
    }
}
