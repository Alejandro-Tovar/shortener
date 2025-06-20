package com.shortener.Utilities;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.SecureRandom;

public class ShortenerUtilities {

    static final String characterSet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static String base62generator() {
        SecureRandom random = new SecureRandom();
        StringBuilder base62Builder = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            base62Builder.append(characterSet.charAt(random.nextInt(characterSet.length())));
        }
        return base62Builder.toString();
    }

    public static boolean isValidURL(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }

}
