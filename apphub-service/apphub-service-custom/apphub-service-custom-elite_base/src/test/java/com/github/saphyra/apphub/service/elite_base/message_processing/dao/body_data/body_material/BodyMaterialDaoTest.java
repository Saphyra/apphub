package com.github.saphyra.apphub.service.elite_base.message_processing.dao.body_data.body_material;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BodyMaterialDaoTest {
    private static final UUID BODY_ID = UUID.randomUUID();
    private static final String BODY_ID_STRING = "body-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private BodyMaterialConverter converter;

    @Mock
    private BodyMaterialRepository repository;

    @InjectMocks
    private BodyMaterialDao underTest;

    @Mock
    private BodyMaterial domain;

    @Mock
    private BodyMaterialEntity entity;

    @Test
    void getByBodyId() {
        given(uuidConverter.convertDomain(BODY_ID)).willReturn(BODY_ID_STRING);
        given(repository.getByBodyId(BODY_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        assertThat(underTest.getByBodyId(BODY_ID)).containsExactly(domain);
    }
}