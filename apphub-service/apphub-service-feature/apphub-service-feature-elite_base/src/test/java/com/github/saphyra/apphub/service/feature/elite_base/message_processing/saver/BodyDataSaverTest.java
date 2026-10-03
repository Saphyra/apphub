package com.github.saphyra.apphub.service.feature.elite_base.message_processing.saver;

import com.github.saphyra.apphub.api.feature.elite_base.model.ReserveLevel;
import com.github.saphyra.apphub.service.feature.elite_base.dao.ObjectType;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.body_data.BodyData;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.body_data.BodyDataDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.body_data.BodyDataFactory;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.body_material.BodyMaterial;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.body_material.BodyMaterialFactory;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.body_ring.BodyRing;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.body_ring.BodyRingFactory;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdate;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdateDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdateFactory;
import com.github.saphyra.apphub.service.feature.elite_base.message_processing.structure.journal.NamePercentPair;
import com.github.saphyra.apphub.service.feature.elite_base.message_processing.structure.journal.Ring;
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
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class BodyDataSaverTest {
    private static final UUID BODY_ID = UUID.randomUUID();
    private static final LocalDateTime TIMESTAMP = LocalDateTime.now();
    private static final Double SURFACE_GRAVITY = 342.432;

    @Mock
    private BodyDataDao bodyDataDao;

    @Mock
    private BodyDataFactory bodyDataFactory;

    @Mock
    private BodyRingFactory bodyRingFactory;

    @Mock
    private BodyMaterialFactory bodyMaterialFactory;

    @Mock
    private LastUpdateDao lastUpdateDao;

    @Mock
    private LastUpdateFactory lastUpdateFactory;

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

    @Mock
    private LastUpdate lastUpdate;

    @Mock
    private LastUpdate newLastUpdate;

    @Test
    void noUpdateForFields() {
        given(bodyDataDao.findById(BODY_ID)).willReturn(Optional.of(bodyData));
        given(bodyData.getBodyId()).willReturn(BODY_ID);
        given(lastUpdateDao.findByIdOrDefault(BODY_ID, ObjectType.BODY_DATA)).willReturn(lastUpdate);
        given(lastUpdate.getLastUpdate()).willReturn(TIMESTAMP.minusSeconds(1));
        given(lastUpdateFactory.create(BODY_ID, ObjectType.BODY_DATA, TIMESTAMP)).willReturn(newLastUpdate);

        underTest.save(BODY_ID, TIMESTAMP, null, null, null, null, null, null);

        then(bodyData).should(never()).setLandable(any());
        then(bodyData).should(never()).setSurfaceGravity(any());
        then(bodyData).should(never()).setReserveLevel(any());
        then(bodyData).should(never()).setHasRing(any());
        then(bodyData).should(never()).setMaterials(any());
        then(bodyData).should(never()).setRings(any());
        then(bodyDataDao).should(never()).save(bodyData);
        then(lastUpdateDao).should().save(newLastUpdate);
    }

    @Test
    void updateFields() {
        NamePercentPair[] materials = {material};
        Ring[] rings = {ring};

        given(bodyDataDao.findById(BODY_ID)).willReturn(Optional.of(bodyData));
        given(bodyData.getBodyId()).willReturn(BODY_ID);
        given(bodyMaterialFactory.create(BODY_ID, materials)).willReturn(List.of(bodyMaterial));
        given(bodyRingFactory.create(BODY_ID, rings)).willReturn(List.of(bodyRing));
        given(lastUpdateDao.findByIdOrDefault(BODY_ID, ObjectType.BODY_DATA)).willReturn(lastUpdate);
        given(lastUpdate.getLastUpdate()).willReturn(TIMESTAMP.minusSeconds(1));
        given(lastUpdateFactory.create(BODY_ID, ObjectType.BODY_DATA, TIMESTAMP)).willReturn(newLastUpdate);

        underTest.save(BODY_ID, TIMESTAMP, true, SURFACE_GRAVITY, ReserveLevel.LOW, true, materials, rings);

        then(bodyData).should().setLandable(true);
        then(bodyData).should().setSurfaceGravity(SURFACE_GRAVITY);
        then(bodyData).should().setReserveLevel(ReserveLevel.LOW);
        then(bodyData).should().setHasRing(true);
        then(bodyData).should().setMaterials(any());
        then(bodyData).should().setRings(any());
        then(bodyDataDao).should().save(bodyData);
        then(lastUpdateDao).should().save(newLastUpdate);
    }

    @Test
    void noUpdateWhenRowCountMatches() {
        NamePercentPair[] materials = {material};
        Ring[] rings = {ring};

        given(bodyDataDao.findById(BODY_ID)).willReturn(Optional.of(bodyData));
        given(bodyData.getMaterials()).willReturn(List.of(bodyMaterial));
        given(bodyData.getRings()).willReturn(List.of(bodyRing));
        given(bodyData.getBodyId()).willReturn(BODY_ID);
        given(lastUpdateDao.findByIdOrDefault(BODY_ID, ObjectType.BODY_DATA)).willReturn(lastUpdate);
        given(lastUpdate.getLastUpdate()).willReturn(TIMESTAMP.minusSeconds(1));
        given(lastUpdateFactory.create(BODY_ID, ObjectType.BODY_DATA, TIMESTAMP)).willReturn(newLastUpdate);

        underTest.save(BODY_ID, TIMESTAMP, true, SURFACE_GRAVITY, ReserveLevel.LOW, true, materials, rings);

        then(bodyData).should(never()).setMaterials(any());
        then(bodyData).should(never()).setRings(any());
        then(bodyDataDao).should().save(bodyData);
        then(lastUpdateDao).should().save(newLastUpdate);
    }

    @Test
    void doUpdateWhenDifferentRowCount() {
        NamePercentPair[] materials = {material};
        Ring[] rings = {ring};

        given(bodyDataDao.findById(BODY_ID)).willReturn(Optional.of(bodyData));
        given(bodyData.getMaterials()).willReturn(List.of());
        given(bodyData.getRings()).willReturn(List.of());
        given(bodyData.getBodyId()).willReturn(BODY_ID);
        given(lastUpdateDao.findByIdOrDefault(BODY_ID, ObjectType.BODY_DATA)).willReturn(lastUpdate);
        given(lastUpdate.getLastUpdate()).willReturn(TIMESTAMP.minusSeconds(1));
        given(lastUpdateFactory.create(BODY_ID, ObjectType.BODY_DATA, TIMESTAMP)).willReturn(newLastUpdate);

        underTest.save(BODY_ID, TIMESTAMP, true, SURFACE_GRAVITY, ReserveLevel.LOW, true, materials, rings);

        then(bodyData).should().setMaterials(any());
        then(bodyData).should().setRings(any());
        then(bodyDataDao).should().save(bodyData);
        then(lastUpdateDao).should().save(newLastUpdate);
    }

    @Test
    void newRecord() {
        NamePercentPair[] materials = {material};
        Ring[] rings = {ring};

        given(bodyDataDao.findById(BODY_ID)).willReturn(Optional.empty());
        given(bodyDataFactory.create(BODY_ID, true, SURFACE_GRAVITY, ReserveLevel.LOW, true, materials, rings)).willReturn(bodyData);
        given(bodyData.getBodyId()).willReturn(BODY_ID);
        given(lastUpdateFactory.create(BODY_ID, ObjectType.BODY_DATA, TIMESTAMP)).willReturn(newLastUpdate);

        underTest.save(BODY_ID, TIMESTAMP, true, SURFACE_GRAVITY, ReserveLevel.LOW, true, materials, rings);

        then(bodyDataDao).should().save(bodyData);
        then(lastUpdateDao).should().save(newLastUpdate);
    }
}