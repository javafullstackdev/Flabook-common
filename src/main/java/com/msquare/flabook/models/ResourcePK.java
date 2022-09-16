package com.msquare.flabook.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ResourcePK implements Serializable {

    private String resourceId;

    /**
     * 컨텐츠 Type
     */
    @Column(columnDefinition = "tinyint")
    @NonNull
    private Resource.ResourceType resourceType;

//    @Column(name = "user_id")
    private Long userId;

}
