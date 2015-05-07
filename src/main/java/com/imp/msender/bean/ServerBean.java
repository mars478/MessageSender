package com.imp.msender.bean;

import java.util.List;
import java.util.ArrayList;
import com.imp.msender.entity.Server;
import java.io.Serializable;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;

@Component
@Scope("session")
public class ServerBean implements Serializable {

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
                boolean eq = false;
                for (Server m : list) {
                    if (m.getId() == srv.getId()) {
                        srv.setId(Server.getNewId());
                    }
                    if (m.eq(srv)) {
                        eq = true;
                    }
                }
                if (srv.correct() && !eq) {
                    list.add(0, srv);
                }
                return srv;
            }
        }
        return null;
    }

    public void setList(List<String> stringServers) {
        List nList = convertList(stringServers);
        synchronized (list) {
            if (nList != null) {
                list = nList;
            } else {
                list.clear();
            }
        }
    }

    public void addList(List<String> stringServers) {
        List<Server> aList = convertList(stringServers);
        synchronized (list) {
            for (Server a : aList) {
                if (!contains(a)) {
                    add(a);
                }
            }
        }
    }

    protected List<Server> convertList(List<String> stringServers) {
        if (stringServers == null || stringServers.isEmpty()) {
            return null;
        }

        Server srv = null;
        List<Server> nList = new ArrayList<>();
        for (String s : stringServers) {
            srv = new Server(s);
            if (srv.correct()) {
                nList.add(srv);
            }
        }
        return nList;
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

    /**
     * not syncronized
     *
     * @param srv
     * @return
     */
    protected boolean contains(Server srv) {
        for (Server s : list) {
            if (s.eq(srv)) {
                return true;
            }
        }
        return false;
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
