package com.msquare.flabook.common.aws;

import com.amazonaws.services.s3.transfer.Upload;
import org.springframework.core.io.Resource;

import java.io.FileNotFoundException;
import java.io.InputStream;

public interface IS3Bucket {


    /**
     *
     * @param keyPrefix 디렉토리 구조 /2019/01/01
     * @param file inputstream
     * @return Upload
     */
    Upload upload(String keyPrefix, InputStream file, String filename)  throws FileNotFoundException, InterruptedException ;

    Resource download(String key)  throws FileNotFoundException, InterruptedException ;
}
