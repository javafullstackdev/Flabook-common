package com.msquare.flabook.models.board;

public class BoardDiscriminatorValues {

    private BoardDiscriminatorValues() throws IllegalAccessException {
        throw new IllegalAccessException("BoardDiscriminatorValues is util");
    }

    public static final String QUESTION = "QU";
    public static final String FREE = "FR";
    public static final String FREE_WAIT = "WF";

    public static final String BOAST = "BO";
    public static final String BOAST_WAIT = "WB";

    public static final String CLINIC = "CL";
    public static final String GUIDE = "GU";
    public static final String MAGAZINE = "MZ";
    public static final String MAGAZINE_WAIT = "WM";

    public static final String TELEVISION = "TV";
    public static final String TELEVISION_WAIT = "WT";

    public static final String PHOTO = "PH";
}
