package com.msquare.flabook.service;

import org.apache.lucene.search.NumericRangeQuery;
import org.hibernate.search.query.dsl.BooleanJunction;

public class TimelineJunction {

    private TimelineJunction() {
        //
    }

    public static void addJunction(BooleanJunction<?> booleanJunction, Long sinceId, Long maxId) {
        if (sinceId != null && sinceId > 0) {
            booleanJunction.must(NumericRangeQuery.newLongRange("id", sinceId, Long.MAX_VALUE, false, true));
        }

        if (maxId != null && maxId > 0) {
            booleanJunction.must(NumericRangeQuery.newLongRange("id", Long.MIN_VALUE, maxId, true, true));
        }
    }
}
