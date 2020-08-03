package com.structurizr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.structurizr.util.Url;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a deployment instance of a {@link SoftwareSystem} or {@link Container}, which can be added to a {@link DeploymentNode}.
 */
public abstract class StaticStructureElementInstance extends DeploymentElement {

    private static final int DEFAULT_HEALTH_CHECK_INTERVAL_IN_SECONDS = 60;
    private static final long DEFAULT_HEALTH_CHECK_TIMEOUT_IN_MILLISECONDS = 0;

    private int instanceId;
    private Set<HttpHealthCheck> healthChecks = new HashSet<>();

    StaticStructureElementInstance() {
    }

    StaticStructureElementInstance(int instanceId, String environment) {
        setInstanceId(instanceId);
        setEnvironment(environment);
    }

    @JsonIgnore
    public abstract StaticStructureElement getElement();

    /**
     * Gets the instance ID of this container.
     *
     * @return  the instance ID, an integer greater than zero
     */
    public int getInstanceId() {
        return instanceId;
    }

    void setInstanceId(int instanceId) {
        this.instanceId = instanceId;
    }

    @Override
    @JsonIgnore
    protected Set<String> getRequiredTags() {
        return Collections.emptySet();
    }

    @Override
    public boolean removeTag(String tag) {
        // do nothing ... tags cannot be removed from element instances (they should reflect the element they are based upon)
        return false;
    }

    @Override
    @JsonIgnore
    public String getCanonicalName() {
        return getElement().getCanonicalName() + "[" + instanceId + "]";
    }

    @Override
    @JsonIgnore
    public Element getParent() {
        return getElement().getParent();
    }

    @Override
    @JsonIgnore
    public String getName() {
        return getElement().getName();
    }

    @Override
    public void setName(String name) {
        // no-op ... the name of an element instance is taken from the associated element
    }

    /**
     * Gets the set of health checks associated with this element instance.
     *
     * @return  a Set of HttpHealthCheck instances
     */
    @Nonnull
    public Set<HttpHealthCheck> getHealthChecks() {
        return new HashSet<>(healthChecks);
    }

    void setHealthChecks(Set<HttpHealthCheck> healthChecks) {
        this.healthChecks = healthChecks;
    }

    /**
     * Adds a new health check, with the default interval (60 seconds) and timeout (0 milliseconds).
     *
     * @param name      the name of the health check
     * @param url       the URL of the health check
     * @return  a HttpHealthCheck instance representing the health check that has been added
     * @throws IllegalArgumentException     if the name is empty, or the URL is not a well-formed URL
     */
    @Nonnull
    public HttpHealthCheck addHealthCheck(String name, String url) {
        return addHealthCheck(name, url, DEFAULT_HEALTH_CHECK_INTERVAL_IN_SECONDS, DEFAULT_HEALTH_CHECK_TIMEOUT_IN_MILLISECONDS);
    }

    /**
     * Adds a new health check.
     *
     * @param name      the name of the health check
     * @param url       the URL of the health check
     * @param interval  the polling interval, in seconds
     * @param timeout   the timeout, in milliseconds
     * @return  a HttpHealthCheck instance representing the health check that has been added
     * @throws IllegalArgumentException     if the name is empty, the URL is not a well-formed URL, or the interval/timeout is not zero/a positive integer
     */
    @Nonnull
    public HttpHealthCheck addHealthCheck(String name, String url, int interval, long timeout) {
        if (name == null || name.trim().length() == 0) {
            throw new IllegalArgumentException("The name must not be null or empty.");
        }

        if (url == null || url.trim().length() == 0) {
            throw new IllegalArgumentException("The URL must not be null or empty.");
        }

        if (!Url.isUrl(url)) {
            throw new IllegalArgumentException(url + " is not a valid URL.");
        }

        if (interval < 0) {
            throw new IllegalArgumentException("The polling interval must be zero or a positive integer.");
        }

        if (timeout < 0) {
            throw new IllegalArgumentException("The timeout must be zero or a positive integer.");
        }

        HttpHealthCheck healthCheck = new HttpHealthCheck(name, url, interval, timeout);
        healthChecks.add(healthCheck);

        return healthCheck;
    }

}