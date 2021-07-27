package io.smallrye.health.deployment;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.Liveness;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class NullHealthCheckProducer {

    @Produces
    @Dependent
    @Liveness
    public HealthCheck nullHealthCheck() {
        return null;
    }

}
