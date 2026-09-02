package com.cimb.callmonitoring.specification;

import com.cimb.callmonitoring.entity.CallRecord;
import com.cimb.callmonitoring.model.SentimentFilter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CallRecordSpecificationTest {

    @SuppressWarnings("unchecked")
    private Root<CallRecord> mockRoot() {
        Root<CallRecord> root = (Root<CallRecord>) mock(Root.class);

        Path<Object> callId = (Path<Object>) mock(Path.class);
        Path<String> callIdText = (Path<String>) mock(Path.class);
        when(root.get("callId")).thenReturn(callId);
        when(callId.as(String.class)).thenReturn(callIdText);

        Path<Object> csName = (Path<Object>) mock(Path.class);
        Path<String> csNameText = (Path<String>) mock(Path.class);
        when(root.get("csName")).thenReturn(csName);
        when(csName.as(String.class)).thenReturn(csNameText);

        Path<Object> customerName = (Path<Object>) mock(Path.class);
        Path<String> customerNameText = (Path<String>) mock(Path.class);
        when(root.get("customerName")).thenReturn(customerName);
        when(customerName.as(String.class)).thenReturn(customerNameText);

        Path<Object> callTimestamp = (Path<Object>) mock(Path.class);
        Path<String> callTimestampText = (Path<String>) mock(Path.class);
        when(root.get("callTimestamp")).thenReturn(callTimestamp);
        when(callTimestamp.as(String.class)).thenReturn(callTimestampText);

        Path<Object> sentimentScore = (Path<Object>) mock(Path.class);
        Path<String> sentimentScoreText = (Path<String>) mock(Path.class);
        when(root.get("sentimentScore")).thenReturn(sentimentScore);
        when(sentimentScore.as(String.class)).thenReturn(sentimentScoreText);

        return root;
    }

    private CriteriaBuilder mockBuilder() {
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Expression<String> lower = (Expression<String>) mock(Expression.class);
        Predicate like = mock(Predicate.class);
        when(cb.concat(any(Expression.class), anyString())).thenReturn(lower);
        when(cb.lower(any(Expression.class))).thenReturn(lower);
        when(cb.like(any(Expression.class), anyString())).thenReturn(like);
        when(cb.or(any(Predicate[].class))).thenReturn(like);
        when(cb.or(any(Predicate.class), any(Predicate.class))).thenReturn(like);
        when(cb.and(any(Predicate[].class))).thenReturn(like);
        // Disambiguate overloads by casting first arg and typing second arg
        when(cb.greaterThanOrEqualTo(any(Expression.class), any(Comparable.class))).thenReturn(like);
        when(cb.lessThanOrEqualTo(any(Expression.class), any(Comparable.class))).thenReturn(like);
        when(cb.lessThan(any(Expression.class), any(Comparable.class))).thenReturn(like);
        return cb;
    }

    @Test
    void noFiltersProducesNullPredicate() {
        Specification<CallRecord> spec = CallRecordSpecification.withFilters(null, null, null, null);
        Predicate predicate = spec.toPredicate(mockRoot(), mock(CriteriaQuery.class), mockBuilder());
        assertEquals(null, predicate);
    }

    @Test
    void searchKeywordBuildsOrPredicate() {
        Specification<CallRecord> spec = CallRecordSpecification.withFilters("siti", null, null, null);
        CriteriaBuilder cb = mockBuilder();
        assertNotNull(spec.toPredicate(mockRoot(), mock(CriteriaQuery.class), cb));
        verify(cb).or(any(Predicate.class), any(Predicate.class));
    }

    @Test
    void startDateBuildsGreaterThanOrEqualPredicate() {
        Specification<CallRecord> spec = CallRecordSpecification.withFilters(
                null, LocalDate.of(2026, 6, 1), null, null);
        CriteriaBuilder cb = mockBuilder();
        assertNotNull(spec.toPredicate(mockRoot(), mock(CriteriaQuery.class), cb));
        verify(cb).greaterThanOrEqualTo(any(Expression.class), any(Comparable.class));
    }

    @Test
    void endDateBuildsLessThanOrEqualPredicate() {
        Specification<CallRecord> spec = CallRecordSpecification.withFilters(
                null, null, LocalDate.of(2026, 8, 31), null);
        CriteriaBuilder cb = mockBuilder();
        assertNotNull(spec.toPredicate(mockRoot(), mock(CriteriaQuery.class), cb));
        verify(cb).lessThanOrEqualTo(any(Expression.class), any(Comparable.class));
    }

    @Test
    void below70BuildsLessThanOnSentimentScore() {
        Specification<CallRecord> spec = CallRecordSpecification.withFilters(
                null, null, null, SentimentFilter.BELOW_70);
        CriteriaBuilder cb = mockBuilder();
        assertNotNull(spec.toPredicate(mockRoot(), mock(CriteriaQuery.class), cb));
        verify(cb).lessThan(any(Expression.class), any(Comparable.class));
    }

    @Test
    void sentimentFilterParsingMapsKnownValues() {
        assertEquals(SentimentFilter.BELOW_70, SentimentFilter.from("below70"));
        assertEquals(SentimentFilter.AT_OR_ABOVE_70, SentimentFilter.from("above70"));
        assertEquals(null, SentimentFilter.from(null));
        assertEquals(null, SentimentFilter.from("unknown"));
        assertEquals(null, SentimentFilter.from(""));
        assertEquals(null, SentimentFilter.from("BELOW70"));
    }
}