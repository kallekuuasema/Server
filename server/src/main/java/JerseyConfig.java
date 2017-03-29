package kalle.server;

import kalle.server.Players;
import kalle.server.Scores;
import org.glassfish.jersey.server.ResourceConfig;

public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        register(Players.class);
        register(Scores.class);
    }
}