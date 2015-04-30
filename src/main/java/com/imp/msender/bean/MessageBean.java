package com.imp.msender.bean;

import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;
import java.net.MalformedURLException;
import com.imp.msender.entity.Message;
import com.imp.msender.entity.interfaces.RetListener;
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

    List<Message> list = new ArrayList<>();

    public MessageBean() {
        super();
    }

    // ============= url string ===========
    public String getUrl() {
        String pwd = StringUtils.defaultString(sender.getRestPwd());
        String usr = StringUtils.defaultString(sender.getRestUser());
        String domain = StringUtils.defaultString(sender.getDomain());
        String app = StringUtils.defaultString(sender.getApp());

        String ret = domain + app;
        if (StringUtils.isNotBlank(usr) && StringUtils.isNotBlank(pwd)) {
            ret = ret.replace("://", "://" + pwd + ":" + usr + "@");
        }
        return ret;
    }

    public String setUrl(String url) {
        try {
            URL aURL = new URL(url);
            int port = aURL.getPort();
            String p = (port == -1) ? "" : (":" + port);
            sender.setDomain(aURL.getProtocol() + "://" + aURL.getHost() + p);
            sender.setApp(aURL.getPath());
            String uInfo = aURL.getUserInfo();
            if (StringUtils.isNotBlank(uInfo)) {
                String[] t = uInfo.split(":");
                sender.setRestUser(t[0]);
                sender.setRestPwd(t[1]);
            }
            sender.resetRestRole();
            refresh();
            return getUrl();
        } catch (MalformedURLException ex) {
            return null;
        }
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
        deleteById(id);
        return add(msg);
    }

    public Message deleteById(final Long id) {
        int index = getIndex(id);
        if (index > -1) {
            synchronized (list) {
                Message temp = list.get(index);
                if (sender.delete(id)) {
                    list.remove(index);
                    return temp;
                }
            }
        }
        return null;
    }

    public Message add(Message msg) {
        return add(msg, true);
    }

    public Message add(Message msg, boolean checkList) {
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
                if (sender.add(msg) != null) {
                    if (checkList) {
                        list.add(msg);
                    }
                    return msg;
                }
            }
        }
        return null;
    }

    public Message add(Message msg, List<String> servers) {
        String exc = null;
        if (servers != null && !servers.isEmpty()) {
            try {
                int errorCnt = 1;
                for (String srv : servers) {
                    if (setUrl(srv) == null) {
                        exc = "Invalid url:" + srv;
                        break;
                    }
                    if (add(msg, false) == null) {
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
        }
        return StringUtils.isBlank(exc) ? null : new Message(0, 0, exc, null, null, null, null, null);
    }

    // ======== client sync =====================
    public void refresh() {
        synchronized (list) {
            list.clear();
            List temp = sender.getAll();
            if (temp != null && !temp.isEmpty()) {
                list.addAll(temp);
            }
        }
    }

}
