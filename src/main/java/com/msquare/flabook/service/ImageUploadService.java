package com.msquare.flabook.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.model.UploadResult;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.format.Format;
import com.sksamuel.scrimage.format.FormatDetector;
import com.sksamuel.scrimage.nio.GifWriter;
import com.sksamuel.scrimage.nio.JpegWriter;
import com.sksamuel.scrimage.nio.PngWriter;
import com.vividsolutions.jts.geom.Point;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.exception.FlabookOException;
import com.msquare.flabook.models.ImageResource;
import com.msquare.flabook.util.GeoUtils;
import com.msquare.flabook.util.ImageMetaUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageUploadService {

    @NonNull
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.public:false}")
    private Boolean publicRead;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageResourceInfo {
        @NonNull
        private ImageResource imageResource;
        @NonNull
        private String dimension;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @NoArgsConstructor
    public static class ImageResourceInfoWithMetadata extends ImageResourceInfo {
        private Point point;
        private String placeName;

        public ImageResourceInfoWithMetadata(ImageResourceInfo info, Point point) {
            this.setImageResource(info.imageResource);
            this.setDimension(info.dimension);
            this.point = point;
        }
    }


    /**
     * <이미지정보, <해상도, 위치정보>>
     *
     * @param dirDatePattern 디렉토리 패턴
     * @param file MultipartFile
     * @return ImageResourceInfo
     * @throws InterruptedException e
     * @throws IOException e
     */
    public ImageResourceInfo upload(String dirDatePattern, MultipartFile file) throws InterruptedException, IOException {
        return this.upload(dirDatePattern, file, true);
    }

    public ImageResourceInfo upload(String dirDatePattern, MultipartFile file, boolean resizing) throws InterruptedException, IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dirDatePattern);
        String folder = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("Asia/Seoul")).format(formatter);

        String sourceFileName = file.getOriginalFilename();

        //null이 되지 않음
        String sourceFileNameExtension = FilenameUtils.getExtension(sourceFileName);
        if (sourceFileNameExtension != null && sourceFileNameExtension.length() > 0) {
            sourceFileNameExtension = "." + sourceFileNameExtension.toLowerCase();
        }

        ImageProcessedData resolutionWithResolution =  imageProcess(file, resizing);
        Upload upload = this.upload(folder, new ByteArrayInputStream(resolutionWithResolution.getImageData()), UUID.randomUUID() + sourceFileNameExtension, resolutionWithResolution.getImageData().length);
        UploadResult result = upload.waitForUploadResult();

        ImageResource imageResource = new ImageResource(result.getKey(), file.getOriginalFilename()); //NOSONAR
        return new ImageResourceInfo(imageResource, resolutionWithResolution.dimension);

    }

    public ImageResourceInfoWithMetadata uploadWithMeta(String dirDatePattern, MultipartFile file) throws InterruptedException, IOException, ImageProcessingException {
        return this.uploadWithMeta(dirDatePattern, file, true);
    }

    public ImageResourceInfoWithMetadata uploadWithMeta(String dirDatePattern, MultipartFile file, boolean resizing) throws InterruptedException, IOException, ImageProcessingException {

        Point point = null;
        if(!file.isEmpty()) {
            ImageMetaUtil metaUtil = new ImageMetaUtil(ImageMetadataReader.readMetadata(file.getInputStream(), file.getBytes().length));
            for (GeoLocation geoLocation : metaUtil.getGeoLocation()) {
                point = GeoUtils.getInstance().createPoint(geoLocation.getLongitude(), geoLocation.getLatitude());
                if(point != null)
                    break;
            }
            ImageResourceInfo imageResourceInfo = upload(dirDatePattern, file, resizing);
            return new ImageResourceInfoWithMetadata(imageResourceInfo, point);
        } else {
            throw new FlabookOException("업로드된 파일을 찾을 수 없습니다.");
        }
    }

    private static final int MIN_IMAGE_LENGTH = 1920;

    @Data
    @AllArgsConstructor
    private static class ImageProcessedData {
        private byte[] imageData;
        private String dimension;
    }
    /**
     * 메타 제거, 위치정보 추출
     * @param multipartFile file
     * @return ImageProcessedData
     * @throws IOException e
     */
    private ImageProcessedData imageProcess(MultipartFile multipartFile, boolean resizing) throws IOException {
        Format format = FormatDetector.detect(multipartFile.getBytes()).orElseThrow(() -> new IllegalStateException("알 수 없는 이미지 타입"));
        ImmutableImage origin = ImmutableImage.loader().fromBytes(multipartFile.getBytes());

        int originHeight = origin.height;
        int originWidth = origin.width;

        int scaledHeight = originHeight;
        int scaledWidth = originWidth;

        if(originHeight < originWidth) {
            if(originHeight > MIN_IMAGE_LENGTH) {
                scaledHeight = MIN_IMAGE_LENGTH;
                double ratio = (double)scaledHeight / (double)originHeight;
                scaledWidth = (int)(ratio * originWidth);
            }

        } else {
            if(originWidth > MIN_IMAGE_LENGTH) {
                scaledWidth = MIN_IMAGE_LENGTH;
                double ratio = (double)scaledWidth / (double)originWidth;
                scaledHeight = (int)(ratio * originHeight);
            }
        }

        if(log.isDebugEnabled()) {
            log.debug("resize image origin ({}, {}), scaled ({}, {})", originWidth, originHeight, scaledWidth, scaledHeight);
        }

        com.sksamuel.scrimage.nio.ImageWriter writer;
        switch (format) {
            case GIF:
                writer = GifWriter.Default.withProgressive(true);
                break;
            case JPEG:
                writer = JpegWriter.Default.withProgressive(true);
                break;
            case PNG:
                writer = PngWriter.MaxCompression;
                break;
            default:
                throw new IOException("알 수 없는 이미지 타입입니다.");
        }

        if(!resizing){
            return new ImageProcessedData(origin.forWriter(writer).bytes(), String.format("%d,%d", originWidth, originHeight));
        }
        return new ImageProcessedData(origin.scaleTo(scaledWidth, scaledHeight).forWriter(writer).bytes(), String.format("%d,%d", scaledWidth, scaledHeight));
    }

    private Upload upload(String keyPrefix, InputStream file, String filename, long fileLength) {
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentLength(fileLength);
        TransferManager tm = TransferManagerBuilder.standard().withS3Client(amazonS3).build();
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, keyPrefix + filename, file, meta );
        if(BooleanUtils.isTrue(publicRead)) {
            putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead);
        } else {
            putObjectRequest.withCannedAcl(CannedAccessControlList.Private);
        }
        return tm.upload(putObjectRequest);
    }

    public void delete(String key){
        try{
            //Delete객체 생성
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(this.bucket, key);
            //delete
            this.amazonS3.deleteObject(deleteObjectRequest);
            System.out.println(String.format("[%s] deletion complete", key));
        } catch (AmazonServiceException e){
            e.printStackTrace();
        } catch (SdkClientException e){
            e.printStackTrace();
        }
    }

}
