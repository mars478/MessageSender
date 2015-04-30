package com.imp.msender.controller;

import java.io.Serializable;
import com.imp.msender.bean.MessageBean;
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
    MessageBean mBean;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        return "pages/index.html";
    }

    @RequestMapping(value = "/server", method = RequestMethod.POST)
    public @ResponseBody String set(@RequestParam String server) {
        if ("refresh".equalsIgnoreCase(server)) {
            mBean.refresh();
        } else {
            mBean.setUrl(server);
        }
        return mBean.getUrl();
    }

}
