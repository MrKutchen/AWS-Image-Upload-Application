package com.kutchen.awsimageupload.filestore;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

@Service
public class FileStore {

    private final AmazonS3 s3;

    @Autowired
    //constructor inject
    public FileStore(AmazonS3 s3) {
        this.s3 = s3;
    }

    //method that allows users to save the uploaded file to the s3 database
    public void save(String path,
                     String fileName,
                     Optional<Map<String, String>> optionalMetadata,
                     InputStream inputStream) {
        //objects metadata that is being stored
        ObjectMetadata metadata = new ObjectMetadata();
        optionalMetadata.ifPresent(map -> {
            //if not empty add the metadata to the database
            if (!map.isEmpty()) {
                map.forEach(metadata::addUserMetadata);
            }
        });
        //need a try catch in case an exception is thrown
        try {
            s3.putObject(path, fileName, inputStream, metadata);
        } catch (AmazonServiceException e) {
            throw new IllegalStateException("Failed to store file to s3", e);
        }
    }

    // method used to download images for display on web browser
    public byte[] download(String path, String key) {
        try {
            S3Object object = s3.getObject(path, key);
            //sending object to be displayed
            return IOUtils.toByteArray(object.getObjectContent());

            //handling exception if not able to be downloaded
        } catch (AmazonServiceException | IOException e) {
            throw new IllegalStateException("Failed to download file to s3", e);
        }
    }
}
