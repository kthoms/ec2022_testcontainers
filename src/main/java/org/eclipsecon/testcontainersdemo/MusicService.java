package org.eclipsecon.testcontainersdemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MusicService {

    private final MusicAlbumRepository musicAlbumRepository;

    @Autowired
    public MusicService(MusicAlbumRepository musicAlbumRepository) {

        this.musicAlbumRepository = musicAlbumRepository;
    }

    public MusicAlbum createAlbum (String name, String artist) {
        var album = new MusicAlbum();
        album.setName(name);
        album.setArtist(artist);
        musicAlbumRepository.save(album);
        return album;
    }

}