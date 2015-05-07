package com.imp.msender.entity;

import java.net.URI;
import java.net.URL;
import java.io.Serializable;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.net.MalformedURLException;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Server implements Serializable {

    private static final Pattern pAuth = Pattern.compile("^(https|http)://(\\w+):(\\w+)@([^@]+?)$");
    private static final Pattern pNonAuth = Pattern.compile("^(https|http)://([^@]+?)$");
    private static long counter = 1;
    private static int ERROR_PORT_VAL = -2;

    long id = -1;
    int port = -1;
    boolean connected = false;
    boolean added = true;
    String protocol;
    String link;
    String username = "tomcat";
    String password = "tomcat";

    public Server() {
        id = getNewId();
    }

    public Server(String url) {
        this();
        setUrl(url);
    }

    public Server(String url, String username, String password) {
        this(url);
        if (username != null && password != null) {
            this.username = username;
            this.password = password;
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public boolean isAdded() {
        return added;
    }

    public void setAdded(boolean added) {
        this.added = added;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @JsonIgnore
    public String getUrl() {
        String pwd = StringUtils.defaultString(password);
        String usr = StringUtils.defaultString(username);
        String lnk = StringUtils.defaultString(link);
        String ptcl = StringUtils.defaultString(protocol);
        String prt = port == -1 ? "" : (":" + port);
        
        String url = ptcl + "://" + lnk;
        int dIndex = url.indexOf("/", url.indexOf("//") + 2);
        url = (dIndex > -1)
                ? url.substring(0, dIndex) + prt + url.substring(dIndex)
                : url + prt;

        return (StringUtils.isNotBlank(usr) && StringUtils.isNotBlank(pwd))
                ? url.replace("://", "://" + usr + ":" + pwd + "@")
                : url;
    }

    @JsonIgnore
    public final String setUrl(String url) {
        try {
            URL u = new URL(url);
            setPort(u.getPort());
            setProtocol(u.getProtocol());
            setLink(u.getHost() + StringUtils.defaultString(u.getPath()));
            String uInfo = u.getUserInfo();
            if (StringUtils.isNotBlank(uInfo)) {
                String[] t = uInfo.split(":");
                setUsername(t[0]);
                setPassword(t[1]);
            }
            return getUrl();
        } catch (MalformedURLException ex) {
            port = ERROR_PORT_VAL;
            return null;
        }
    }

    @JsonIgnore
    public URI getBaseURI() {
        return getBaseURI(getUrl());
    }

    @JsonIgnore
    protected URI getBaseURI(String base) {
        try {
            if (!base.endsWith("/")) {
                base = base + "/";
            }
            return new URI(base + "rest/");
        } catch (Exception e) {
            return null;
        }
    }

    public boolean correct() {
        return port != ERROR_PORT_VAL && port != -1;
    }

    public boolean eq(Server s) {
        return this.added == s.added
                && this.connected == s.connected
                && this.port == s.port
                && StringUtils.equalsIgnoreCase(this.link, s.link)
                && StringUtils.equalsIgnoreCase(this.password, s.password)
                && StringUtils.equalsIgnoreCase(this.protocol, s.protocol)
                && StringUtils.equalsIgnoreCase(this.username, s.username);
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

    public static long getNewId() {
        return counter++;
    }

}
