package com.imp.msender.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageLog {

    List<Date> dates = null;

    public MessageLog() {
        dates = new ArrayList<>();
    }

    public List<Date> getDates() {
        return dates;
    }

    public MessageLog putCurrentDate() {
        dates.add(new Date());
        return this;
    }

}
