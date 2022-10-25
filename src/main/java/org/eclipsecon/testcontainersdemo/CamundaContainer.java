package org.eclipsecon.testcontainersdemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class CamundaContainer extends GenericContainer<CamundaContainer> {
    private static final Logger LOG = LoggerFactory.getLogger(CamundaContainer.class);

    public CamundaContainer() {
        this(DockerImageName.parse("camunda/camunda-bpm-platform:7.18.0"));
    }

    public CamundaContainer(DockerImageName image) {
        super(image);
        withExposedPorts(8080);
        waitingFor(Wait.forHttp("/camunda"));
        withStartupTimeout(Duration.of(6, ChronoUnit.MINUTES));
        withCreateContainerCmdModifier(cmd -> cmd.getHostConfig().withMemory(2147483648L /*2GB*/));
        withLogConsumer(new Slf4jLogConsumer(LOG));
        withReuse(true)
        ;
    }
}