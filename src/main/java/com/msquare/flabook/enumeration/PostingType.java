package com.msquare.flabook.enumeration;

import javassist.NotFoundException;
import lombok.Getter;
import com.msquare.flabook.models.Posting;
import com.msquare.flabook.models.board.*;

@SuppressWarnings("java:S115")
@Getter
public enum PostingType {


    question("이름이뭐야", Question.class, false, false, BoardDiscriminatorValues.QUESTION, null),

    boast("자랑하기", Boast.class, true, true, BoardDiscriminatorValues.BOAST, null),

    clinic("식물클리닉", Clinic.class, false, true, BoardDiscriminatorValues.CLINIC, null),

    guidebook("가이드북", Guide.class, true, false, BoardDiscriminatorValues.GUIDE, null),

    free("자유수다", Free.class, true, false, BoardDiscriminatorValues.FREE, null),

    magazine("매거진", Magazine.class, true, true, BoardDiscriminatorValues.MAGAZINE, null),

	magazine_wait("매거진", MagazineWait.class, true, true, BoardDiscriminatorValues.MAGAZINE_WAIT, magazine),

    television("모야모TV", Television.class, false, true, BoardDiscriminatorValues.TELEVISION, null),

    television_wait("모야모TV", TelevisionWait.class, false, true, BoardDiscriminatorValues.TELEVISION_WAIT, television),

    photo("포토", Photo.class, true, false, BoardDiscriminatorValues.PHOTO, null ),

    boast_wait("자랑하기", BoastWait.class, true, true, BoardDiscriminatorValues.BOAST_WAIT, boast),

    free_wait("자유수다", FreeWait.class, true, false, BoardDiscriminatorValues.FREE_WAIT, free);

    private final String name;
    private final Class<? extends Posting> clazz;
    private final boolean allowDescription;
    private final boolean allowTitle;
    private final String discriminatorValue;
    private final PostingType targetPostingType;

    PostingType(String name, Class<? extends Posting> clazz, boolean allowDescription, boolean allowTitle, String discriminatorValue, PostingType targetPostingType) {
        this.name = name;
        this.clazz = clazz;
        this.allowTitle = allowTitle;
        this.allowDescription = allowDescription;
        this.discriminatorValue= discriminatorValue;
        this.targetPostingType= targetPostingType;
    }

    public static PostingType findByEntityClazz(Class<? extends Posting> clazz) throws NotFoundException {
        for (PostingType postingType : values()) {
            if (clazz == postingType.clazz)
                return postingType;
        }

        throw new NotFoundException("찾을 수 없는 Entity");
    }

    public static PostingType orElse(PostingType postingType, PostingType orElse) {
        if(postingType == null) {
            return orElse;
        }
        return postingType;
    }




}
