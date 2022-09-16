package com.msquare.flabook.common.aws;

import java.io.InputStream;
import java.util.UUID;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class S3PutObject {

    private static final String SEPARATOR = "/";

    /**
     * 메타데이터 설정값
     */
    @Accessors(chain = true)
    private ObjectMetadata metadata;

    @NonNull
    @Accessors(chain = true)
    private InputStream file;

    /**
     * 원본파일명, 없을경우 랜덤 생성
     */
    @Accessors(chain = true)
    private String filename;

    @Accessors(chain = true)
    private boolean randomFilename = false;

    @Accessors(chain = true)
    private Integer defaultCacheControl;

    /**
     * 디렉토리 구조 /{path}/{subpath}
     */
    @Accessors(chain = true)
    private String keyPrefix = null;

    private String generateFilename() {
        if(this.randomFilename || this.filename == null) {
            return UUID.randomUUID().toString();
        } else {
            return this.filename;
        }
    }

    private String generateKey() {
        StringBuilder stringBuffer =  new StringBuilder();
        String prefix = this.keyPrefix;

        if(prefix != null) {
            stringBuffer.append(prefix);

            if (!prefix.endsWith(SEPARATOR))
                stringBuffer.append(SEPARATOR);
        }

        return stringBuffer.append(generateFilename()).toString();
    }

    public PutObjectRequest toPutObjectRequest(String bucket) throws InterruptedException {
        PutObjectRequest request;
        if(defaultCacheControl != null) {
            metadata.setCacheControl(defaultCacheControl.toString());
        }

        String key = generateKey();
        log.debug("S3PutObject key : {}", key);
        request = new PutObjectRequest(bucket, key, file, metadata);
        return request;
    }


}
