package com.imp.msender.controller;

import java.util.List;
import java.util.Arrays;
import java.util.Collection;
import java.io.Serializable;
import com.imp.msender.entity.Server;
import com.imp.msender.bean.ServerBean;
import com.imp.msender.bean.MessageBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.context.annotation.Scope;
import org.springframework.beans.factory.annotation.Autowired;
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

    @RequestMapping(value = "/setServerList", method = RequestMethod.POST)
    public @ResponseBody
    String setServerList(@RequestParam String servers) {
        if (StringUtils.isNotBlank(servers) && !"refresh".equalsIgnoreCase(servers)) {
            srv.setList(Arrays.asList(servers.split("#;")));
        }
        return StringUtils.join(serversToUrls(srv.getActiveServers()), "#;");
    }

    @RequestMapping(value = "/addServerList", method = RequestMethod.POST)
    public @ResponseBody
    String addServerList(@RequestParam String servers) {
        if (StringUtils.isNotBlank(servers) && !"refresh".equalsIgnoreCase(servers)) {
            srv.addList(Arrays.asList(servers.split("#;")));
        }
        return StringUtils.join(serversToUrls(srv.getActiveServers()), "#;");
    }

    private Collection serversToUrls(List<Server> servers) {
        return CollectionUtils.collect(servers, new Transformer() {
            public Object transform(Object input) {
                return ((Server) input).getUrl();
            }
        });
    }
}
