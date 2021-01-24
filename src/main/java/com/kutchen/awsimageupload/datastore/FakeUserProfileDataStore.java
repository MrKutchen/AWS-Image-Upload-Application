package com.kutchen.awsimageupload.datastore;

import com.kutchen.awsimageupload.profile.UserProfile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class FakeUserProfileDataStore {

    private static final List<UserProfile> USER_PROFILES = new ArrayList<>();

    static {
        USER_PROFILES.add(new UserProfile(UUID.fromString("64bb85cf-bac6-4a38-afe2-811d88bf2f0e"), "adampugh", null));
        USER_PROFILES.add(new UserProfile(UUID.fromString("f925ec2d-f782-4b07-b8b4-3f42371606df"), "juncranker", null));
    }

    public List<UserProfile> getUserProfiles() {
        return USER_PROFILES;
    }
}
