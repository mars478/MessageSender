package com.imp.msender.controller;

import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;
import com.imp.msender.bean.MessageBean;
import com.imp.msender.entity.Message;
import org.springframework.context.annotation.Scope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RestController
@Scope("session")
public class IndexRest implements Serializable {

    @Autowired
    MessageBean mBean;

    @RequestMapping(value = "/message", method = RequestMethod.GET)
    public List<Message> readAll() {
        return mBean.getAll();
    }

    @RequestMapping(value = "/message/{id}", method = RequestMethod.GET)
    public Message read(@PathVariable Long id) {
        return mBean.getById(id);
    }

    @RequestMapping(value = "/message", method = RequestMethod.POST)
    public Message create(@RequestBody MessageCreate mCreate) {
        return mBean.add(mCreate.toMessage(), mCreate.getServers());
    }

    @RequestMapping(value = "/message/{id}", method = RequestMethod.DELETE)
    public Message delete(@PathVariable Long id) {
        return mBean.deleteById(id);
    }

    static class MessageCreate extends Message {

        List<String> servers;

        public MessageCreate() {
            servers = new ArrayList<>();
        }

        public List<String> getServers() {
            return servers;
        }

        public void setServers(List<String> servers) {
            this.servers = servers;
        }

        public Message toMessage() {
            return new Message(getRepeats(), getInterval(), getHeader(), getBody(), getAction(), getStartDate(), getExpireDate(), getLogins());
        }
    }
}
