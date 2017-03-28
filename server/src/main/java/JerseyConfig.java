package testpackage;

import testpackage.TestPath;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;

public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        // Enable Spring DI
        register(RequestContextFilter.class);
        register(TestPath.class);
    }
}