package com.imp.msender.controller;

import java.util.List;
import java.io.Serializable;
import com.imp.msender.entity.Server;
import com.imp.msender.bean.ServerBean;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@Scope("session")
public class ServersRest implements Serializable {

    @Autowired
    ServerBean sBean;

    @RequestMapping(value = "/server", method = RequestMethod.GET)
    public List<Server> readAll() {
        return sBean.getAll();
    }

    @RequestMapping(value = "/server/{id}", method = RequestMethod.GET)
    public Server read(@PathVariable Long id) {
        return sBean.getById(id);
    }

    @RequestMapping(value = "/server", method = RequestMethod.POST)
    public Server create(@RequestBody Server srv) {
        return sBean.add(srv);
    }

    @RequestMapping(value = "/server/{id}", method = RequestMethod.PATCH)
    public Server update(@PathVariable(value = "id") Long id, @RequestBody Server srv) {
        return sBean.patch(srv);
    }

    @RequestMapping(value = "/server/{id}", method = RequestMethod.DELETE)
    public Server delete(@PathVariable Long id) {
        return sBean.deleteById(id);
    }
}
