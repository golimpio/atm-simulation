package com.github.golimpio.atm;

import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("resources")
public class RestApplication extends ResourceConfig {
    public RestApplication() {
        packages("com.github.golimpio.atm.services");
    }
}
