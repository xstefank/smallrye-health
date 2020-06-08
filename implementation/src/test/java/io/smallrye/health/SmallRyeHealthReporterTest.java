package io.smallrye.health;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.util.logging.*;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponse.State;
import org.junit.Before;
import org.junit.Test;

import io.smallrye.health.registry.LivenessHealthRegistry;
import io.smallrye.health.registry.ReadinessHealthRegistry;

public class SmallRyeHealthReporterTest {

    private SmallRyeHealthReporter reporter;
    private LivenessHealthRegistry healthRegistry;

    public static class FailingHealthCheck implements HealthCheck {
        @Override
        public HealthCheckResponse call() {
            throw new RuntimeException("this health check has failed");
        }
    }

    public static class DownHealthCheck implements HealthCheck {
        @Override
        public HealthCheckResponse call() {
            return HealthCheckResponse.named("down").down().build();
        }
    }

    public static class UpHealthCheck implements HealthCheck {
        @Override
        public HealthCheckResponse call() {
            return HealthCheckResponse.named("up").up().build();
        }
    }

    @Before
    public void createReporter() {
        reporter = new SmallRyeHealthReporter();
        healthRegistry = new LivenessHealthRegistry();
        reporter.livenessHealthRegistry = healthRegistry;
        reporter.readinessHealthRegistry = new ReadinessHealthRegistry();
        reporter.emptyChecksOutcome = "UP";
        reporter.uncheckedExceptionDataStyle = "rootCause";
    }

    @Test
    public void testDefaultGetHealth() {
        SmallRyeHealth health = reporter.getHealth();

        assertThat(health.isDown(), is(false));
        assertThat(health.getPayload().getString("status"), is("UP"));
        assertThat(health.getPayload().getJsonArray("checks"), is(empty()));
    }

    @Test
    public void testGetHealthWithEmptyChecksOutcomeDown() {
        reporter.setEmptyChecksOutcome(State.DOWN.toString());

        SmallRyeHealth health = reporter.getHealth();

        assertThat(health.isDown(), is(true));
        assertThat(health.getPayload().getString("status"), is("DOWN"));
        assertThat(health.getPayload().getJsonArray("checks"), is(empty()));
    }

    @Test
    public void testGetHealthWithFailingCheckAndStyleDefault() {
        healthRegistry.register(new FailingHealthCheck());

        SmallRyeHealth health = reporter.getHealth();

        assertThat(health.isDown(), is(true));
        assertThat(health.getPayload().getString("status"), is("DOWN"));
        assertThat(health.getPayload().getJsonArray("checks").getJsonObject(0).getString("name"),
                is(FailingHealthCheck.class.getName()));
        assertThat(health.getPayload().getJsonArray("checks").getJsonObject(0).getString("status"), is("DOWN"));
        assertThat(health.getPayload().getJsonArray("checks").getJsonObject(0).getJsonObject("data").getString("rootCause"),
                is("this health check has failed"));
    }

    @Test
    public void testGetHealthWithFailingCheckAndStyleNone() {
        healthRegistry.register(new FailingHealthCheck());
        reporter.setUncheckedExceptionDataStyle("NONE");

        SmallRyeHealth health = reporter.getHealth();

        assertThat(health.isDown(), is(true));
        assertThat(health.getPayload().getString("status"), is("DOWN"));
        assertThat(health.getPayload().getJsonArray("checks").getJsonObject(0).getString("name"),
                is(FailingHealthCheck.class.getName()));
        assertThat(health.getPayload().getJsonArray("checks").getJsonObject(0).getString("status"), is("DOWN"));
        assertThat(health.getPayload().getJsonArray("checks").getJsonObject(0).getJsonObject("data"), is(nullValue()));
    }

    @Test
    public void testGetHealthWithFailingCheckAndStyleStackTrace() {
        healthRegistry.register(new FailingHealthCheck());
        reporter.setUncheckedExceptionDataStyle("stackTrace");

        SmallRyeHealth health = reporter.getHealth();

        assertThat(health.isDown(), is(true));
        assertThat(health.getPayload().getString("status"), is("DOWN"));
        assertThat(health.getPayload().getJsonArray("checks").getJsonObject(0).getString("name"),
                is(FailingHealthCheck.class.getName()));
        assertThat(health.getPayload().getJsonArray("checks").getJsonObject(0).getString("status"), is("DOWN"));
        assertThat(health.getPayload().getJsonArray("checks").getJsonObject(0).getJsonObject("data").getString("stackTrace"),
                is(notNullValue()));
    }

    @Test
    public void testGetHealthWithMixedChecksAndStyleDefault() {
        healthRegistry.register(new UpHealthCheck());
        healthRegistry.register(new FailingHealthCheck());
        healthRegistry.register(new DownHealthCheck());

        SmallRyeHealth health = reporter.getHealth();

        assertThat(health.isDown(), is(true));
        assertThat(health.getPayload().getString("status"), is("DOWN"));
        assertThat(health.getPayload().getJsonArray("checks").getJsonObject(0).getString("name"), is("down"));
        assertThat(health.getPayload().getJsonArray("checks").getJsonObject(0).getString("status"), is("DOWN"));
        assertThat(health.getPayload().getJsonArray("checks").getJsonObject(1).getString("name"),
                is(FailingHealthCheck.class.getName()));
        assertThat(health.getPayload().getJsonArray("checks").getJsonObject(1).getString("status"), is("DOWN"));
        assertThat(health.getPayload().getJsonArray("checks").getJsonObject(1).getJsonObject("data").getString("rootCause"),
                is("this health check has failed"));
        assertThat(health.getPayload().getJsonArray("checks").getJsonObject(2).getString("name"), is("up"));
        assertThat(health.getPayload().getJsonArray("checks").getJsonObject(2).getString("status"), is("UP"));
    }

    @Test
    public void testGetHealthWithMixedChecksAndStyleNone() {
        healthRegistry.register(new UpHealthCheck());
        healthRegistry.register(new FailingHealthCheck());
        healthRegistry.register(new DownHealthCheck());
        reporter.setUncheckedExceptionDataStyle("NONE");

        SmallRyeHealth health = reporter.getHealth();

        assertThat(health.isDown(), is(true));
        assertThat(health.getPayload().getString("status"), is("DOWN"));
        assertThat(health.getPayload().getJsonArray("checks").getJsonObject(0).getString("name"), is("down"));
        assertThat(health.getPayload().getJsonArray("checks").getJsonObject(0).getString("status"), is("DOWN"));
        assertThat(health.getPayload().getJsonArray("checks").getJsonObject(1).getString("name"),
                is(FailingHealthCheck.class.getName()));
        assertThat(health.getPayload().getJsonArray("checks").getJsonObject(1).getString("status"), is("DOWN"));
        assertThat(health.getPayload().getJsonArray("checks").getJsonObject(1).getJsonObject("data"), is(nullValue()));
        assertThat(health.getPayload().getJsonArray("checks").getJsonObject(2).getString("name"), is("up"));
        assertThat(health.getPayload().getJsonArray("checks").getJsonObject(2).getString("status"), is("UP"));
    }

    @Test
    public void testGetHealthWithMixedChecksAndStyleStackTrace() {
        healthRegistry.register(new UpHealthCheck());
        healthRegistry.register(new FailingHealthCheck());
        healthRegistry.register(new DownHealthCheck());
        reporter.setUncheckedExceptionDataStyle("stackTrace");

        SmallRyeHealth health = reporter.getHealth();

        assertThat(health.isDown(), is(true));
        assertThat(health.getPayload().getString("status"), is("DOWN"));
        assertThat(health.getPayload().getJsonArray("checks").getJsonObject(0).getString("name"), is("down"));
        assertThat(health.getPayload().getJsonArray("checks").getJsonObject(0).getString("status"), is("DOWN"));
        assertThat(health.getPayload().getJsonArray("checks").getJsonObject(1).getString("name"),
                is(FailingHealthCheck.class.getName()));
        assertThat(health.getPayload().getJsonArray("checks").getJsonObject(1).getString("status"), is("DOWN"));
        assertThat(health.getPayload().getJsonArray("checks").getJsonObject(1).getJsonObject("data").getString("stackTrace"),
                is(notNullValue()));
        assertThat(health.getPayload().getJsonArray("checks").getJsonObject(2).getString("name"), is("up"));
        assertThat(health.getPayload().getJsonArray("checks").getJsonObject(2).getString("status"), is("UP"));
    }

    @Test
    public void testReportWhenDown() {
        ByteArrayOutputStream logStream = new ByteArrayOutputStream();
        Handler handler = new StreamHandler(logStream, new SimpleFormatter());

        Logger logger = Logger.getLogger("io.smallrye.health");
        logger.addHandler(handler);
        logger.setLevel(Level.ALL);
        logger.setUseParentHandlers(false);

        healthRegistry.register(new DownHealthCheck());
        reporter.reportHealth(new ByteArrayOutputStream(), reporter.getHealth());

        handler.flush();
        assertThat(logStream.toString(), containsString(reporter.getHealth().getPayload().toString()));
    }
}
