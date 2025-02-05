package com.github.saphyra.apphub.service.elite_base.message_processing.saver;

import com.github.saphyra.apphub.service.elite_base.message_processing.dao.body_data.BodyData;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.body_data.BodyDataDao;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.body_data.BodyDataFactory;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.body_data.ReserveLevel;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.body_data.body_material.BodyMaterial;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.body_data.body_material.BodyMaterialFactory;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.body_data.body_ring.BodyRing;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.body_data.body_ring.BodyRingFactory;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.NamePercentPair;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.Ring;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class BodyDataSaverTest {
    private static final UUID BODY_ID = UUID.randomUUID();
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();
    private static final Double SURFACE_GRAVITY = 342.432;

    @Mock
    private BodyDataDao bodyDataDao;

    @Mock
    private BodyDataFactory bodyDataFactory;

    @Mock
    private BodyRingFactory bodyRingFactory;

    @Mock
    private BodyMaterialFactory bodyMaterialFactory;

    @InjectMocks
    private BodyDataSaver underTest;

    @Mock
    private NamePercentPair material;

    @Mock
    private Ring ring;

    @Mock
    private BodyData bodyData;

    @Mock
    private BodyMaterial bodyMaterial;

    @Mock
    private BodyRing bodyRing;

    @Test
    void newRecord_deprecatedMessage() {
        NamePercentPair[] materials = {material};
        Ring[] rings = {ring};

        given(bodyDataDao.findById(BODY_ID)).willReturn(Optional.empty());
        given(bodyDataFactory.create(BODY_ID, LAST_UPDATE, true, SURFACE_GRAVITY, ReserveLevel.LOW, true, materials, rings)).willReturn(bodyData);
        given(bodyData.getLastUpdate()).willReturn(LAST_UPDATE.plusSeconds(1));

        underTest.save(BODY_ID, LAST_UPDATE, true, SURFACE_GRAVITY, ReserveLevel.LOW, true, materials, rings);

        then(bodyDataDao).should().save(bodyData);
    }

    @Test
    void noUpdateForFields() {
        given(bodyDataDao.findById(BODY_ID)).willReturn(Optional.of(bodyData));
        given(bodyData.getLastUpdate()).willReturn(LAST_UPDATE);

        underTest.save(BODY_ID, LAST_UPDATE, null, null, null, null, null, null);

        then(bodyData).should(times(0)).setLastUpdate(any());
        then(bodyData).should(times(0)).setLandable(any());
        then(bodyData).should(times(0)).setSurfaceGravity(any());
        then(bodyData).should(times(0)).setReserveLevel(any());
        then(bodyData).should(times(0)).setHasRing(any());
        then(bodyData).should(times(0)).setMaterials(any());
        then(bodyData).should(times(0)).setRings(any());
        then(bodyDataDao).should().save(bodyData);
    }

    @Test
    void updateFields() {
        NamePercentPair[] materials = {material};
        Ring[] rings = {ring};

        given(bodyDataDao.findById(BODY_ID)).willReturn(Optional.of(bodyData));
        given(bodyData.getBodyId()).willReturn(BODY_ID);
        given(bodyData.getLastUpdate()).willReturn(LAST_UPDATE.minusSeconds(1));
        given(bodyMaterialFactory.create(BODY_ID, materials)).willReturn(List.of(bodyMaterial));
        given(bodyRingFactory.create(BODY_ID, rings)).willReturn(List.of(bodyRing));

        underTest.save(BODY_ID, LAST_UPDATE, true, SURFACE_GRAVITY, ReserveLevel.LOW, true, materials, rings);

        then(bodyData).should().setLastUpdate(LAST_UPDATE);
        then(bodyData).should().setLandable(true);
        then(bodyData).should().setSurfaceGravity(SURFACE_GRAVITY);
        then(bodyData).should().setReserveLevel(ReserveLevel.LOW);
        then(bodyData).should().setHasRing(true);
        then(bodyData).should().setMaterials(any());
        then(bodyData).should().setRings(any());
        then(bodyDataDao).should().save(bodyData);
    }

    @Test
    void doNotUpdateWhenNoData() {
        NamePercentPair[] materials = {material};
        Ring[] rings = {ring};

        given(bodyDataDao.findById(BODY_ID)).willReturn(Optional.of(bodyData));
        given(bodyData.getLastUpdate()).willReturn(LAST_UPDATE.minusSeconds(1));

        underTest.save(BODY_ID, LAST_UPDATE, false, SURFACE_GRAVITY, ReserveLevel.LOW, false, materials, rings);

        then(bodyData).should(times(0)).setLandable(any());
        then(bodyData).should(times(0)).setHasRing(any());
        then(bodyData).should(times(0)).setMaterials(any());
        then(bodyData).should(times(0)).setRings(any());
        then(bodyDataDao).should().save(bodyData);
    }

    @Test
    void noUpdateWhenRowCountMatches() {
        NamePercentPair[] materials = {material};
        Ring[] rings = {ring};

        given(bodyDataDao.findById(BODY_ID)).willReturn(Optional.of(bodyData));
        given(bodyData.getLastUpdate()).willReturn(LAST_UPDATE);
        given(bodyData.getMaterials()).willReturn(List.of(bodyMaterial));
        given(bodyData.getRings()).willReturn(List.of(bodyRing));

        underTest.save(BODY_ID, LAST_UPDATE, true, SURFACE_GRAVITY, ReserveLevel.LOW, true, materials, rings);

        then(bodyData).should(times(0)).setMaterials(any());
        then(bodyData).should(times(0)).setRings(any());
        then(bodyDataDao).should().save(bodyData);
    }

    @Test
    void doUpdateWhenDifferentRowCount() {
        NamePercentPair[] materials = {material};
        Ring[] rings = {ring};

        given(bodyDataDao.findById(BODY_ID)).willReturn(Optional.of(bodyData));
        given(bodyData.getLastUpdate()).willReturn(LAST_UPDATE);
        given(bodyData.getMaterials()).willReturn(List.of());
        given(bodyData.getRings()).willReturn(List.of());

        underTest.save(BODY_ID, LAST_UPDATE, true, SURFACE_GRAVITY, ReserveLevel.LOW, true, materials, rings);

        then(bodyData).should().setMaterials(any());
        then(bodyData).should().setRings(any());
        then(bodyDataDao).should().save(bodyData);
    }
}