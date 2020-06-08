package io.smallrye.health.registry;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.health.HealthRegistry;
import org.eclipse.microprofile.health.Readiness;

@ApplicationScoped
@Readiness
public class ReadinessHealthRegistry extends AbstractHealthRegistry implements HealthRegistry {
}
