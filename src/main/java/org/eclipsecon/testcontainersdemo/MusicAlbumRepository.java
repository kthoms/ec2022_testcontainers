package org.eclipsecon.testcontainersdemo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MusicAlbumRepository extends JpaRepository<MusicAlbum,Long> {

}