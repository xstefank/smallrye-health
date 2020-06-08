package io.smallrye.health.registry;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;

import org.eclipse.microprofile.health.HealthRegistry;
import org.eclipse.microprofile.health.Liveness;

@Liveness
@Default
@ApplicationScoped
public class LivenessHealthRegistry extends AbstractHealthRegistry implements HealthRegistry {
}
