package kalle.server;

import kalle.server.TestPath;
import org.glassfish.jersey.server.ResourceConfig;

public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        register(TestPath.class);
    }
}