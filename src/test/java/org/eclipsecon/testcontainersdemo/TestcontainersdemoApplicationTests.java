package org.eclipsecon.testcontainersdemo;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TestcontainersdemoApplicationTests {
    @Autowired
    PhotoAlbumRepository repository;

    @Autowired
    PhotoService service;

	@Test
	void testStoreNewAlbum() {
        // GIVEN
		assertThat(repository.count()).isEqualTo(0);

        // WHEN
        service.createAlbum("Memento Mori", "Depeche Mode");
        // THEN
        assertThat(repository.count()).isEqualTo(1);
    }

}
