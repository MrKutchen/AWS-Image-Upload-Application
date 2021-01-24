package com.kutchen.awsimageupload.bucket;

public enum BucketName {

    //pulled directly from the AWS bucket console - what my s3 bucket database is called
    PROFILE_IMAGE("kutchen-aws-image-upload-123");

    private final String bucketName;

    BucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getBucketName() {
        return bucketName;
    }
}
