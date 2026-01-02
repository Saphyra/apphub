package com.github.saphyra.apphub.service.admin_panel.error_report.repository;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ErrorReportDaoTest {
    private static final UUID ID = UUID.randomUUID();
    private static final String ID_STRING = "id";
    private static final Long COUNT = 32L;

    @Mock
    private ErrorReportConverter converter;

    @Mock
    private ErrorReportRepository repository;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private ErrorReportDao underTest;

    @Mock
    private ErrorReportDto domain;

    @Mock
    private ErrorReportEntity entity;

    @Mock
    private Page<ErrorReportEntity> page;

    @Mock
    private Specification<ErrorReportEntity> specification;

    @Mock
    private Pageable pageable;

    @Test
    public void getOverview() {
        given(repository.findAll(specification, pageable)).willReturn(page);
        given(page.toList()).willReturn(Arrays.asList(entity));
        given(converter.convertEntity(Arrays.asList(entity))).willReturn(Arrays.asList(domain));

        List<ErrorReportDto> result = underTest.getOverview(specification, pageable);

        assertThat(result).containsExactly(domain);
    }

    @Test
    public void findById() {
        given(uuidConverter.convertDomain(ID)).willReturn(ID_STRING);
        given(repository.findById(ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        Optional<ErrorReportDto> result = underTest.findById(ID);

        assertThat(result).contains(domain);
    }

    @Test
    public void deleteById() {
        given(uuidConverter.convertDomain(ID)).willReturn(ID_STRING);
        given(repository.existsById(ID_STRING)).willReturn(true);

        underTest.deleteById(ID);

        verify(repository).deleteById(ID_STRING);
    }

    @Test
    public void findAllById() {
        given(uuidConverter.convertDomain(Arrays.asList(ID))).willReturn(Arrays.asList(ID_STRING));
        given(repository.findAllById(Arrays.asList(ID_STRING))).willReturn(Arrays.asList(entity));
        given(converter.convertEntity(entity)).willReturn(domain);

        List<ErrorReportDto> result = underTest.findAllById(Arrays.asList(ID));

        assertThat(result).containsExactly(domain);
    }

    @Test
    public void deleteByStatus() {
        underTest.deleteByStatus(ErrorReportStatus.READ);

        verify(repository).deleteByStatus(ErrorReportStatus.READ.name());
    }

    @Test
    void deleteAllExceptStatus() {
        underTest.deleteAllExceptStatus(List.of(ErrorReportStatus.UNREAD));

        then(repository).should().deleteByStatusNotIn(List.of(ErrorReportStatus.UNREAD.name()));
    }

    @Test
    void count() {
        given(repository.count()).willReturn(COUNT);

        assertThat(underTest.count()).isEqualTo(COUNT);
    }
}