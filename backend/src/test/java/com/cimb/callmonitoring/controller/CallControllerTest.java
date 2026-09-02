package com.cimb.callmonitoring.controller;

import com.cimb.callmonitoring.dto.PageResponse;
import com.cimb.callmonitoring.entity.CallRecord;
import com.cimb.callmonitoring.repository.CallRecordRepository;
import com.cimb.callmonitoring.specification.CallRecordSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CallControllerTest {

    private CallRecordRepository repository;
    private CallController controller;

    @BeforeEach
    void setUp() {
        repository = mock(CallRecordRepository.class);
        controller = new CallController(repository);
    }

    private CallRecord callRecord() {
        CallRecord record = new CallRecord();
        record.setId(1L);
        record.setCallId(UUID.randomUUID());
        record.setCallTimestamp(LocalDateTime.now());
        record.setCsName("Siti Aminah");
        record.setCustomerName("Budi Santoso");
        record.setSentimentScore((short) 85);
        return record;
    }

    @Test
    void getCallsReturnsContent() {
        Page<CallRecord> page = new PageImpl<>(List.of(callRecord()),
                org.springframework.data.domain.PageRequest.of(0, 5), 1);
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        PageResponse<CallRecord> result = controller.getCalls(
                "siti", null, null, null, "callTimestamp", "desc", 0, 5);

        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals("Siti Aminah", result.content().get(0).getCsName());
        assertEquals(0, result.page());
        assertEquals(5, result.size());
        verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getCallsAppliesFiltersAndSort() {
        Page<CallRecord> page = new PageImpl<>(List.of(),
                org.springframework.data.domain.PageRequest.of(2, 5), 0);
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        PageResponse<CallRecord> result = controller.getCalls(
                "budi",
                java.time.LocalDate.of(2026, 7, 1),
                java.time.LocalDate.of(2026, 8, 31),
                "below70",
                "sentimentScore",
                "asc",
                2,
                5);

        assertEquals(2, result.page());
        assertEquals(5, result.size());
        verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void invalidSentimentFilterIsIgnored() {
        Page<CallRecord> page = new PageImpl<>(List.of());
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        PageResponse<CallRecord> result = controller.getCalls(
                null, null, null, "bogus", "callTimestamp", "desc", 0, 5);

        assertNotNull(result);
    }

    @Test
    void dateRangeOutsideLastThreeMonthsIsRejected() {
        Page<CallRecord> page = new PageImpl<>(List.of());
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        assertThrows(IllegalArgumentException.class, () -> controller.getCalls(
                null,
                java.time.LocalDate.of(2026, 1, 1),
                null,
                null, "callTimestamp", "desc", 0, 5));

        assertThrows(IllegalArgumentException.class, () -> controller.getCalls(
                null,
                null,
                java.time.LocalDate.now().plusDays(1),
                null, "callTimestamp", "desc", 0, 5));

        verify(repository, never()).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void endDateBeforeStartDateIsRejected() {
        Page<CallRecord> page = new PageImpl<>(List.of());
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        assertThrows(IllegalArgumentException.class, () -> controller.getCalls(
                null,
                java.time.LocalDate.of(2026, 8, 31),
                java.time.LocalDate.of(2026, 8, 1),
                null, "callTimestamp", "desc", 0, 5));

        verify(repository, never()).findAll(any(Specification.class), any(Pageable.class));
    }
}