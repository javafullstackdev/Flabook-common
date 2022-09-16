package com.msquare.flabook.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.models.Resource;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonView(Views.BaseView.class)
public class ResourceDto implements Serializable {
    private String resourceId;
    private Resource.ResourceType resourceType;
    private String referenceId;
    private Resource.ResourceType referenceType;
}
