package com.github.saphyra.apphub.service.feature.elite_base.processor.dao.body.body_ring;

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
    private static final String EXISTING_RING_NAME = "existing-ring";
    private static final String DEPRECATED_RING_NAME = "deprecated-ring";
    private static final String NEW_RING_NAME = "new-ring";

    @Mock
    private BodyRingDao bodyRingDao;

    @InjectMocks
    private BodyRingSyncService underTest;

    @Mock
    private BodyRing existingRing;

    @Mock
    private BodyRing duplicatedRing;

    @Mock
    private BodyRing newRing;

    @Mock
    private BodyRing deprecatedRing;

    @Test
    void sync() {
        given(bodyRingDao.getByBodyId(BODY_ID)).willReturn(List.of(existingRing, deprecatedRing, duplicatedRing));
        given(existingRing.getName()).willReturn(EXISTING_RING_NAME);
        given(duplicatedRing.getName()).willReturn(EXISTING_RING_NAME);
        given(deprecatedRing.getName()).willReturn(DEPRECATED_RING_NAME);
        given(newRing.getName()).willReturn(NEW_RING_NAME);

        underTest.sync(BODY_ID, List.of(existingRing, newRing));

        then(bodyRingDao).should().delete(duplicatedRing);
        then(bodyRingDao).should().deleteAll(List.of(deprecatedRing));
        then(bodyRingDao).should().saveAll(List.of(newRing));
    }
}