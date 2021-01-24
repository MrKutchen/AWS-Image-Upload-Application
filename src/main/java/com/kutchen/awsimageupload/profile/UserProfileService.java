package com.kutchen.awsimageupload.profile;

import com.kutchen.awsimageupload.bucket.BucketName;
import com.kutchen.awsimageupload.filestore.FileStore;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

//business logic
@Service
public class UserProfileService {

    private final UserProfileDataAccessService userProfileDataAccessService;
    private final FileStore fileStore;

    @Autowired
    public UserProfileService(UserProfileDataAccessService userProfileDataAccessService,
                              FileStore fileStore) {
        this.userProfileDataAccessService = userProfileDataAccessService;
        this.fileStore = fileStore;
    }

    //getting all userProfiles
    List<UserProfile> getUserProfiles() {
        return userProfileDataAccessService.getUserProfiles();
    }

    //multi step method to upload an image to the userProfile
    void uploadUserProfileImage(UUID userProfileId, MultipartFile file) {
        //Call method to check if image is not empty
        isFileEmpty(file);
        //Call method to see if file is an image
        isImage(file);
        //create a variable with the user information
        UserProfile user = getUserProfileOrThrow(userProfileId);

        //Create metadata variable by calling extractMetadata
        Map<String, String> metadata = extractMetaData(file);

        //Store the image in s3 and update database (userProfileImageLink) with s3 image link
        //create a path variable that has the name of the s3 bucket and the specific profile to store the metadata
        String path = String.format("%s/%s", BucketName.PROFILE_IMAGE.getBucketName(),
                user.getUserProfileId());
        //create a filename variable
        String filename = String.format("%s-%s", file.getOriginalFilename(), UUID.randomUUID());
        //try catch in cause of an exception
        try {
            fileStore.save(path, filename, Optional.of(metadata), file.getInputStream());
            user.setUserProfileImageLink(filename);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    //download image from the userprofile in the s3 bucket
    public byte[] downloadUserProfileImage(UUID userProfileId) {
        UserProfile user = getUserProfileOrThrow(userProfileId);

        String path = String.format("%s/%s",
                BucketName.PROFILE_IMAGE.getBucketName(),
                user.getUserProfileId());

        //return the image from the s3 profile with mapping from the fileStore
        return user.getUserProfileImageLink()
                .map(key -> fileStore.download(path, key))
                .orElse(new byte[0]);
    }


    //get the metadata from the file by placing the information in a Map then return that information
    private Map<String, String> extractMetaData(MultipartFile file) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));
        return metadata;
    }

    //return the specific UserProfile by filtering the UserProfiles
    private UserProfile getUserProfileOrThrow(UUID userProfileId) {
        return userProfileDataAccessService
                .getUserProfiles()
                .stream()
                .filter(userProfile -> userProfile.getUserProfileId().equals(userProfileId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("User profile %s not found", userProfileId)));
    }

    //checking to see if the image is a specific format/type
    //
    private void isImage(MultipartFile file) {
        //if not one of these throw an exception
        if (!Arrays.asList(
                //Returns the MIME type for the data.
                //This is a simple access method that returns the value of the mimeType attribute.
                //A media type (also known as a Multipurpose Internet Mail Extensions or MIME type)
                //is a standard that indicates the nature and format of a document,
                //file, or assortment of bytes.
                ContentType.IMAGE_JPEG.getMimeType(),
                ContentType.IMAGE_PNG.getMimeType(),
                ContentType.IMAGE_GIF.getMimeType()).contains(file.getContentType())) {
            throw new IllegalStateException("File must be an image [ " + file.getContentType() + "]");
        }
    }

    //check to see if the file is empty, if it is throw an exception
    private void isFileEmpty(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalStateException("Cannot upload empty file [ " + file.getSize() + "]");
        }
    }
}
