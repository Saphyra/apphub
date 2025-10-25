package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.custom.elite_base.common.EliteBaseProperties;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import com.google.common.cache.Cache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StarSystemDaoTest {
    private static final String STAR_NAME = "star-name";
    private static final Integer PAGE_SIZE = 32;
    private static final UUID ID = UUID.randomUUID();
    private static final String ID_STRING = "id";
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private StarSystemConverter converter;

    @Mock
    private StarSystemRepository repository;

    @Mock
    private EliteBaseProperties properties;

    @Mock
    private Cache<UUID, StarSystem> readCache;

    @Mock
    private StarSystemWriteBuffer writeBuffer;

    @Mock
    private StarSystemDeleteBuffer deleteBuffer;

    @InjectMocks
    private StarSystemDao underTest;

    @Mock
    private StarSystem domain1;

    @Mock
    private StarSystem domain2;

    @Mock
    private StarSystemEntity entity;

    @Test
    void findByStarName_fromRepository() {
        given(repository.findByStarName(STAR_NAME)).willReturn(Optional.of(entity));
        given(converter.convertEntity(entity)).willReturn(domain1);
        given(readCache.asMap()).willReturn(new ConcurrentHashMap<>());

        assertThat(underTest.findByStarName(STAR_NAME)).contains(domain1);
    }

    @Test
    void findByStarName_fromReadCache() {
        ConcurrentHashMap<UUID, StarSystem> map = new ConcurrentHashMap<>();
        map.put(ID, domain1);
        given(domain1.getStarName()).willReturn(STAR_NAME);
        given(readCache.asMap()).willReturn(map);

        assertThat(underTest.findByStarName(STAR_NAME)).contains(domain1);
    }

    @Test
    void findByStarName_mergerPicksLatest() {
        ConcurrentHashMap<UUID, StarSystem> map = new ConcurrentHashMap<>();
        map.put(ID, domain1);
        given(domain1.getStarName()).willReturn(STAR_NAME);
        given(readCache.asMap()).willReturn(map);

        given(domain1.getId()).willReturn(ID);
        given(domain2.getId()).willReturn(ID);

        given(domain1.getLastUpdate()).willReturn(CURRENT_TIME);
        given(domain2.getLastUpdate()).willReturn(CURRENT_TIME.plusSeconds(1));

        given(writeBuffer.search(any())).willReturn(List.of(domain2));

        assertThat(underTest.findByStarName(STAR_NAME)).contains(domain2);
    }

    @Test
    void getByStarNameLike() {
        given(repository.getByStarNameIgnoreCaseContaining(STAR_NAME)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain1));

        assertThat(underTest.getByStarNameLike(STAR_NAME)).containsExactly(domain1);
    }

    @Test
    void findByIdValidated_notFound() {
        given(uuidConverter.convertDomain(ID)).willReturn(ID_STRING);
        given(repository.findById(ID_STRING)).willReturn(Optional.empty());

        ExceptionValidator.validateNotLoggedException(() -> underTest.findByIdValidated(ID), HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    void findByIdValidated() {
        given(uuidConverter.convertDomain(ID)).willReturn(ID_STRING);
        given(repository.findById(ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain1));

        assertThat(underTest.findByIdValidated(ID)).isEqualTo(domain1);
    }

    @Test
    void findAllById() {
        given(uuidConverter.convertDomain(List.of(ID))).willReturn(List.of(ID_STRING));
        given(repository.findAllById(List.of(ID_STRING))).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain1));

        assertThat(underTest.findAllById(List.of(ID))).containsExactly(domain1);
    }
}