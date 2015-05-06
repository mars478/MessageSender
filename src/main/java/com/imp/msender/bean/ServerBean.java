package com.imp.msender.bean;

import java.util.List;
import java.util.ArrayList;
import com.imp.msender.entity.Server;
import java.io.Serializable;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;

@Component
@Scope("session")
public class ServerBean implements Serializable{

    List<Server> list;

    public ServerBean() {
        list = new ArrayList<>();
    }

    public List<Server> getAll() {
        return list;
    }

    public List<Server> getActiveServers() {
        List<Server> ret = new ArrayList<>();
        synchronized (list) {
            for (Server s : list) {
                if (s.isAdded()) {
                    ret.add(s);
                }
            }
        }
        return ret;
    }

    public Server getFirst() {
        return list.isEmpty() ? null : list.get(0);
    }

    public Server getById(Long id) {
        int i = getIndex(id);
        return i > -1 ? list.get(i) : null;
    }

    public Server patch(Server srv) {
        deleteById(srv.getId());
        return add(srv);
    }

    public Server deleteById(final Long id) {
        int index = getIndex(id);
        if (index > -1) {
            synchronized (list) {
                Server temp = list.get(index);
                list.remove(index);
                return temp;
            }
        }
        return null;
    }

    public Server add(Server srv) {
        if (getIndex(srv.getId()) == -1) {
            synchronized (list) {
                for (Server m : list) {
                    if (m.getId() == srv.getId()) {
                        srv.setId(Server.getNewId());
                        break;
                    }
                }
                if (srv.correct()) {
                    list.add(0, srv);
                }
                return srv;
            }
        }
        return null;
    }

    public void setList(List<String> stringServers) {
        if (stringServers == null || stringServers.isEmpty()) {
            return;
        }

        Server srv = null;
        List<Server> nList = new ArrayList<>();
        for (String s : stringServers) {
            srv = new Server(s);
            if (srv.correct()) {
                nList.add(srv);
            }
        }

        synchronized (list) {
            list = nList;
        }
    }

    public void moveToTop(String url) {
        synchronized (list) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getUrl().equalsIgnoreCase(url)) {
                    Server top = list.get(0);
                    list.set(0, list.get(i));
                    list.set(i, top);
                    break;
                }
            }
        }
    }

    // ========= service ================
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

}
