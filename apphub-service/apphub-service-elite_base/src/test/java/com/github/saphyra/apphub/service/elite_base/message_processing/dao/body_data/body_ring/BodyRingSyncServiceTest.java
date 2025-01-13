package com.github.saphyra.apphub.service.elite_base.message_processing.dao.body_data.body_ring;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class BodyRingSyncServiceTest {
    private static final UUID BODY_ID = UUID.randomUUID();

    @Mock
    private BodyRingDao bodyRingDao;

    @InjectMocks
    private BodyRingSyncService underTest;

    @Mock
    private BodyRing existingRing;

    @Mock
    private BodyRing newRing;

    @Mock
    private BodyRing deprecatedRing;

    @Test
    void sync() {
        given(bodyRingDao.getByBodyId(BODY_ID)).willReturn(List.of(existingRing, deprecatedRing));

        underTest.sync(BODY_ID, List.of(existingRing, newRing));

        then(bodyRingDao).should().deleteAll(List.of(deprecatedRing));
        then(bodyRingDao).should().saveAll(List.of(newRing));
    }
}