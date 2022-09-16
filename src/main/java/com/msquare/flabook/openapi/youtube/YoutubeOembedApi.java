package com.msquare.flabook.openapi.youtube;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="youtubeOembed", url = "${openapi.youtube.address.host:https://www.youtube.com}")
public interface YoutubeOembedApi {
    @GetMapping(path = "/oembed", produces = "application/json; utf-8")
    EmbedMetaData metaData(@RequestParam(value="v", defaultValue = "2") String v, @RequestParam(value = "format", defaultValue = "json") String format, @RequestParam(value="dnt", defaultValue = "1") int dnt, @RequestParam(value="maxwidth", defaultValue = "1024") int maxWidth, @RequestParam(value = "url") String url);
}
