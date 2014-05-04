package com.github.golimpio.atm.services;

import com.github.golimpio.atm.model.Cash;
import com.github.golimpio.atm.model.Response;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/init")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class Initialise {
    @POST
    @Path("/add")
    public Response add(List<Cash> recipes) {
        return null;
    }
}
