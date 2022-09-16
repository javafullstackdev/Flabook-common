package com.msquare.flabook.common.configurations;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.common.aws.IS3Bucket;
import com.msquare.flabook.common.aws.S3PutObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;


@Slf4j
@Component
public class S3Bucket implements IS3Bucket {

    private final AmazonS3 s3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket ;

    public S3Bucket(ResourceLoader resourceLoader, AmazonS3 s3) {
        if(log.isDebugEnabled()) {
            log.debug("resourceLoader : {}", resourceLoader);
        }
        this.s3 = s3;
    }


    @Override
    public Upload upload(String keyPrefix, InputStream file, String filename) throws InterruptedException {
        TransferManager tm = TransferManagerBuilder.standard().withS3Client(s3).build();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        try {
            objectMetadata.setContentLength(file.available());
        } catch (IOException e) {
            log.error("S3Bucket.upload", e);
        }

        S3PutObject putObject = new S3PutObject(file).setKeyPrefix(keyPrefix).setFilename(filename).setMetadata(objectMetadata);
        PutObjectRequest request = putObject.toPutObjectRequest(bucket);
        return tm.upload(request);
    }

    @Override
    public Resource download(String key)  {
        return null;
    }

    public boolean isExists(String key) {
        return s3.doesObjectExist(bucket, key);
    }

    @SuppressWarnings("unused")
    public FileResource downloadFromBucket(String key) throws IOException {
        int lastIndex = key.lastIndexOf("/");
        lastIndex = (lastIndex > -1) ? lastIndex + 1 : lastIndex;
        String filename = (lastIndex > 0) ? key.substring(lastIndex) : key;

        GetObjectRequest getObjectRequest = new GetObjectRequest(bucket, key);
        S3Object s3Object = s3.getObject(getObjectRequest);
        S3ObjectInputStream objectInputStream = s3Object.getObjectContent();

        byte[] bytes = IOUtils.toByteArray(objectInputStream);
        return new FileResource(bytes, filename, key);
    }

    public void delete(String key) {
        s3.deleteObject(bucket, key);
    }

}
