package com.github.golimpio.atm.services;

import com.github.golimpio.atm.model.Cash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static com.github.golimpio.atm.services.CashBox.cashBox;
import static javax.ws.rs.core.Response.Status;

@Path("/init")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class Initialise {
    private static final Logger LOGGER = LoggerFactory.getLogger(Initialise.class);

    @POST
    @Path("/add")
    public Response add(List<Cash> money) {
        LOGGER.info("Adding money: [{}]", money);

        try {
            cashBox().add(money);
        }
        catch (AtmException e) {
            throw new WebApplicationException(
                    Response.status(e.isInternalError() ? Status.INTERNAL_SERVER_ERROR : Status.BAD_REQUEST)
                            .entity(e.getMessage()).build());
        }
        catch (Exception e) {
            throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()).build());
        }
        return Response.status(Status.OK).build();
    }

    @POST
    @Path("/clear")
    public Response clear() {
        LOGGER.info("Initialising ATM");

        try {
            cashBox().initialise();
        }
        catch (Exception e) {
            throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR)
                                                      .entity(e.getMessage()).build());
        }
        return Response.status(Status.OK).build();
    }
}
