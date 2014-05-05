package com.github.golimpio.atm.services;

import com.github.golimpio.atm.model.Amount;
import com.github.golimpio.atm.model.Cash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static com.github.golimpio.atm.services.CashBox.cashBox;
import static javax.ws.rs.core.Response.Status;

@Path("/withdraw")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class Withdraw {
    private static final Logger LOGGER = LoggerFactory.getLogger(Withdraw.class);

    @GET
    @Path("/")
    @Consumes(MediaType.TEXT_PLAIN)
    public List<Cash> withdraw(long value) {
        LOGGER.info("New withdraw: [{}]",
                value);

        try {
            return cashBox().withdraw(value);
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
    }

    @GET
    @Path("/minimum")
    public Amount minimum() {
        LOGGER.info("Retrieving minimum allowed withdraw...");
        try {
            return new Amount(cashBox().getMinimalWithdrawValue());
        }
        catch (Exception e) {
            throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR)
                                                      .entity(e.getMessage()).build());
        }
    }

    @GET
    @Path("/maximum")
    public Amount maximum() {
        LOGGER.info("Retrieving maximum allowed withdraw...");
        try {
            return new Amount(cashBox().sumInCash());
        }
        catch (Exception e) {
            throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()).build());
        }
    }
}

