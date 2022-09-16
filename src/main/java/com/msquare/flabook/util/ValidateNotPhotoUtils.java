package com.msquare.flabook.util;

import com.msquare.flabook.enumeration.PostingType;
import com.msquare.flabook.exception.FlabookPermissionException;
import com.msquare.flabook.models.Posting;

public class ValidateNotPhotoUtils {

    private ValidateNotPhotoUtils() throws IllegalAccessException{
        throw new IllegalAccessException("ValidateNotPhotoUtils is static");
    }

    public static void validateNotPhoto(Posting posting) {
        if(posting.getPostingType().equals(PostingType.photo)) {
            throw new FlabookPermissionException(FlabookPermissionException.Messages.NOT_AUTHORIZED);
        }
    }
}
