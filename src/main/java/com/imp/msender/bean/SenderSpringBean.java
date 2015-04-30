package com.imp.msender.bean;

import com.imp.msender.entity.Message;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Scope("session")
public class SenderSpringBean extends SenderAbstractBean  implements Serializable{

    RestTemplate rest;
    HttpEntity<String> request;

    public SenderSpringBean() {
        System.out.println("SenderSpringBean init");
        rest = new RestTemplate();
        request = null;
    }

    @Override
    protected URI getBaseURI() {
        try {
            String a = StringUtils.defaultString(app);
            if (StringUtils.isNotBlank(a) && !a.endsWith("/")) {
                a = a + "/";
            }
            return new URI(domain + a + "rest/message/");
        } catch (URISyntaxException ex) {
            return null;
        }
    }

    @Override
    public void resetRestRole() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (StringUtils.isNotBlank(restUser) && StringUtils.isNotBlank(restPwd)) {
            String plainCreds = restUser + ":" + restPwd;
            byte[] plainCredsBytes = plainCreds.getBytes();
            byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
            String base64Creds = new String(base64CredsBytes);
            headers.add("Authorization", "Basic " + base64Creds);
        }
        request = new HttpEntity<>(headers);
    }

    @Override
    public void setRestRole(String username, String password) {
        this.restUser = username;
        this.restPwd = password;
    }

    @Override
    public List<Message> getAll() {
        ResponseEntity<Message[]> response = rest.exchange(getBaseURI(), HttpMethod.GET, request, Message[].class);
        Message[] forNow = response.getBody();
        return new ArrayList<>(Arrays.asList(forNow));
    }

    @Override
    public Message add(Message msg) {
        ResponseEntity<Message> response = rest.exchange(getBaseURI(), HttpMethod.POST, request, Message.class);
        return response.getBody();
    }

    @Override
    public boolean delete(Long id) {
        ResponseEntity<Message> response = rest.exchange(getBaseURI() + "/" + id, HttpMethod.DELETE, request, Message.class);
        return response.getBody() != null;
    }

}
