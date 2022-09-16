package com.msquare.flabook.models;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import com.msquare.flabook.json.Views;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Embeddable
@Getter
@JsonView(Views.BaseView.class)
public class Resource implements Serializable {

    @Getter
    public enum ResourceType {
        notice  //0
        , comment // 1
        , reply //2
        , likeposting //3
        , likecomment //4
        , advertisement //5
        , shop //6
        , boast(true) //7
        , clinic(true)  //8
        , free(true) //9
        , guidebook(true) //10
        , magazine(true) //11
        , question(true) //12
        , dictionary(false) //13
        , web(false) //14
        , badge //15
        , profile //16 사용자 프로필
        , television(true)// 17 모야모TV
        , photo(true) //18 모야모포토
        , photoalbum //19 포토 앨범
        ;

        boolean isBoardType = false;
        ResourceType(boolean isBoardType) {
            this.isBoardType = isBoardType;
        }

        ResourceType() {

        }
    }

    /**
     * 개별 리소스 ID
     */
    @NonNull
    @Column(length = 128)
    private String resourceId;

    /**
     * 컨텐츠 Type
     */
    @Column(columnDefinition = "tinyint")
    @NonNull
    private ResourceType resourceType;

    /**
     * 연관 포스팅/상점 ... ID
     */
    @Column(length = 128)
    private String referenceId;

    @Column(columnDefinition = "tinyint")
    private ResourceType referenceType;

    public Resource(@NonNull Long resourceId, @NonNull ResourceType resourceType, Long referenceId, @NonNull ResourceType referenceType) {
        this.resourceId = String.valueOf(resourceId);
        this.resourceType = resourceType;
        this.referenceId = String.valueOf(referenceId);
        this.referenceType = referenceType;
    }

    public Resource(@NonNull Long resourceId, @NonNull ResourceType resourceType) {
        this.resourceId = String.valueOf(resourceId);
        this.resourceType = resourceType;
    }


}
