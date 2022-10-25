package org.eclipsecon.testcontainersdemo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
@SuppressWarnings("all")
class TestcontainersdemoApplicationTests {
    private static final Logger LOG = LoggerFactory.getLogger(TestcontainersdemoApplicationTests.class);

    @Container
    static PostgreSQLContainer container = new PostgreSQLContainer("postgres:latest")
            .withUsername("demo")
            .withPassword("supersecret")
            .withDatabaseName("test");
    @Container
    static MinioContainer minioContainer = new MinioContainer();

    static CamundaContainer camundaContainer = new CamundaContainer();

    @Autowired
    PhotoAlbumRepository repository;

    @Autowired
    PhotoService service;

    @BeforeAll
    static void init () {
        camundaContainer.start();
    }

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        LOG.info("Postgres Test DB URL: {}", container.getJdbcUrl());

        Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(LOG);
        minioContainer.followOutput(logConsumer);
    }

    // since SpringBoot 2.2.6
    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);

        registry.add("s3.accessKey", minioContainer::getRootUser);
        registry.add("s3.secretKey", minioContainer::getRootPassword);
        registry.add("s3.endpointUrl", minioContainer::getEndpointUri);

    }

    @Test
    void testStoreNewAlbum() {
        // GIVEN
        assertThat(repository.count()).isEqualTo(0);

        // WHEN
        PhotoAlbum album = service.createAlbum("Memento Mori", "Depeche Mode");

        // THEN
        assertThat(album.getId()).isNotNull();
        assertThat(repository.findById(album.getId()))
                .isPresent();
    }

    @Test
    void testUniquenessConstraint() {
        // WHEN
        service.createAlbum("The Joshua Tree", "U2");
        assertThatThrownBy(() -> service.createAlbum("The Joshua Tree", "U2"))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}