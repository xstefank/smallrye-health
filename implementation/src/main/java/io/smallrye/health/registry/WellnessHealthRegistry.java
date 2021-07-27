package io.smallrye.health.registry;

import io.smallrye.health.api.HealthRegistry;
import io.smallrye.health.api.Wellness;
import jakarta.enterprise.context.ApplicationScoped;

@Wellness
@ApplicationScoped
public class WellnessHealthRegistry extends AbstractHealthRegistry implements HealthRegistry {
}
