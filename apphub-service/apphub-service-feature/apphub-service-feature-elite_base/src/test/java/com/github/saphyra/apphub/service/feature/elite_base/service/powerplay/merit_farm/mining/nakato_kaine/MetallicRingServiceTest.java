package com.github.saphyra.apphub.service.feature.elite_base.service.powerplay.merit_farm.mining.nakato_kaine;

import com.github.saphyra.apphub.api.feature.elite_base.model.ReserveLevel;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.Body;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.BodyDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.BodyType;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.body_data.BodyData;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.body_data.BodyDataDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.body_ring.BodyRing;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.body_ring.BodyRingDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.body_ring.RingType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MetallicRingServiceTest {
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final UUID BODY_ID = UUID.randomUUID();

    @Mock
    private BodyDao bodyDao;

    @Mock
    private BodyRingDao bodyRingDao;

    @Mock
    private BodyDataDao bodyDataDao;

    @InjectMocks
    private MetallicRingService underTest;

    @Mock
    private Body body;

    @Mock
    private BodyRing bodyRing;

    @Mock
    private BodyData bodyData;

    @Test
    void getSystemsWithMetallicRing(){
        given(bodyDao.getByStarSystemIds(List.of(STAR_SYSTEM_ID))).willReturn(List.of(body));
        given(body.getType()).willReturn(BodyType.PLANET);
        given(body.getStarSystemId()).willReturn(STAR_SYSTEM_ID);
        given(body.getId()).willReturn(BODY_ID);
        given(bodyRingDao.getByBodyIds(List.of(BODY_ID))).willReturn(List.of(bodyRing));
        given(bodyRing.getType()).willReturn(RingType.METALLIC);
        given(bodyRing.getBodyId()).willReturn(BODY_ID);
        given(bodyDataDao.getByIds(Set.of(BODY_ID))).willReturn(List.of(bodyData));
        given(bodyData.getBodyId()).willReturn(BODY_ID);
        given(bodyData.getReserveLevel()).willReturn(ReserveLevel.COMMON);

        assertThat(underTest.getSystemsWithMetallicRing(List.of(STAR_SYSTEM_ID))).containsEntry(STAR_SYSTEM_ID, ReserveLevel.COMMON);
    }
}