package com.imp.msender.bean;

import java.util.List;
import com.imp.msender.entity.Message;
import com.imp.msender.entity.Server;
import java.io.Serializable;

public abstract class SenderAbstractBean implements Serializable {

    public abstract void resetRestRole(Server srv);

    public abstract List<Message> getAll(Server srv);

    public abstract Message add(Server srv, Message msg);

    public abstract boolean delete(Server srv, Long id);

    protected int parseStrInt(String str, int exc) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return exc;
        }
    }

}
