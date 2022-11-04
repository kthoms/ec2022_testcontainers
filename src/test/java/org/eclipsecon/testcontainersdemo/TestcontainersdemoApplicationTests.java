package org.eclipsecon.testcontainersdemo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
class TestcontainersdemoApplicationTests {
    private static final Logger LOG = LoggerFactory.getLogger(TestcontainersdemoApplicationTests.class);

    @Container
    static PostgreSQLContainer container = new PostgreSQLContainer("postgres:latest")
            .withUsername("demo")
            .withPassword("supersecret")
            .withDatabaseName("test");

    @Autowired
    MusicAlbumRepository repository;

    @Autowired
    MusicService service;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        LOG.info("Postgres Test DB URL: {}", container.getJdbcUrl());
    }

    // since SpringBoot 2.2.6
    @DynamicPropertySource
    static void properties (DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }

    @Test
    void testStoreNewAlbum() {
        // GIVEN
        assertThat(repository.count()).isEqualTo(0);

        // WHEN
        MusicAlbum album = service.createAlbum("Memento Mori", "Depeche Mode");

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