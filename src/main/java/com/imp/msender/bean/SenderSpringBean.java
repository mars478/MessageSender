package com.imp.msender.bean;

import com.imp.msender.entity.Message;
import com.imp.msender.entity.Server;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Scope("session")
public class SenderSpringBean extends SenderAbstractBean implements Serializable {

    RestTemplate rest;
    HttpEntity<String> request;

    public SenderSpringBean() {
        System.out.println("SenderSpringBean init");
        rest = new RestTemplate();
        request = null;
    }

    @Override
    public void resetRestRole(Server srv) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (StringUtils.isNotBlank(srv.getUsername()) && StringUtils.isNotBlank(srv.getPassword())) {
            String plainCreds = srv.getUsername() + ":" + srv.getPassword();
            byte[] plainCredsBytes = plainCreds.getBytes();
            byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
            String base64Creds = new String(base64CredsBytes);
            headers.add("Authorization", "Basic " + base64Creds);
        }
        request = new HttpEntity<>(headers);
    }

    @Override
    public List<Message> getAll(Server srv) {
        ResponseEntity<Message[]> response = rest.exchange(srv.getBaseURI(), HttpMethod.GET, request, Message[].class);
        Message[] forNow = response.getBody();
        return new ArrayList<>(Arrays.asList(forNow));
    }

    @Override
    public Message add(Server srv, Message msg) {
        ResponseEntity<Message> response = rest.exchange(srv.getBaseURI(), HttpMethod.POST, request, Message.class);
        return response.getBody();
    }

    @Override
    public boolean delete(Server srv, Long id) {
        ResponseEntity<Message> response = rest.exchange(srv.getBaseURI() + "/" + id, HttpMethod.DELETE, request, Message.class);
        return response.getBody() != null;
    }

}
