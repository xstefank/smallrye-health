package io.smallrye.health.registry;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthRegistry;

public class AbstractHealthRegistry implements HealthRegistry {

    Map<String, HealthCheck> checks = new HashMap<>();

    @Override
    public boolean register(String id, HealthCheck healthCheck) {
        try {
            checks.put(id, healthCheck);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean remove(String id) {
        try {
            checks.remove(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Collection<HealthCheck> getChecks() {
        return Collections.unmodifiableCollection(checks.values());
    }
}
