package com.github.saphyra.apphub.service.admin_panel.error_report.service.overview;

import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportEntity;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
@Builder
@Getter
public class ErrorReportOverviewSpecification implements Specification<ErrorReportEntity> {
    private final String message;
    private final Integer statusCode;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final ErrorReportStatus status;

    @Override
    public Predicate toPredicate(Root<ErrorReportEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (!isNull(message)) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("message")), "%" + message.toLowerCase() + "%"));
        }

        if (!isNull(statusCode)) {
            predicates.add(criteriaBuilder.equal(root.get("responseStatus"), statusCode));
        }

        if (!isNull(startTime)) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startTime));
        }

        if (!isNull(endTime)) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endTime));
        }

        if (!isNull(status)) {
            predicates.add(criteriaBuilder.equal(root.get("status"), status.name()));
        }

        Predicate[] predicateArray = new Predicate[predicates.size()];
        return criteriaBuilder.and(predicates.toArray(predicateArray));
    }
}
