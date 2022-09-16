package com.msquare.flabook.models.firebase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DynamicLinkRequest {
    private String domainUriPrefix;
    private String link;
    private DynamicLinkRequest.AndroidInfo androidInfo;
    private DynamicLinkRequest.IosInfo iosInfo;
    private DynamicLinkRequest.SocialMetaTagInfo socialMetaTagInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class SocialMetaTagInfo {
        private String socialTitle;
        private String socialDescription;
        private String socialImageLink;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class IosInfo {
        private String iosBundleId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class AndroidInfo {
        private String androidPackageName;
    }
}
