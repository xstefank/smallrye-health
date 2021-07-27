package io.smallrye.health.registry;

import org.eclipse.microprofile.health.Liveness;

import io.smallrye.health.api.HealthRegistry;
import jakarta.enterprise.context.ApplicationScoped;

@Liveness
@ApplicationScoped
public class LivenessHealthRegistry extends AbstractHealthRegistry implements HealthRegistry {
}
