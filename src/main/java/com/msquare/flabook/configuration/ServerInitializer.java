package com.msquare.flabook.configuration;

import com.drew.imaging.ImageProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.common.configurations.S3Bucket;
import com.msquare.flabook.models.Badge;
import com.msquare.flabook.repository.BadgeRepository;
import com.msquare.flabook.service.FolderDatePatterns;
import com.msquare.flabook.service.ImageUploadService;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ServerInitializer implements ApplicationRunner {

    private static final String DELIMITER = "/";
    private static final String BOOLEAN_FALSE = "false";

    private final S3Bucket bucket;
    private final ResourceLoader resourceLoader;

    private final BadgeRepository badgeRepository;
    private final ImageUploadService imageUploadService;

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        initApplication();
    }

    @SuppressWarnings("unused")
    private void recursive(Resource resource,  String prefix,  List<Resource> container) throws IOException {
        if(!resource.isFile()) {
            log.info("resource {}", resource.getURI().getPath());
            String path = (prefix != null && prefix.length() > 0) ? prefix + DELIMITER + resource.getFile().getName() :  resource.getFile().getName() ;

            String classPath = "classpath:" +  path + "/*";
            log.info("path : {}, classpath : {}", path, classPath);
            Resource[] resources = ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources(classPath);
            for(Resource r : resources) {
                recursive(r, path,  container);
            }
        } else if(resource.isFile()){
            container.add(resource);
        }
    }

    private void initImageResource() throws IOException, InterruptedException {
        ResourcePatternResolver resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        Resource[] resources = resourcePatternResolver.getResources("classpath*:/static/**/*");

        for(Resource resource :resources) {
            if(!resource.isReadable()) continue;

            String path = resource.getURI().toString();
            path = path.replace(File.separator, "/");
            String key = path.substring(path.indexOf("/static") + 8);
            String prefix = key.substring(0, key.lastIndexOf("/"));
            String filename = resource.getFilename();
            if(bucket.isExists(key)) {
                log.info("exists commons uri : {}, prefix : {}, filename :{}", key, prefix, resource.getFilename());
            } else {
                log.info("upload commons uri : {}, prefix : {}, filename :{}", key, prefix, resource.getFilename());
                bucket.upload(prefix, resource.getInputStream(), filename);
            }
        }
    }

    private void initBadge() {
        if(badgeRepository.count() == 0L) {

            List<String[]> names = Arrays.asList( //순서 고정하기
                    new String[]{"빠른 속도", "빠른 댓글을 남기셨군요!", "앞으로도 이름이 모야에서의 빠른댓글 활약 기대할게요!", "static/commons/badges/badges_active/빠른속도_active.png", "static/commons/badges/badges_inactive/빠른속도_inactive.png"},
                    new String[]{"발빠른 속도", "발빠른 댓글을 남기셨군요!", "이름이 모야에서 없어서는 안될 당신은 핵심 인재입니다!", "static/commons/badges/badges_active/발빠른속도_active.png", "static/commons/badges/badges_inactive/발빠른속도_inactive.png"},
                    new String[]{"빛의 속도", "빛의 속도의 댓글을 남기셨군요!", "이름이 모야에서 많고 빠른 댓글을 단 당신! 대단해요!", "static/commons/badges/badges_active/빛의속도_active.png", "static/commons/badges/badges_inactive/빛의속도_inactive.png"},

                    new String[]{"댓글 만사마", "이름이 모야에서 만개의 댓글을 남기셨군요!", "식물 전문가로 이름을 날리시기 시작했습니다!", "static/commons/badges/badges_active/댓글만사마_active.png", "static/commons/badges/badges_inactive/댓글만사마_inactive.png", BOOLEAN_FALSE},
                    new String[]{"댓글 십만사마", "이름이 모야에서 십만개의 댓글을 남기셨군요!", "당신은 손꼽히는 식물 전문가!", "static/commons/badges/badges_active/댓글십만사마_active.png", "static/commons/badges/badges_inactive/댓글십만사마_inactive.png", BOOLEAN_FALSE},
                    new String[]{"댓글 백만사마", "이름이 모야에서 백만개의 댓글을 남기셨군요!", "세상에서 당신을 따를 전문가는 없을 겁니다!", "static/commons/badges/badges_active/댓글백만사마_active.png", "static/commons/badges/badges_inactive/댓글백만사마_inactive.png", BOOLEAN_FALSE},

                    new String[]{"식물 의원", "식물클리닉에서 10회 채택 받으셨군요!", "앞으로도 유익한 치료법을 안내해주세요!", "static/commons/badges/badges_active/식물의원_active.png", "static/commons/badges/badges_inactive/식물의원_inactive.png"},
                    new String[]{"식물 명의", "식물클리닉에서 100회 채택 받으셨군요!", "앞으로도 많은 식물의 건강을 부탁드려요!", "static/commons/badges/badges_active/식물명의_active.png", "static/commons/badges/badges_inactive/식물명의_inactive.png"},
                    new String[]{"식물 어의", "식물클리닉에서 500회 채택을 받으셨군요!", "당신을 식물 전문의로 임명합니다!", "static/commons/badges/badges_active/식물어의_active.png", "static/commons/badges/badges_inactive/식물어의_inactive.png"},

                    new String[]{"첫 질문의 설렘", "모야모에서 첫 질문을 올리셨네요!", "앞으로도 모야모에서 다양한 활동 기대할께요!", "static/commons/badges/badges_active/첫질문의설렘_active.png", "static/commons/badges/badges_inactive/첫질문의설렘_inactive.png"},

                    new String[]{"배우는 즐거움", "매거진 글 재밌게 읽어보고 계신가요?", "앞으로도 다양한 식물의 정보를 알려드릴게요!", "static/commons/badges/badges_active/배우는즐거움_active.png", "static/commons/badges/badges_inactive/배우는즐거움_inactive.png"},
                    new String[]{"학습의 기쁨", "매거진 글이 많이 도움되고 있나요?", "앞으로도 유익한 식물 정보 기대해주세요!", "static/commons/badges/badges_active/학습의기쁨_active.png", "static/commons/badges/badges_inactive/학습의기쁨_inactive.png"},
                    new String[]{"공부의 신", "매거진에서 많은 글을 보셨군요!", "당신을 매거진 공부의 신으로 임명합니다!", "static/commons/badges/badges_active/공부의신_active.png", "static/commons/badges/badges_inactive/공부의신_inactive.png"},

                    new String[]{"금손", "손재주가 좋은 당신!", "금손이 틀림없네요!", "static/commons/badges/badges_active/금손_active.png", "static/commons/badges/badges_inactive/금손_inactive.png"},

                    new String[]{"소통왕", "자유수다에서 즐거운 수다를 나누는 당신!", "모야모의 소통왕이네요!", "static/commons/badges/badges_active/소통왕_active.png", "static/commons/badges/badges_inactive/소통왕_inactive.png"},

                    new String[]{"이름 학사", "이름이 모야에서 이름 학사를 취득하셨군요!", "랭킹에 처음 등극하신걸 축하해요!", "static/commons/badges/badges_active/이름학사_active.png", "static/commons/badges/badges_inactive/이름학사_inactive.png"},
                    new String[]{"이름 석사", "이름이 모야에서 이름 석사를 취득하셨군요!", "조금만 더 힘내서 이름 박사에 도전해보세요!", "static/commons/badges/badges_active/이름석사_active.png", "static/commons/badges/badges_inactive/이름석사_inactive.png"},
                    new String[]{"이름 박사", "척척박사님 축하해요! 이름 박사를 취득하셨군요!", "당신은 이미 이름 전문가!", "static/commons/badges/badges_active/이름박사_active.png", "static/commons/badges/badges_inactive/이름박사_inactive.png"},

                    new String[]{"클리닉 학사", "식물클리닉에서 클리닉 학사를 취득하셨군요!", "랭킹에 처음 등극하신걸 축하해요!", "static/commons/badges/badges_active/클리닉학사_active.png", "static/commons/badges/badges_inactive/클리닉학사_inactive.png"},
                    new String[]{"클리닉 석사", "식물클리닉에서 클리닉 석사를 취득하셨군요!", "조금만 더 힘내서 클리닉 박사에 도전해보세요!", "static/commons/badges/badges_active/클리닉석사_active.png", "static/commons/badges/badges_inactive/클리닉석사_inactive.png"},
                    new String[]{"클리닉 박사", "척척박사님 축하해요! 클리닉 박사를 취득하셨군요!", "당신은 이미 클리닉 전문가!", "static/commons/badges/badges_active/클리닉박사_active.png", "static/commons/badges/badges_inactive/클리닉박사_inactive.png"},

                    new String[]{"작가지망생", "베스트 글 랭킹에 처음 등극하신걸 축하해요!", "작가의 가능성이 무궁무진하네요!", "static/commons/badges/badges_active/작가지망생_active.png", "static/commons/badges/badges_inactive/작가지망생_inactive.png"},
                    new String[]{"등단 작가", "여러 번 베스트 글 랭킹에 오르셨군요!", "모야모의 등단작가로 임명합니다!", "static/commons/badges/badges_active/등단작가_active.png", "static/commons/badges/badges_inactive/등단작가_inactive.png"},
                    new String[]{"프로작가", "랭킹에 100번 등극한 당신!", "프로작가 등단을 축하해요!", "static/commons/badges/badges_active/프로작가_active.png", "static/commons/badges/badges_inactive/프로작가_inactive.png"},

                    new String[]{"하트요정", "좋아요를 눌러주는 당신!", "사랑이 넘치는 당신은 바로 하트요정!", "static/commons/badges/badges_active/하트요정_active.png", "static/commons/badges/badges_inactive/하트요정_inactive.png"},

                    new String[]{"터줏대감", "모야모의 자랑이자 보배인 당신!", "진정한 모야모의 터줏대감이에요!", "static/commons/badges/badges_active/터줏대감_active.png", "static/commons/badges/badges_inactive/터줏대감_inactive.png",BOOLEAN_FALSE},

                    new String[]{"매너왕", "감사의 마음을 전하는 당신!", "마음도 예쁜 매너왕이에요!", "static/commons/badges/badges_active/매너왕_active.png", "static/commons/badges/badges_inactive/매너왕_inactive.png"},
                    new String[]{"미소천사", "항상 행복한 웃음을 전하는 당신!", "모야모의 얼굴입니다^^", "static/commons/badges/badges_active/미소천사_active.png", "static/commons/badges/badges_inactive/미소천사_inactive.png"}

            );
            AtomicInteger idx = new AtomicInteger(1);

            List<Badge> entities = names.stream().map( texts -> {
                String title = texts[0];
                String des1 = texts[1];
                String des2 = texts[2];
                String truePath = texts[3];
                String falsePath = texts[4];

                Badge badge = new Badge();
                badge.setTitle(title);
                badge.setDescription1(des1);
                badge.setDescription2(des2);
                badge.setActive(texts.length <= 5);
                badge.setOrderCount(idx.getAndIncrement());

                try {
                    ClassPathResource trueResource = new ClassPathResource(truePath);
                    ClassPathResource falseResource = new ClassPathResource(falsePath);

                    File trueFile = new File(truePath);
                    FileItem trueFileItem = new DiskFileItem(StringUtils.replace(title," ","")+"_active", Files.probeContentType(trueFile.toPath()), false, trueFile.getName(), (int) trueFile.length(), trueFile.getParentFile());
                    IOUtils.copy(trueResource.getInputStream(), trueFileItem.getOutputStream());
                    MultipartFile trueMultipartFile = new CommonsMultipartFile(trueFileItem);

                    File falseFile = new File(falsePath);
                    FileItem falseFileItem = new DiskFileItem(StringUtils.replace(title," ","")+"_inactive", Files.probeContentType(falseFile.toPath()), false, falseFile.getName(), (int) falseFile.length(), falseFile.getParentFile());
                    IOUtils.copy(falseResource.getInputStream(), falseFileItem.getOutputStream());
                    MultipartFile falseMultipartFile = new CommonsMultipartFile(falseFileItem);

                    ImageUploadService.ImageResourceInfoWithMetadata trueImageResource = imageUploadService.uploadWithMeta(FolderDatePatterns.BADGES, trueMultipartFile);
                    ImageUploadService.ImageResourceInfoWithMetadata falseImageResource = imageUploadService.uploadWithMeta(FolderDatePatterns.BADGES, falseMultipartFile);

                    badge.setTrueImageResource(trueImageResource.getImageResource());
                    badge.setFalseImageResource(falseImageResource.getImageResource());

                } catch (ImageProcessingException | IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }

                return badge;
            }).collect(Collectors.toList());

            badgeRepository.saveAll(entities);
        }
    }

    private void initApplication() throws IOException, InterruptedException {
        log.info("initApplication");
        initImageResource();
        initBadge();
    }


}
