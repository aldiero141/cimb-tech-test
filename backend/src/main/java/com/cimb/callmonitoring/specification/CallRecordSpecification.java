package com.cimb.callmonitoring.specification;

import com.cimb.callmonitoring.entity.CallRecord;
import com.cimb.callmonitoring.model.SentimentFilter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public final class CallRecordSpecification {

    private CallRecordSpecification() {
    }

    public static Specification<CallRecord> withFilters(String q,
                                                        LocalDate startDate,
                                                        LocalDate endDate,
                                                        SentimentFilter sentiment) {
        Specification<CallRecord> spec = Specification.where(null);

        if (StringUtils.hasText(q)) {
            String pattern = "%" + q.trim().toLowerCase() + "%";
            spec = spec.and(searchByCallIdAndCsName(pattern));
        }

        if (startDate != null) {
            LocalDateTime start = startDate.atStartOfDay();
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("callTimestamp"), start));
        }

        if (endDate != null) {
            LocalDateTime end = endDate.atTime(LocalTime.MAX);
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("callTimestamp"), end));
        }

        if (sentiment != null) {
            switch (sentiment) {
                case BELOW_70 ->
                        spec = spec.and((root, query, cb) ->
                                cb.lessThan(root.<Short>get("sentimentScore"), (short) 70));
                case AT_OR_ABOVE_70 ->
                        spec = spec.and((root, query, cb) ->
                                cb.greaterThanOrEqualTo(root.<Short>get("sentimentScore"), (short) 70));
            }
        }

        return spec;
    }

    private static Specification<CallRecord> searchByCallIdAndCsName(String pattern) {
        return (root, query, cb) -> {
            var callIdLike = cb.like(cb.lower(cb.concat(root.get("callId").as(String.class), "")), pattern);
            var csNameLike = cb.like(cb.lower(root.get("csName")), pattern);
            return cb.or(callIdLike, csNameLike);
        };
    }
}