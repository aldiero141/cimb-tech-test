package com.cimb.callmonitoring.controller;

import com.cimb.callmonitoring.dto.PageResponse;
import com.cimb.callmonitoring.entity.CallRecord;
import com.cimb.callmonitoring.model.SentimentFilter;
import com.cimb.callmonitoring.repository.CallRecordRepository;
import com.cimb.callmonitoring.specification.CallRecordSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api")
public class CallController {

    private final CallRecordRepository repository;

    public CallController(CallRecordRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/calls")
    public PageResponse<CallRecord> getCalls(
            @RequestParam(required = false) String q,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String sentiment,
            @RequestParam(defaultValue = "callTimestamp") String sort,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        validateDateRange(startDate, endDate);

        SentimentFilter sentimentFilter = SentimentFilter.from(sentiment);
        Specification<CallRecord> spec =
                CallRecordSpecification.withFilters(q, startDate, endDate, sentimentFilter);

        String direction = order.equalsIgnoreCase("asc") ? "asc" : "desc";
        Sort sortBy = Sort.by(Sort.Direction.fromString(direction), mapSortField(sort));
        PageRequest pageRequest = PageRequest.of(page, size, sortBy);

        return PageResponse.of(repository.findAll(spec, pageRequest));
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDate today = LocalDate.now();
        LocalDate oldestAllowed = today.minusMonths(3);
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("endDate must be on or after startDate");
        }
        if (startDate != null && (startDate.isBefore(oldestAllowed) || startDate.isAfter(today))) {
            throw new IllegalArgumentException("startDate must be within the last 3 months");
        }
        if (endDate != null && (endDate.isBefore(oldestAllowed) || endDate.isAfter(today))) {
            throw new IllegalArgumentException("endDate must be within the last 3 months");
        }
    }

    private String mapSortField(String sort) {
        return switch (sort) {
            case "callId" -> "callId";
            case "callTimestamp" -> "callTimestamp";
            case "csName" -> "csName";
            case "customerName" -> "customerName";
            case "sentimentScore" -> "sentimentScore";
            default -> "callTimestamp";
        };
    }
}