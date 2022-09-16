package com.msquare.flabook.form;

import lombok.Data;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

@Data
@Validated
public class UpdateProfileAvatarVo {

    private MultipartFile avatar;
}
