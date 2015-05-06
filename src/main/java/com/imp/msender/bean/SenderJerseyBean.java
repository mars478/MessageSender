package com.imp.msender.bean;

import java.util.List;
import java.io.Serializable;
import javax.ws.rs.core.MediaType;
import com.imp.msender.entity.Message;
import com.imp.msender.entity.Server;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.Primary;

@Primary
@Component
@Scope("session")
public class SenderJerseyBean extends SenderAbstractBean implements Serializable {

    Client client = null;
    WebResource service = null;

    public SenderJerseyBean() {
        System.out.println("SenderJerseyBean created");
        client = Client.create(new DefaultClientConfig());
    }

    @Override
    public void resetRestRole(Server srv) {
        client.removeAllFilters();
        client.addFilter(new HTTPBasicAuthFilter(srv.getUsername(), srv.getPassword()));
    }

    @Override
    public List<Message> getAll(Server srv) {
        try {
            resetRestRole(srv);

            service = client.resource(srv.getBaseURI());
            String responseJson = service.path("message").accept(MediaType.APPLICATION_JSON).get(String.class);
            return new ObjectMapper().readValue(responseJson, new TypeReference<List<Message>>() {});
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Message add(Server srv, Message msg) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            service = client.resource(srv.getBaseURI());
            ClientResponse response = service.path("message")
                    .type(MediaType.APPLICATION_JSON)
                    .post(ClientResponse.class, mapper.writeValueAsString(msg));

            Message responseStr = mapper.readValue(response.getEntityInputStream(), Message.class);
            return (responseStr != null)
                    ? msg
                    : null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean delete(Server srv, Long id) {
        try {
            service = client.resource(srv.getBaseURI());
            ClientResponse response = service.path("message").path(id.toString()).delete(ClientResponse.class);
            return response.toString().contains("200 OK");
        } catch (Exception e) {
            return false;
        }
    }
}