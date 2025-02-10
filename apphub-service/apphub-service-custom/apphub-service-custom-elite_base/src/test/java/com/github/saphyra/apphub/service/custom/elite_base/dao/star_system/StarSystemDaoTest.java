package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.custom.elite_base.common.EliteBaseProperties;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StarSystemDaoTest {
    private static final Long STAR_ID = 324L;
    private static final String STAR_NAME = "star-name";
    private static final Integer PAGE_SIZE = 32;
    private static final UUID ID = UUID.randomUUID();
    private static final String IS_STRING = "id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private StarSystemConverter converter;

    @Mock
    private StarSystemRepository repository;

    @Mock
    private EliteBaseProperties properties;

    @InjectMocks
    private StarSystemDao underTest;

    @Mock
    private StarSystem domain;

    @Mock
    private StarSystemEntity entity;

    @Test
    void findByStarId() {
        given(repository.findByStarId(STAR_ID)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        assertThat(underTest.findByStarId(STAR_ID)).contains(domain);
    }

    @Test
    void findByStarName() {
        given(repository.findByStarName(STAR_NAME)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        assertThat(underTest.findByStarName(STAR_NAME)).contains(domain);
    }

    @Test
    void getByStarNameLike() {
        given(properties.getStarSystemSuggestionListSize()).willReturn(PAGE_SIZE);
        given(repository.getByStarNameIgnoreCaseContaining(STAR_NAME, PageRequest.of(0, PAGE_SIZE))).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        assertThat(underTest.getByStarNameLike(STAR_NAME)).containsExactly(domain);
    }

    @Test
    void findByIdValidated_notFound() {
        given(uuidConverter.convertDomain(ID)).willReturn(IS_STRING);
        given(repository.findById(IS_STRING)).willReturn(Optional.empty());

        ExceptionValidator.validateNotLoggedException(() -> underTest.findByIdValidated(ID), HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    void findByIdValidated() {
        given(uuidConverter.convertDomain(ID)).willReturn(IS_STRING);
        given(repository.findById(IS_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        assertThat(underTest.findByIdValidated(ID)).isEqualTo(domain);
    }
}