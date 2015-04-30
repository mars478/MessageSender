package com.imp.msender.entity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {

    private static final Pattern pAuth = Pattern.compile("^(https|http)://(\\w+):(\\w+)@([^@]+?)$");
    private static final Pattern pNonAuth = Pattern.compile("^(https|http)://([^@]+?)$");

    String url;
    String username;
    String password;

    public Server(String url) {
        this.url = url;
        this.username = null;
        this.password = null;
    }

    public Server(String url, String username, String password) {
        this.url = url;
        if (username != null && password != null) {
            this.username = username;
            this.password = password;
        } else {
            this.username = null;
            this.password = null;
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // ***************************************************************************************
    public static Server parse(String str) {
        if (!verify(str)) {
            return null;
        }
        try {
            str = str.replace(" ", "");
            Matcher m = pAuth.matcher(str);
            return (m.find())
                    ? new Server(m.group(1) + "://" + m.group(4), m.group(2), m.group(3))
                    : ((pNonAuth.matcher(str).find()) ? new Server(str) : null);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean verify(String str) {
        return (pAuth.matcher(str).find()) ? true : pNonAuth.matcher(str).find();
    }

}
