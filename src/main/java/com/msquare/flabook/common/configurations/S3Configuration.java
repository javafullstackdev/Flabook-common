package com.msquare.flabook.common.configurations;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile({"!mock"})
@Configuration
public class S3Configuration {

    //@Value는 Properties에서 값을 가져온다.
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;
    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;
    @Value("${cloud.aws.region.static}")
    private String region;

    //S3Mock을 빌드할때 포트나 메모리에 저장할 지 실제로 저장할 지 같은 것 등등을 설정 가능하다.

    //위에서 작성한 S3Mock을 주입받는 Bean을 작성하였다.
//실제 테스트가 아닌 환경을 위해 작성된 Config환경과 같이 켜질 경우를 대비하여 @Primary를 넣어주었다.
//s3Mock.start를 이용하여 Mock S3 서버를 로컬에서 시작한다.
    @Bean
    public BasicAWSCredentials basicAWSCredentials() {
        return new BasicAWSCredentials(accessKey, secretKey);
    }

    @Bean
    @Primary
    public AmazonS3 amazonS3(AWSCredentials awsCredentials) {
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(region)
                .build();
    }

}
