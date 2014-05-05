package com.github.golimpio.atm.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.github.golimpio.atm.services.CashBox.cashBox;
import static javax.ws.rs.core.Response.Status;

@Path("/withdraw")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class Withdraw {
    private static final Logger LOGGER = LoggerFactory.getLogger(Withdraw.class);

    @POST
    @Path("/")
    public Response withdraw(long value) {
        LOGGER.info("New withdraw: [{}]", value);

        try {
            cashBox().withdraw(value);
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
    @Path("/minimum")
    public long minimum() {
        LOGGER.info("Retrieving minimum allowed withdraw...");
        try {
            return cashBox().getMinimalWithdrawValue();
        }
        catch (Exception e) {
            throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR)
                                                      .entity(e.getMessage()).build());
        }
    }

    @POST
    @Path("/maximum")
    public long maximum() {
        LOGGER.info("Retrieving maximum allowed withdraw...");
        try {
            return cashBox().sumInCash();
        }
        catch (Exception e) {
            throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()).build());
        }
    }
}

