package com.imp.msender.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class BlockMessagePair {

    List<String> logins = new ArrayList<>();

    final TempMsg info;
    final TempMsg block;

    public BlockMessagePair(Message infoMsg, String blockHeader, String blockText, int blockInterval, int blockRepeats, Date blockExpire) {
        info = TempMsg.fromMessage(infoMsg);
        block = TempMsg.fillBlock(infoMsg, blockHeader, blockText, blockInterval, blockRepeats, blockExpire);
    }

    public Message getInfo() {
        return info.toMessage();
    }

    public Message getBlock() {
        return block.toMessage();
    }

    static class TempMsg {

        public int repeats = 1;
        public int interval = 15; // minutes
        public String header = "";
        public String body = "";
        public String action = Message.LOGOUT_WARNING;
        public Date startDate = new Date();
        public Date expireDate = new Date();
        public List<String> logins = new ArrayList<>();

        public TempMsg() {
        }

        public static TempMsg fillBlock(Message infoMsg, String blockHeader, String blockText, int blockInterval, int blockRepeats, Date blockExpire) {
            TempMsg t = new TempMsg();
            t.action = Message.LOGOUT;
            if (StringUtils.isNotBlank(blockText)) {
                t.body = blockText;
            }
            if (blockExpire != null) {
                t.expireDate = blockExpire;
            }
            if (StringUtils.isNotBlank(blockHeader)) {
                t.header = blockHeader;
            }
            if (blockInterval > -2) {
                t.interval = blockInterval;
            }
            if (infoMsg.getLogins() != null) {
                t.logins = infoMsg.getLogins();
            }
            if (blockRepeats > -2) {
                t.repeats = blockRepeats;
            }
            if (infoMsg.getExpireDate() != null) {
                t.startDate = infoMsg.getExpireDate();
            }
            return t;
        }

        public static TempMsg fromMessage(Message msg) {
            TempMsg t = new TempMsg();
            if (StringUtils.isNotBlank(msg.getBody())) {
                t.body = msg.getBody();
            }
            if (msg.getExpireDate() != null) {
                t.expireDate = msg.getExpireDate();
            }
            if (StringUtils.isNotBlank(msg.getHeader())) {
                t.header = msg.getHeader();
            }
            if (msg.getInterval() > -2) {
                t.interval = msg.getInterval();
            }
            if (msg.getLogins() != null) {
                t.logins = msg.getLogins();
            }
            if (msg.getRepeats() > -2) {
                t.repeats = msg.getRepeats();
            }
            if (msg.getStartDate() != null) {
                t.startDate = msg.getStartDate();
            }
            return t;
        }

        public Message toMessage() {
            return new Message(repeats, interval, header, body, action, startDate, expireDate, logins);
        }
    }
}
