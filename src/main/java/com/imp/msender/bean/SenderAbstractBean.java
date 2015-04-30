package com.imp.msender.bean;

import java.net.URI;
import java.util.List;
import com.imp.msender.entity.Message;
import java.io.Serializable;
import org.apache.commons.lang3.StringUtils;

public abstract class SenderAbstractBean implements Serializable {

    String restUser = "tomcat";
    String restPwd = "tomcat";
    String domain;
    String app;

    protected URI getBaseURI() {
        return getBaseURI(domain + StringUtils.defaultString(app));
    }

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

    public abstract void resetRestRole();

    public abstract void setRestRole(String username, String password);

    public abstract List<Message> getAll();

    public abstract Message add(Message msg);

    protected int parseStrInt(String str, int exc) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return exc;
        }
    }

    public abstract boolean delete(Long id);

    // ======== get-n-set ============
    public String getRestUser() {
        return restUser;
    }

    public void setRestUser(String restUser) {
        this.restUser = restUser;
    }

    public String getRestPwd() {
        return restPwd;
    }

    public void setRestPwd(String restPwd) {
        this.restPwd = restPwd;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

}
