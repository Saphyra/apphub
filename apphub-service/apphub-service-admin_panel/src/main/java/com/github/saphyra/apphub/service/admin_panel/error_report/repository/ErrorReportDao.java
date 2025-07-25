package com.github.saphyra.apphub.service.admin_panel.error_report.repository;

import com.github.saphyra.apphub.lib.common_util.dao.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@Slf4j
public class ErrorReportDao extends AbstractDao<ErrorReportEntity, ErrorReportDto, String, ErrorReportRepository> {
    private final UuidConverter uuidConverter;

    public ErrorReportDao(ErrorReportConverter converter, ErrorReportRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public List<ErrorReportDto> getOverview(Specification<ErrorReportEntity> spec, Pageable pageable) {
        return converter.convertEntity(repository.findAll(spec, pageable).toList());
    }

    public Optional<ErrorReportDto> findById(UUID id) {
        return findById(uuidConverter.convertDomain(id));
    }

    public void deleteById(UUID id) {
        deleteById(uuidConverter.convertDomain(id));
    }

    public List<ErrorReportDto> findAllById(List<UUID> ids) {
        return StreamSupport.stream(repository.findAllById(uuidConverter.convertDomain(ids)).spliterator(), false)
            .map(converter::convertEntity)
            .collect(Collectors.toList());
    }

    public void deleteByStatus(ErrorReportStatus status) {
        repository.deleteByStatus(status.name());
    }

    @Transactional
    public void deleteAllExceptStatus(List<ErrorReportStatus> statuses){
        repository.deleteByStatusNotIn(statuses.stream().map(Enum::name).toList());
    }
}
