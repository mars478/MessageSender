package com.imp.msender.controller;

import java.io.Serializable;
import com.imp.msender.bean.MessageBean;
import com.imp.msender.bean.ServerBean;
import com.imp.msender.entity.Server;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.context.annotation.Scope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Scope("session")
public class IndexController implements Serializable {

    @Autowired
    MessageBean msg;

    @Autowired
    ServerBean srv;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        return "pages/index.html";
    }

    @RequestMapping(value = "/link", method = RequestMethod.POST)
    public @ResponseBody
    String set(@RequestParam String server) {
        if ("refresh".equalsIgnoreCase(server)) {
            msg.refresh();
        } else {
            msg.setUrl(server);
        }
        return msg.getUrl();
    }

    @RequestMapping(value = "/serverList", method = RequestMethod.POST)
    public @ResponseBody
    List<Server> setServerList(@RequestParam String servers) {
        if (StringUtils.isNotBlank(servers) && !"refresh".equalsIgnoreCase(servers)) {
            srv.setList(Arrays.asList(servers.split("#;")));
        }
        return srv.getActiveServers();
    }

}
