package com.github.saphyra.apphub.service.notebook.dao.dimension;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DimensionFactoryTest {
    private static final UUID DIMENSION_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final Integer INDEX = 245;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private DimensionFactory underTest;

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(DIMENSION_ID);

        Dimension result = underTest.create(USER_ID, EXTERNAL_REFERENCE, INDEX);

        assertThat(result.getDimensionId()).isEqualTo(DIMENSION_ID);
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getIndex()).isEqualTo(INDEX);
    }
}