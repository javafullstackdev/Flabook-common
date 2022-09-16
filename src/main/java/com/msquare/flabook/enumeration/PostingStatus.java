package com.msquare.flabook.enumeration;

@SuppressWarnings("java:S115")
public enum PostingStatus {


    created(0), answered(-30), unanswered(0), unviewed(-30), hardtoo(30), outdate(-30), ranked(0);

    private final int weight; // 기본값은 10?
    PostingStatus(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return this.weight;
    }
}
