package com.msquare.flabook.api.service;

import java.nio.charset.StandardCharsets;

import javax.annotation.PostConstruct;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.common.controllers.CommonResponseCode;
import com.msquare.flabook.enumeration.MailType;
import com.msquare.flabook.exception.CommonException;
import com.msquare.flabook.form.CreateMailVo;

@Slf4j
@Service("asyncTask")
@RequiredArgsConstructor
public class SendMailAsyncService {

	@NonNull
	private final JavaMailSender mailSender;

	@Value("${spring.config.activate.on-profile:develop}")
	private String springProfiles;



	@Value("${flabook.password.modify: http://flabook-resource-public.s3.ap-northeast-2.amazonaws.com/modify_password.html}")
	private String PASSWORD_MAIL_URL;


	@PostConstruct
	void PostConstruct() {
		log.info("===========");
		log.info("SendMailAsyncService 비밀번호 변경 페이지 (PASSWORD_MAIL_URL: {})", PASSWORD_MAIL_URL);
		log.info("===========");
	}

	public  String MAIL_TEMPLATE_LOGO_IMAGE = "https://flabook-resource-public.s3.ap-northeast-2.amazonaws.com/group.png";
	public static final String MAIL_TEMPLATE_BACKGROUND_IMAGE = "https://flabook-resource-public.s3.ap-northeast-2.amazonaws.com/3699875.png";

	public static final String MAIL_TEMPLATE = "";
	public static final String MAIL_TEMPLATE_URL = "template/mail_template.html";

	public static final String WELCOME_MAIL_TEMPLATE = "";
	public static final String WELCOME_MAIL_TEMPLATE_URL = "template/mail_welcome.html";
	public static final String WELCOME_MAIL_SUBJECT = "[모야모] 가입을 축하합니다!";
	public static final String WELCOME_MAIL_TITLE = "Welcome!";

	public static final String PASSWORD_MAIL_TEMPLATE = "";


	public static final String PASSWORD_MAIL_TEMPLATE_URL = "template/mail_password.html";
	public static final String PASSWORD_MAIL_SUBJECT = "[모야모] 비밀번호 재설정";
	public static final String PASSWORD_MAIL_TITLE = "비밀번호 변경";


//	public static final String PASSWORD_MAIL_S3_PATH = "http://" + "flabook-resource-public.s3.ap-northeast-2.amazonaws.com/modify_password.html";
//	public static final String PASSWORD_MAIL_S3_PATH_PRODUCT = "http://" + "flabook-resource-public.s3.ap-northeast-2.amazonaws.com/modify_password_product.html";

	public static final String MODIFY_PROFILE_MAIL_TEMPLATE = "";
	public static final String MODIFY_PROFILE_MAIL_TEMPLATE_URL = "template/mail_modify_profile.html";
	public static final String MODIFY_PROFILE_MAIL_SUBJECT = "[모야모] 이메일 인증";
	public static final String MODIFY_PROFILE_MAIL_TITLE = "이메일 인증";

	@SuppressWarnings("unused")
	public static final String MODIFY_PROVIDER_MAIL_TEMPLATE = "";
	@SuppressWarnings("unused")
	public static final String MODIFY_PROVIDER_MAIL_TEMPLATE_URL = "template/mail_modify_provider.html";
	@SuppressWarnings("unused")
	public static final String MODIFY_PROVIDER_MAIL_SUBJECT = "[모야모] 이메일 인증";
	public static final String MODIFY_PROVIDER_MAIL_TITLE = "이메일 인증";

	@Async("executorSample")
	public void sendMail(CreateMailVo createMailVo) {
		try {
			MimeMessage message;
			if(MailType.join.equals(createMailVo.getType())) {
				//가입, 사용자 이메일 인증
				createMailVo.setTemplate(WELCOME_MAIL_TEMPLATE);
				createMailVo.setTemplateUrl(WELCOME_MAIL_TEMPLATE_URL);
				createMailVo.setSubject(WELCOME_MAIL_SUBJECT);
				createMailVo.setTitle(WELCOME_MAIL_TITLE);

			}else if(MailType.resetPassword.equals(createMailVo.getType())) {
				//패스워드 초기화
				createMailVo.setTemplate(PASSWORD_MAIL_TEMPLATE);
				createMailVo.setTemplateUrl(PASSWORD_MAIL_TEMPLATE_URL);
				createMailVo.setSubject(PASSWORD_MAIL_SUBJECT);
				createMailVo.setTitle(PASSWORD_MAIL_TITLE);

			}else if(MailType.modifyProfile.equals(createMailVo.getType())) {
				//프로필, 이메일 등록
				createMailVo.setTemplate(MODIFY_PROFILE_MAIL_TEMPLATE);
				createMailVo.setTemplateUrl(MODIFY_PROFILE_MAIL_TEMPLATE_URL);
				createMailVo.setSubject(MODIFY_PROFILE_MAIL_SUBJECT);
				createMailVo.setTitle(MODIFY_PROFILE_MAIL_TITLE);

			}else if(MailType.modifyProvider.equals(createMailVo.getType())) {
				//기존 휴대폰 로그인 사용자, provider 변경
				createMailVo.setTemplate(WELCOME_MAIL_TEMPLATE);
				createMailVo.setTemplateUrl(WELCOME_MAIL_TEMPLATE_URL);
				createMailVo.setSubject(WELCOME_MAIL_SUBJECT);
				createMailVo.setTitle(MODIFY_PROVIDER_MAIL_TITLE);

			}else {
				log.error("지원하지 않는 타입의 메일입니다.");
				throw new CommonException(CommonResponseCode.FAIL, false);
			}

			message = createMailTemplate(createMailVo); //메일 템플릿 생성
			message.addRecipient(RecipientType.TO, new InternetAddress(createMailVo.getEmail()));

			mailSender.send(message);
			log.error("sendMail Success");

		} catch (Exception e) {
			log.error("sendMail ERROR", e);
			throw new CommonException(CommonResponseCode.USER_EMAIL_LATE_FAILL, false);
		}

	}

	public MimeMessage createMailTemplate(CreateMailVo createMailVo) throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();

		String template = readMailTemplate(MAIL_TEMPLATE, MAIL_TEMPLATE_URL); //기본 메일 템플릿 저장
        String htmlContent = readMailTemplate(createMailVo.getTemplate(), createMailVo.getTemplateUrl()); //메일 type에 해당하는 content 템플릿 저장

        if(createMailVo.getNickName() != null) {
        	htmlContent = htmlContent.replace("{=userName}", createMailVo.getNickName());
        }
        if(createMailVo.getEmail() != null) {
        	htmlContent = htmlContent.replace("{=email}", createMailVo.getEmail());
        }
        if(createMailVo.getAuthKey() != null) {
        	htmlContent = htmlContent.replace("{=authKey}", createMailVo.getAuthKey());
        }
        if(createMailVo.getPassword() != null) {
        	htmlContent = htmlContent.replace("{=password}", createMailVo.getPassword());
        }

		htmlContent = htmlContent.replace("{=s3path}", PASSWORD_MAIL_URL);


        template = template.replace("{=logoImg}", MAIL_TEMPLATE_LOGO_IMAGE);
        template = template.replace("{=backgroundImg}", MAIL_TEMPLATE_BACKGROUND_IMAGE);
        template = template.replace("{=title}", createMailVo.getTitle());
        template = template.replace("{=content}", htmlContent);

		message.setSubject(createMailVo.getSubject());
		message.setText(template, "UTF-8", "html");

		return message;
	}

	public static String readMailTemplate(String template, String templateUrl) {
		try {
			if(!"".equals(template)) {
				return template;
			}

			System.getProperty("file.separator");

			ClassPathResource resource = new ClassPathResource(templateUrl);
			byte[] bdata = FileCopyUtils.copyToByteArray(resource.getInputStream());
			template = new String(bdata, StandardCharsets.UTF_8);

		} catch (Exception e) {
			log.error("readMailTemplate ERROR", e);
			throw new CommonException(CommonResponseCode.FAIL, false);
		}

		return template;
	}
}
