package com.msquare.flabook.models.board;

public interface WithCondition {
    default ClinicCondition getCondition() {return null;}
}
