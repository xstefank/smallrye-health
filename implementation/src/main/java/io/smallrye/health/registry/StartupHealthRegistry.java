package io.smallrye.health.registry;

import org.eclipse.microprofile.health.Startup;

import io.smallrye.health.api.HealthRegistry;
import jakarta.enterprise.context.ApplicationScoped;

@Startup
@ApplicationScoped
public class StartupHealthRegistry extends AbstractHealthRegistry implements HealthRegistry {
}
