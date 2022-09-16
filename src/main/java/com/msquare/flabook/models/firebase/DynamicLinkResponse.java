package com.msquare.flabook.models.firebase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicLinkResponse {
    private String shortLink;
    private String previewLink;
}
