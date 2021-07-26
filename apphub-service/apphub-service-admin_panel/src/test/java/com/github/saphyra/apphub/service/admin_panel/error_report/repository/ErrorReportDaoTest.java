package com.github.saphyra.apphub.service.admin_panel.error_report.repository;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ErrorReportDaoTest {
    private static final UUID ID = UUID.randomUUID();
    private static final String ID_STRING = "id";

    @Mock
    private ErrorReportConverter converter;

    @Mock
    private ErrorReportRepository repository;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private ErrorReportDao underTest;

    @Mock
    private ErrorReport domain;

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

        List<ErrorReport> result = underTest.getOverview(specification, pageable);

        assertThat(result).containsExactly(domain);
    }

    @Test
    public void findById() {
        given(uuidConverter.convertDomain(ID)).willReturn(ID_STRING);
        given(repository.findById(ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        Optional<ErrorReport> result = underTest.findById(ID);

        assertThat(result).contains(domain);
    }
}