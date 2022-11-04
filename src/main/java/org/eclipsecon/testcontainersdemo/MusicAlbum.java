package org.eclipsecon.testcontainersdemo;

import javax.persistence.*;

@Entity
@Table(name = "PHOTO_ALBUM", uniqueConstraints = @UniqueConstraint(name = "ALBUM_NAME_ARTIST", columnNames = {"name", "artist"}))
public class MusicAlbum {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String artist;


    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}