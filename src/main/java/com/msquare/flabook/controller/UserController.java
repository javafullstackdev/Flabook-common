package com.msquare.flabook.controller;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.api.service.SendMailAsyncService;
import com.msquare.flabook.common.controllers.CommonResponse;
import com.msquare.flabook.common.controllers.CommonResponseCode;
import com.msquare.flabook.dto.PostingDto;
import com.msquare.flabook.dto.UserDto;
import com.msquare.flabook.dto.UserWithSecurityDto;
import com.msquare.flabook.enumeration.MailType;
import com.msquare.flabook.enumeration.PostingType;
import com.msquare.flabook.exception.CommonException;
import com.msquare.flabook.exception.FlabookGlobalException;
import com.msquare.flabook.form.CreateMailVo;
import com.msquare.flabook.form.UpdateProfileAvatarVo;
import com.msquare.flabook.form.auth.*;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.models.User;
import com.msquare.flabook.service.AdoptCommentService;
import com.msquare.flabook.service.PostingService;
import com.msquare.flabook.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings({"java:S4684", "unused"})
@Slf4j
@Api(tags = {"2. User 프로필관리"})
@RequiredArgsConstructor
@RestController
@RequestMapping("/v2/users")
public class UserController {

    private final SendMailAsyncService sendMailAsyncService;
    private final UserService userService;
    private final PostingService postingService;
    private final AdoptCommentService adoptCommentService;
    @Qualifier("userCircuitBreaker")
    private final CircuitBreaker cb;

    @JsonView(Views.MyProfileDetailJsonView.class)
    @GetMapping(path = "/me")
    public CommonResponse<UserDto> doFindMyProfile(@ApiIgnore User currentUser) {
//        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), UserWithSecurityDto.of(currentUser));
        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), UserWithSecurityDto.ofPhotoEnable(currentUser));
    }

    @JsonView(Views.UserProfileDetailJsonView.class)
    @GetMapping(path = "/{id}")
    public CommonResponse<UserDto> doFindUserProfile(@ApiIgnore User currentUser, @PathVariable Long id) {
        return cb.run(() -> userService.findUser(id).map(user -> new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), user))
                .orElseGet(() -> new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), null, "사용자를 찾을 수 없습니다.")), throwable -> {
            log.error("UserController.findUserProfile", throwable);
            return new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), null, FlabookGlobalException.Messages.PLEASE_WAIT);
        });
    }

    @JsonView(Views.BaseView.class)
    @GetMapping(path = "/me/scraps")
    public CommonResponse<List<PostingDto>> doFindScrapList(@ApiIgnore User currentUser, @RequestParam(value = "postingType", required = false) PostingType type, @RequestParam(required = false) Long sinceId, @RequestParam(required = false) Long maxId, @RequestParam(required = false, defaultValue = "10") int count) {
        return cb.run(() -> {
            List<PostingDto> postingDtoList = postingService.findScrapListByUser(currentUser.getId(), type, sinceId, maxId, count);
            return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), postingDtoList);
        }, throwable -> {
            log.error("UserController.doFindScrapList", throwable);
            return new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), null, FlabookGlobalException.Messages.PLEASE_WAIT);
        });
    }

    @JsonView(Views.BaseView.class)
    @GetMapping(path = "/me/likes")
    public CommonResponse<List<PostingDto>>  doFindLikeList(@ApiIgnore User currentUser, @RequestParam(value="postingType",  required = false) PostingType type, @RequestParam(required = false) Long sinceId, @RequestParam(required = false) Long maxId, @RequestParam(required = false, defaultValue = "10") int count) {
        return cb.run(() -> {
            List<PostingDto> postingDtoList = postingService.findLikeListByUser(currentUser.getId(), type, sinceId, maxId, count);
            return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), postingDtoList);
        }, throwable -> {
            log.error("UserController.doFindLikeList", throwable);
            return new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), null, FlabookGlobalException.Messages.PLEASE_WAIT);
        });
    }

    @JsonView(Views.BaseView.class)
    @GetMapping(path = "/me/adopts")
    public CommonResponse<List<PostingDto>>  doFindAdoptedList(@ApiIgnore User currentUser, @RequestParam(value="postingType",  required = false) PostingType type, @RequestParam(required = false) Long sinceId, @RequestParam(required = false) Long maxId, @RequestParam(required = false, defaultValue = "10") int count, @RequestParam(name = "q", required = false) String query) {
        return cb.run(() -> {
            List<PostingDto> postingDtoList;
            if(org.apache.commons.lang3.StringUtils.isBlank(query)) {
                postingDtoList = postingService.findAdopedListByUser(currentUser.getId(), type, sinceId, maxId, count);
            } else {
                postingDtoList = adoptCommentService.findAdopedPostingListByUser(currentUser.getId(), sinceId, maxId, count, query).stream().map(PostingDto::of).collect(Collectors.toList());
            }

            return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), postingDtoList);
        }, throwable -> {
            log.error("UserController.doFindAdoptedList", throwable);
            return new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), null, FlabookGlobalException.Messages.PLEASE_WAIT);
        });
    }

    @JsonView(Views.PostingLikeOnlyJsonView.class)
    @GetMapping(path = "/me/postings")
    public CommonResponse<List<PostingDto>>  doFindMyPostingList(@ApiIgnore User currentUser, @RequestParam(value="postingType",  required = false) PostingType type, @RequestParam(required = false) Long sinceId, @RequestParam(required = false) Long maxId, @RequestParam(required = false, defaultValue = "10") int count, @RequestParam(name="q", required = false) String query) {
        return cb.run(() -> {
            List<PostingDto> postingDtoList = postingService.searchUserPostings(currentUser.getId(), type, sinceId, maxId, count, (StringUtils.isNotBlank(query)) ? Optional.of(query) : Optional.empty());
            return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), postingDtoList);
        }, throwable -> {
            log.error("UserController.doFindMyPostingList", throwable);
            return new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), null, FlabookGlobalException.Messages.PLEASE_WAIT);
        });
    }

    @JsonView(Views.PostingLikeOnlyJsonView.class)
    @GetMapping(path = "/me/comments")
    public CommonResponse<List<PostingDto>>  doFindMyCommentPostingList(@ApiIgnore User currentUser, @RequestParam(value="postingType",  required = false) PostingType type, @RequestParam(required = false) Long sinceId, @RequestParam(required = false) Long maxId, @RequestParam(required = false, defaultValue = "10") int count, @RequestParam(name="q", required = false) String query) {
        return cb.run(() -> {
            List<PostingDto> postingDtoList = postingService.searchUserComments(currentUser.getId(), type, sinceId, maxId, count, (StringUtils.isNotBlank(query)) ? Optional.of(query) : Optional.empty());
            return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), postingDtoList);
        }, throwable -> {
            log.error("UserController.doFindMyCommentPostingList", throwable);
            return new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), null, FlabookGlobalException.Messages.PLEASE_WAIT);
        });
    }

    @JsonView(Views.PostingLikeOnlyJsonView.class)
    @GetMapping(path = "/{id}/postings")
    public CommonResponse<List<PostingDto>>  doFindUserPostingList(@ApiIgnore User currentUser, @RequestParam(value="postingType",  required = false) PostingType type, @PathVariable Long id, @RequestParam(required = false) Long sinceId, @RequestParam(required = false) Long maxId, @RequestParam(required = false, defaultValue = "10") int count, @RequestParam(name="q", required = false) String query) {
        return cb.run(() -> {
            List<PostingDto> postingDtoList = postingService.searchUserPostings(id, type, sinceId, maxId, count, (StringUtils.isNotBlank(query)) ? Optional.of(query) : Optional.empty());
            return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), postingDtoList);
        }, throwable -> {
            log.error("UserController.doFindUserPostingList", throwable);
            return new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), null, FlabookGlobalException.Messages.PLEASE_WAIT);
        });
    }

    @JsonView(Views.PostingLikeOnlyJsonView.class)
    @GetMapping(path = "/{id}/comments")
    public CommonResponse<List<PostingDto>>  doFindUserCommentPostingList(@ApiIgnore User currentUser, @RequestParam(value="postingType",  required = false) PostingType type, @PathVariable Long id, @RequestParam(required = false) Long sinceId, @RequestParam(required = false) Long maxId, @RequestParam(required = false, defaultValue = "10") int count, @RequestParam(name="q", required = false) String query) {
        return cb.run(() -> {
            List<PostingDto> postingDtoList = postingService.searchUserComments(id, type, sinceId, maxId, count, (StringUtils.isNotBlank(query)) ? Optional.of(query) : Optional.empty());
            return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), postingDtoList);
        }, throwable -> {
            log.error("UserController.doFindUserCommentPostingList", throwable);
            return new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), null, FlabookGlobalException.Messages.PLEASE_WAIT);
        });
    }

    @JsonView(Views.BaseView.class)
    @GetMapping(path = "/{id}/adopts")
    public CommonResponse<List<PostingDto>>  doFindUserAdoptedList(@ApiIgnore User currentUser, @PathVariable Long id,@RequestParam(value="postingType",  required = false) PostingType type, @RequestParam(required = false) Long sinceId, @RequestParam(required = false) Long maxId, @RequestParam(required = false, defaultValue = "10") int count, @RequestParam(name = "q", required = false) String query) {
        return cb.run(() -> {
            List<PostingDto> postingDtoList;
            if(org.apache.commons.lang3.StringUtils.isBlank(query)) {
                postingDtoList = postingService.findAdopedListByUser(id, type, sinceId, maxId, count);
            } else {
                postingDtoList = adoptCommentService.findAdopedPostingListByUser(id, sinceId, maxId, count, query).stream().map(PostingDto::of).collect(Collectors.toList());
            }
            return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), postingDtoList);
        }, throwable -> {
            log.error("UserController.doFindUserAdoptedList", throwable);
            return new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), null, FlabookGlobalException.Messages.PLEASE_WAIT);
        });
    }

    @PostMapping(path = "/me/avatar")
    @ApiOperation(value = "프로필관리 사진 변경")
    public CommonResponse<UserDto> doUpdateMyProfileAvatar(@ApiIgnore User currentUser, @Valid UpdateProfileAvatarVo vo) {
        try {
            return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), userService.updateUserProfileAvatar(currentUser, vo));
        } catch (IOException e) {
            e.printStackTrace();
            return new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), null, e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
            return new CommonResponse<>(CommonResponseCode.FAIL.getResultCode(), null, e.getMessage());
        }
    }

    @DeleteMapping(path = "/me/avatar")
    @ApiOperation(value = "프로필관리 사진 삭제")
    public CommonResponse<UserDto> doDeleteMyProfileAvatar(@ApiIgnore User currentUser) {
        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), userService.deleteUserProfileAvatar(currentUser));
    }

    @PostMapping(path = "/me/name")
    @ApiOperation(value = "프로필관리 닉네임 변경")
    public CommonResponse<UserDto> doUpdateMyProfileName(@ApiIgnore User currentUser, @Valid UseableVo vo) {
        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), userService.updateUserProfileName(currentUser, vo));
    }

    @PostMapping(path = "/me/password")
    @ApiOperation(value = "프로필관리 비밀번호 변경")
    public CommonResponse<UserDto> doUpdateMyProfilePassword(@ApiIgnore User currentUser, @Valid PasswordVo vo) {
        return new CommonResponse<>(CommonResponseCode.SUCCESS.getResultCode(), userService.updateUserProfilePassword(currentUser, vo));
    }

    @DeleteMapping(path = "/me")
    @ApiOperation(value = "회원 탈퇴")
    public CommonResponse<Boolean> doDeleteMyProfile(@ApiIgnore User currentUser, @Valid DeleteReasonVo reason) {
        return new CommonResponse<>(CommonResponseCode.SUCCESS, userService.deleteMyProfile(currentUser, reason));
    }

	@PostMapping(path = "/me/phone")
	@ApiOperation(value = "프로필관리 - 개인정보 - 휴대폰 번호 등록")
	public CommonResponse<UserDto> doUpdateMyProfilePhone(@ApiIgnore User currentUser, @Valid @RequestBody MyProfilePhoneVo vo) {
		return new CommonResponse<>(CommonResponseCode.SUCCESS, userService.updateMyProfilePhone(currentUser, vo));
	}

	@PostMapping(path = "/me/auth/phone")
	@ApiOperation(value = "프로필관리 - 개인정보 - 휴대폰 번호 등록 - 인증번호 발송")
	public CommonResponse<Boolean> doSendPhoneAuthKey(@ApiIgnore User currentUser, @Valid @RequestBody PhoneAuthKeyVo vo) throws Exception {
		return new CommonResponse<>(CommonResponseCode.SUCCESS, userService.sendPhoneAuthKey(currentUser, vo));
	}

	@PostMapping(path = "/me/mail")
	@ApiOperation(value = "프로필관리 - 개인정보 - 이메일 등록")
	public CommonResponse<UserDto> doUpdateMyProfileEmail(@ApiIgnore User currentUser, @Valid @RequestBody MyProfileEmailVo vo) {
		return new CommonResponse<>(CommonResponseCode.SUCCESS, userService.updateMyProfileEmail(currentUser, vo));
	}

	@PostMapping(path = "/me/auth/mail")
	@ApiOperation(value = "프로필관리 - 개인정보 - 이메일 등록 - 인증번호 발송")
	public CommonResponse<Boolean> doSendEmailAuthKey(@ApiIgnore User currentUser, @Valid @RequestBody EmailAuthKeyVo vo) {

		String authKey = userService.getEmailAuthKey(currentUser, vo);
		if(authKey == null) {
			throw new CommonException(CommonResponseCode.USER_EMAIL_LATE_FAILL, vo.getEmail());
		}

		//인증 메일 발송
		CreateMailVo createMailVo = CreateMailVo.builder()
				.nickName(currentUser.getNickname())
				.authKey(authKey)
				.email(vo.getEmail())
				.type(MailType.modifyProfile)
				.build();

		sendMailAsyncService.sendMail(createMailVo);

		return new CommonResponse<>(CommonResponseCode.SUCCESS, true);
	}

    @JsonView(Views.PostingLikeOnlyJsonView.class)
    @GetMapping(path = "/index")
    public String doIndex(@ApiIgnore User currentUser) {
        userService.index();
        return "index";
    }


}
