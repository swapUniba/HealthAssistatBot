package com.triage.rest;

import javax.ws.rs.*;
import java.io.*;

@Path("/chart")
public class ChartResponseService {
    @GET
    @Path("chart")
    @Produces("image/png")
    public javax.ws.rs.core.Response getChart(@QueryParam("chatid") String chatId) throws IOException {
        String filePath="";
        if(System.getProperty("os.name").equals("Windows 8.1"))
           filePath= "C:/Users/frank/TriageBotRestServer-data/OCRCharts/chart_"+chatId+".png";
        else
            filePath= "/home/baccaro/TriageBotRestServer-data/OCRCharts/chart_"+chatId+".png";
        File file = new File(filePath);
        javax.ws.rs.core.Response.ResponseBuilder response = javax.ws.rs.core.Response.ok((Object) file);
        response.header("Content-Disposition",
                "attachment; filename=name.png");
        return response.build();
    }
}
