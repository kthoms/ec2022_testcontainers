package org.eclipsecon.testcontainersdemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

@Service
public class PhotoService {
    private static final Logger LOG = LoggerFactory.getLogger(PhotoService.class);

    private final PhotoAlbumRepository photoAlbumRepository;
    private final S3Client s3;

    @Autowired
    public PhotoService(PhotoAlbumRepository photoAlbumRepository, S3Client s3Client) {
        this.photoAlbumRepository = photoAlbumRepository;
        this.s3 = s3Client;
    }

    public PhotoAlbum createAlbum(String name, String artist) {
        var album = new PhotoAlbum();
        album.setName(name);
        album.setArtist(artist);
        photoAlbumRepository.save(album);

        String bucketName = "album-" + album.getId();

        LOG.info("Creating bucket {}", bucketName);
        var createBucketRequest = CreateBucketRequest.builder().bucket(bucketName).build();
        s3.createBucket(createBucketRequest);
        LOG.info("Bucket {} created.", bucketName);
        return album;
    }

}