package io.smallrye.health.registry;

import org.eclipse.microprofile.health.Readiness;

import io.smallrye.health.api.HealthRegistry;
import jakarta.enterprise.context.ApplicationScoped;

@Readiness
@ApplicationScoped
public class ReadinessHealthRegistry extends AbstractHealthRegistry implements HealthRegistry {
}
