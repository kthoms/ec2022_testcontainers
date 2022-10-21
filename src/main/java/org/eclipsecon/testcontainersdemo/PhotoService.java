package org.eclipsecon.testcontainersdemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PhotoService {

    private final PhotoAlbumRepository photoAlbumRepository;

    @Autowired
    public PhotoService (PhotoAlbumRepository photoAlbumRepository) {

        this.photoAlbumRepository = photoAlbumRepository;
    }

    public void createAlbum (String name, String artist) {
        var album = new PhotoAlbum();
        album.setName(name);
        album.setArtist(artist);
        photoAlbumRepository.save(album);
    }

}