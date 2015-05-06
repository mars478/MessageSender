package com.imp.msender.bean;

import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;
import com.imp.msender.entity.Message;
import com.imp.msender.entity.Server;
import com.imp.msender.entity.interfaces.RetListener;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;
import org.springframework.beans.factory.annotation.Autowired;

@Component
@Scope("session")
public class MessageBean implements Serializable {

    @Autowired
    SenderJerseyBean sender;

    @Autowired
    ServerBean servers;

    List<Message> list = new ArrayList<>();

    public MessageBean() {
        super();
        System.out.println("message bean created");
    }

    // ========== messages service ================
    private int getIndex(final Long id) {
        synchronized (list) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId() == id) {
                    return i;
                }
            }
        }
        return -1;
    }

    private Message wrapCall(final long id, Message msg, RetListener callback) {
        int index = getIndex(id);
        if (index == -1) {
            return null;
        }
        synchronized (list) {
            if (callback != null) {
                callback.call(new CallbackParam(index, msg, list));
            }
            return list.get(index);
        }
    }

    static class CallbackParam {

        public final int index;
        public final Message message;
        public final List<Message> list;

        public CallbackParam(int index) {
            this.index = index;
            message = null;
            list = null;
        }

        public CallbackParam(int index, Message message, List<Message> list) {
            this.index = index;
            this.message = message;
            this.list = list;
        }
    }

    // ========== messages rest =================
    public Message getById(final Long id) {
        return wrapCall(id, null, null);
    }

    public List<Message> getAll() {
        return list;
    }

    public Message updateById(final Long id, final Message msg) {
        if (nullServer()) {
            return null;
        }
        deleteById(id);
        return add(msg, true);
    }

    public Message deleteById(final Long id) {
        if (nullServer()) {
            return null;
        }
        int index = getIndex(id);
        if (index > -1) {
            synchronized (list) {
                Message temp = list.get(index);
                if (sender.delete(curSrv(), id)) {
                    list.remove(index);
                    return temp;
                }
            }
        }
        return null;
    }

    public Message add(Server srv, Message msg, boolean checkList) {
        if (nullServer()) {
            return null;
        }
        if (!checkList || getIndex(msg.getId()) == -1) {
            synchronized (list) {
                if (checkList) {
                    for (Message m : list) {
                        if (m.getId() == msg.getId()) {
                            msg.setId(Message.getNewId());
                            break;
                        }
                    }
                }
                if (sender.add(srv, msg) != null) {
                    if (checkList) {
                        list.add(msg);
                    }
                    return msg;
                }
            }
        }
        return null;
    }

    public Message add(Message msg, boolean curServer) {
        String exc = null;
        try {
            int errorCnt = 1;
            List<Server> srvs = curServer ? Arrays.asList(curSrv()) : servers.getAll();
            for (Server srv : srvs) {
                if (add(srv, msg, false) == null) {
                    if (exc == null) {
                        exc = "Невозможно добавить сообщение:\n";
                    }
                    exc = errorCnt++ + ") " + srv + "\n";
                }
            }
            return msg;
        } catch (Exception e) {
            exc = StringUtils.abbreviate(ExceptionUtils.getMessage(e), 200);
        }
        return StringUtils.isBlank(exc) ? null : new Message(0, 0, exc, null, null, null, null, null);
    }

    // ======== client sync =====================
    public void refresh() {
        if (nullServer()) {
            return;
        }
        synchronized (list) {
            list.clear();
            List temp = sender.getAll(curSrv());
            if (temp != null && !temp.isEmpty()) {
                list.addAll(temp);
            }
        }
    }

    protected boolean nullServer() {
        return curSrv() == null;
    }

    protected Server curSrv() {
        return servers.getFirst();
    }

    public void setUrl(String server) {
        if (!Server.verify(server)) {
            return;
        }

        if (!nullServer()) {
            servers.moveToTop(server);
        } else {
            servers.add(new Server(server));
        }
    }

    public String getUrl() {
        return nullServer() ? "" : curSrv().getUrl();
    }

}
