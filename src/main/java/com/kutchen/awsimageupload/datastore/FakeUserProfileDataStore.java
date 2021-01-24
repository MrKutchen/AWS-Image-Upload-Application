package com.kutchen.awsimageupload.datastore;

import com.kutchen.awsimageupload.profile.UserProfile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


//created a small fake database to speed up the process
//must upload a new image everytime server is started for images to appear, this does not persist the data
@Repository
public class FakeUserProfileDataStore {

    private static final List<UserProfile> USER_PROFILES = new ArrayList<>();

    static {
        //used UUIDs for the UserProfile Ids
        USER_PROFILES.add(new UserProfile(UUID.fromString("64bb85cf-bac6-4a38-afe2-811d88bf2f0e"), "adampugh", null));
        USER_PROFILES.add(new UserProfile(UUID.fromString("f925ec2d-f782-4b07-b8b4-3f42371606df"), "juncranker", null));
    }

    //returning all profiles in the static initializer
    public List<UserProfile> getUserProfiles() {
        return USER_PROFILES;
    }
}
