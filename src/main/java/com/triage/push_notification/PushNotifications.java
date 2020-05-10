package com.triage.push_notification;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.triage.rest.models.messages.Response;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;


public class PushNotifications {
    public boolean sendResponseObject(Response response){
        Client client = Client.create();
        String clientPushPath="";
        if(System.getProperty("os.name").equals("Windows 8.1"))
            clientPushPath="https://triagebot2.herokuapp.com/push.php";
        else
            clientPushPath = "http://193.204.187.192/classcode_2_bot/push.php";
        WebResource webResource = client.resource(clientPushPath);
        Gson g = new Gson();
        String json = g.toJson(response);
        MultivaluedMap<String,String> formData = new MultivaluedMapImpl();
        formData.add("response", json);

        ClientResponse clientResponse = webResource
                .type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                .accept("application/json")
                .post(ClientResponse.class, formData);
        int status = clientResponse.getStatus();

        if(status == 200)
            return true;
        else
            return false;
    }

}
