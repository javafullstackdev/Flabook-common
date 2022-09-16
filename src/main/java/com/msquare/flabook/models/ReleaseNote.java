package com.msquare.flabook.models;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import com.msquare.flabook.enumeration.OsType;
import com.msquare.flabook.form.auth.ValidationPattern;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * APP 릴리즈 Entity
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class ReleaseNote extends BaseEntity{

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Column(nullable = false, columnDefinition="boolean default false")
    private Boolean isDelete = false;

	@NotBlank
	@ApiModelProperty(value = "APP 버전")
	@Column(name="version", nullable = false, length = 16)
	private String version;

	@ApiModelProperty(value = "APP OS")
	@Column(name="os_type", nullable = false, length = 16)
	@Enumerated(EnumType.STRING)
	private OsType osType;

	@NotBlank
	@ApiModelProperty(value = "배포 내용")
	@Column(name="description", nullable = false, length = 255)
	private String description;

	@ApiModelProperty(value = "강제 업데이트 여부")
	@Column(name="force_update", nullable = false, columnDefinition="boolean default false")
	private Boolean forceUpdate = false;

	@ApiModelProperty(value = "강제 업데이트 버전")
	@Column(columnDefinition="varchar(20)")
	private String forceUpdateVersion;


	@NotBlank
	@ApiModelProperty(value = "배포 일자")
	@Column(name="release_date", nullable = false)
	@Pattern(message = ValidationPattern.PATTERN_DATE_MESSAGE, regexp = ValidationPattern.PATTERN_DATE_REGEXP)
	private String releaseDate;

}
