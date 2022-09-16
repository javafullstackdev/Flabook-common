package com.msquare.flabook.models;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticePolicy extends BaseEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@ApiModelProperty(value = "약관 제목")
	private String title;

	@ColumnDefault("0")
	@ApiModelProperty(value = "약관 순서")
	private int position;

	@ApiModelProperty(value = "약관 내용")
	@Column(columnDefinition = "text", length=4096)
	private String description;

	@Column(columnDefinition="boolean default false")
    private Boolean isDelete = false;
}
