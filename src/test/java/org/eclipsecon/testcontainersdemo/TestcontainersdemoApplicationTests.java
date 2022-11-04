package org.eclipsecon.testcontainersdemo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class TestcontainersdemoApplicationTests {
    @Autowired
    MusicAlbumRepository repository;

    @Autowired
    MusicService service;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
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
    void testUniquenessConstraint () {
        // WHEN
        service.createAlbum("The Joshua Tree", "U2");
        assertThatThrownBy(() -> service.createAlbum("The Joshua Tree", "U2"))
            .isInstanceOf(DataIntegrityViolationException.class);
    }
}