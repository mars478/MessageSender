package com.imp.msender.entity.service;

import java.util.Date;
import java.text.SimpleDateFormat;
import com.imp.msender.entity.Message;
import org.apache.commons.lang3.StringUtils;

public class Util {

    public static String getExpireString(Date msgExpire) {
        String dayExpire = new SimpleDateFormat("yyyy-MM-dd").format(msgExpire);
        dayExpire = ((dayExpire.equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date())))
                ? " сегодня в " + new SimpleDateFormat("HH:mm").format(msgExpire) // снятие блокировки сегодня
                : new SimpleDateFormat("dd-MM-yyyy HH:mm").format(msgExpire)) + "</b>"; // другой день

        return "{ expireDate : '#1' }".replace("#1", dayExpire);
    }

    public static boolean verifyMessage(Message msg) {
        return !(msg == null || msg.getExpireDate() == null || msg.getId() == 0
                || ((StringUtils.isBlank(msg.getHeader())
                || StringUtils.isBlank(msg.getBody())) && (!Message.LOGOUT.equals(msg.getAction()) && !Message.LOGOUT_WARNING.equals(msg.getAction()))));
    }
}
