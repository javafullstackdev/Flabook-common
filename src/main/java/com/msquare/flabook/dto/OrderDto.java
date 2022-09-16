package com.msquare.flabook.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import com.msquare.flabook.models.INotification;
import com.msquare.flabook.models.ImageResource;
import com.msquare.flabook.models.Resource;
import com.msquare.flabook.models.User;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto implements INotification {

    private static final String RESOURCE_ID = "order";

    private static final Resource resource = new Resource(RESOURCE_ID
            , Resource.ResourceType.shop
            , RESOURCE_ID
            , Resource.ResourceType.shop);

    private String id;
    private String text;
    private String thumbnail;
    private User owner;

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public User getOwner() {
        return owner;
    }

    @Override
    public ImageResource getThumbnail() {
        return new ImageResource(this.thumbnail, "thumbnail");
    }

    @Override
    public Resource asResource() {
        return resource;
    }
}
