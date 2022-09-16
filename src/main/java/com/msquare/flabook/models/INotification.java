package com.msquare.flabook.models;


import com.msquare.flabook.enumeration.OsType;

public interface INotification {
	default String getTitle() {
		return null;
	}

    String getText();

    User getOwner();

    ImageResource getThumbnail();

    Resource asResource();

    default OsType getOsType() {
        return OsType.all;
    }

}
