package org.eclipsecon.testcontainersdemo;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.utility.Base58;

import java.net.URI;
import java.time.Duration;

public class MinioContainer extends GenericContainer<MinioContainer> {
    private static final int DEFAULT_PORT = 9000;
    private static final String DEFAULT_IMAGE = "minio/minio";
    private static final String DEFAULT_TAG = "RELEASE.2022-10-21T22-37-48Z";
    private static final String DEFAULT_USER = "minio";
    private static final String DEFAULT_PASSWORD = "minio123";
    private static final String DEFAULT_STORAGE_DIRECTORY = "/data";
    private static final String HEALTH_ENDPOINT = "/minio/health/ready";

    private static final String MINIO_ROOT_USER = "MINIO_ROOT_USER";
    private static final String MINIO_ROOT_PASSWORD = "MINIO_ROOT_PASSWORD";

    private String rootUser;

    private String rootPassword;

    public MinioContainer() {
        this(DEFAULT_IMAGE + ":" + DEFAULT_TAG, DEFAULT_USER, DEFAULT_PASSWORD);
    }

    public MinioContainer(String image, String rootUser, String rootPassword) {
        super(image == null ? DEFAULT_IMAGE + ":" + DEFAULT_TAG : image);
        withNetworkAliases("minio-" + Base58.randomString(6));
        addExposedPort(DEFAULT_PORT);
        withEnv(MINIO_ROOT_USER, this.rootUser = rootUser);
        withEnv(MINIO_ROOT_PASSWORD, this.rootPassword = rootPassword);
        withCommand("server", DEFAULT_STORAGE_DIRECTORY);
        setWaitStrategy(new HttpWaitStrategy()
                .forPort(DEFAULT_PORT)
                .forPath(HEALTH_ENDPOINT)
                .withStartupTimeout(Duration.ofSeconds(15)));
    }

    public String getRootUser() {
        return rootUser;
    }

    public String getRootPassword() {
        return rootPassword;
    }

    public URI getEndpointUri() {
        return URI.create(String.format("http://%s:%s", this.getHost(), this.getMappedPort(9000)));
    }
}