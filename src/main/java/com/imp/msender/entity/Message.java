package com.imp.msender.entity;

import java.util.List;
import java.util.Date;
import java.util.Arrays;
import java.util.ArrayList;
import java.io.Serializable;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Message implements Serializable {

    public static final String INFO = "INFO";
    public static final String LOGOUT = "LOGOUT";
    public static final String LOGOUT_WARNING = "LOGOUT_WARN";
    public static List<String> MSG_TYPES = Arrays.asList(new String[]{INFO, LOGOUT, LOGOUT_WARNING});

    protected static long counter = 1;

    long id = 0;
    int repeats = 1;
    int interval = 15; // minutes
    String header = null;
    String body = null;
    String action = null;
    Date startDate = new Date();
    Date expireDate = null;
    List<String> logins = new ArrayList<>();

    public Message() {
        id = getNewId();
    }

    public Message(int repeats, int interval, String header, String body, String action, Date startDate, Date expireDate, List<String> logins) {
        this();
        this.repeats = repeats;
        this.interval = interval;
        this.header = header;
        this.body = body;
        this.setAction(action);
        this.startDate = startDate;
        this.expireDate = expireDate;
        this.logins = logins;
    }

    public static long getNewId() {
        return counter++;
    }

    @Override
    public String toString() {
        String loginsStr = "";
        for (String str : logins) {
            loginsStr = loginsStr + "," + str;
        }
        loginsStr = loginsStr.replaceFirst(",", "");

        return header + "\t" + id + "\t" + "\t" + body + "\texpire date: " + expireDate + "\tstart date: " + startDate + "\t repeats:" + repeats + "\t" + "\tlogins:" + loginsStr;
    }

    //********* get-n-set *****************************
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public int getRepeats() {
        return repeats;
    }

    public void setRepeats(int repeats) {
        this.repeats = repeats;
    }

    public List<String> getLogins() {
        return logins;
    }

    public void setLogins(List<String> logins) {
        this.logins = logins;
    }

    public String getAction() {
        return action != null ? action.toUpperCase() : action;
    }

    public final void setAction(String action) {
        action = action != null ? action.toUpperCase() : action;
        this.action = action;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public static boolean verifyMessage(Message msg) {
        return !(msg == null || msg.getExpireDate() == null || msg.getId() == 0 || ((StringUtils.isBlank(msg.getHeader())
                || StringUtils.isBlank(msg.getBody())) && (!Message.LOGOUT.equals(msg.getAction()) && !Message.LOGOUT_WARNING.equals(msg.getAction()))));
    }

}
