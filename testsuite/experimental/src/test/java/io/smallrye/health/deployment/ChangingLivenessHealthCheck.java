package io.smallrye.health.deployment;

import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import jakarta.enterprise.context.ApplicationScoped;

@Liveness
@ApplicationScoped
public class ChangingLivenessHealthCheck implements HealthCheck {

    private final AtomicInteger invocations = new AtomicInteger(0);

    @Override
    public HealthCheckResponse call() {
        return invocations.getAndIncrement() == 0 ? HealthCheckResponse.up("up") : HealthCheckResponse.down("down");
    }
}
