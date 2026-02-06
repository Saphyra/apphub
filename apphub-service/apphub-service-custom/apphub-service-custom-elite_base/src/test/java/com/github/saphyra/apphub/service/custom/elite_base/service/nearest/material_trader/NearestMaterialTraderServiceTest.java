package com.github.saphyra.apphub.service.custom.elite_base.service.nearest.material_trader;

import com.github.saphyra.apphub.api.custom.elite_base.model.MaterialType;
import com.github.saphyra.apphub.api.custom.elite_base.model.material_trader.NearestMaterialTraderResponse;
import com.github.saphyra.apphub.lib.common_util.LazyLoadedField;
import com.github.saphyra.apphub.service.custom.elite_base.common.BufferSynchronizationService;
import com.github.saphyra.apphub.service.custom.elite_base.dao.EconomyEnum;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body.Body;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body.BodyDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystemDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystemPosition;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.Station;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.StationDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.material_trader_override.MaterialTraderOverride;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.material_trader_override.MaterialTraderOverrideDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_service.StationServiceEnum;
import com.github.saphyra.apphub.test.common.CustomAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class NearestMaterialTraderServiceTest {
    private static final UUID REFERENCE_STAR_ID = UUID.randomUUID();
    private static final UUID MATERIAL_TRADER_STAR_ID = UUID.randomUUID();
    private static final UUID STATION_ID = UUID.randomUUID();
    private static final String STATION_NAME = "station-name";
    private static final String STAR_NAME = "star-name";
    private static final UUID BODY_ID = UUID.randomUUID();
    private static final Double DISTANCE_FROM_STAR = 23.0;

    @Autowired
    private NearestMaterialTraderService underTest;

    @Autowired
    private List<CrudRepository<?, ?>> repositories;

    @Autowired
    private StarSystemDao starSystemDao;

    @Autowired
    private StationDao stationDao;

    @Autowired
    private BodyDao bodyDao;

    @Autowired
    private MaterialTraderOverrideDao materialTraderOverrideDao;

    @Autowired
    private BufferSynchronizationService bufferSynchronizationService;

    @BeforeEach
    void setUp() {
        StarSystem referenceStarSystem = StarSystem.builder()
            .id(REFERENCE_STAR_ID)
            .position(StarSystemPosition.builder()
                .x(0d)
                .y(0d)
                .z(0d)
                .build())
            .build();
        starSystemDao.save(referenceStarSystem);
    }

    @AfterEach
    void cleanup() {
        repositories.forEach(CrudRepository::deleteAll);
    }

    @Test
    void getNearestMaterialTraders_any() {
        StarSystem materialTraderStarSystem = StarSystem.builder()
            .id(MATERIAL_TRADER_STAR_ID)
            .starName(STAR_NAME)
            .position(StarSystemPosition.builder()
                .x(5d)
                .y(0d)
                .z(0d)
                .build())
            .build();
        starSystemDao.save(materialTraderStarSystem);

        Station station = Station.builder()
            .id(STATION_ID)
            .starSystemId(MATERIAL_TRADER_STAR_ID)
            .stationName(STATION_NAME)
            .bodyId(BODY_ID)
            .economy(EconomyEnum.INDUSTRIAL)
            .services(LazyLoadedField.loaded(List.of(StationServiceEnum.MATERIAL_TRADER)))
            .economies(LazyLoadedField.loaded(Collections.emptyList()))
            .build();
        stationDao.save(station);

        Body body = Body.builder()
            .id(BODY_ID)
            .distanceFromStar(DISTANCE_FROM_STAR)
            .build();
        bodyDao.save(body);

        bufferSynchronizationService.synchronizeAll();

        CustomAssertions.singleListAssertThat(underTest.getNearestMaterialTraders(REFERENCE_STAR_ID, MaterialType.ANY, 0))
            .returns(5d, NearestMaterialTraderResponse::getDistanceFromReference)
            .returns(MATERIAL_TRADER_STAR_ID, NearestMaterialTraderResponse::getStarId)
            .returns(STAR_NAME, NearestMaterialTraderResponse::getStarName)
            .returns(STATION_ID, NearestMaterialTraderResponse::getStationId)
            .returns(STATION_NAME, NearestMaterialTraderResponse::getStationName)
            .returns(DISTANCE_FROM_STAR, NearestMaterialTraderResponse::getDistanceFromStar)
            .returns(MaterialType.MANUFACTURED, NearestMaterialTraderResponse::getMaterialType);
    }

    @Test
    void getNearestMaterialTraders_specified() {
        StarSystem materialTraderStarSystem = StarSystem.builder()
            .id(MATERIAL_TRADER_STAR_ID)
            .starName(STAR_NAME)
            .position(StarSystemPosition.builder()
                .x(5d)
                .y(0d)
                .z(0d)
                .build())
            .build();
        starSystemDao.save(materialTraderStarSystem);

        Station station = Station.builder()
            .id(STATION_ID)
            .starSystemId(MATERIAL_TRADER_STAR_ID)
            .stationName(STATION_NAME)
            .bodyId(BODY_ID)
            .economy(EconomyEnum.INDUSTRIAL)
            .services(LazyLoadedField.loaded(List.of(StationServiceEnum.MATERIAL_TRADER)))
            .economies(LazyLoadedField.loaded(Collections.emptyList()))
            .build();
        stationDao.save(station);

        Body body = Body.builder()
            .id(BODY_ID)
            .distanceFromStar(DISTANCE_FROM_STAR)
            .build();
        bodyDao.save(body);

        bufferSynchronizationService.synchronizeAll();

        CustomAssertions.singleListAssertThat(underTest.getNearestMaterialTraders(REFERENCE_STAR_ID, MaterialType.MANUFACTURED, 0))
            .returns(5d, NearestMaterialTraderResponse::getDistanceFromReference)
            .returns(MATERIAL_TRADER_STAR_ID, NearestMaterialTraderResponse::getStarId)
            .returns(STAR_NAME, NearestMaterialTraderResponse::getStarName)
            .returns(STATION_ID, NearestMaterialTraderResponse::getStationId)
            .returns(STATION_NAME, NearestMaterialTraderResponse::getStationName)
            .returns(DISTANCE_FROM_STAR, NearestMaterialTraderResponse::getDistanceFromStar)
            .returns(MaterialType.MANUFACTURED, NearestMaterialTraderResponse::getMaterialType);
    }

    @Test
    void getNearestMaterialTraders_overriddenIncluded() {
        StarSystem materialTraderStarSystem = StarSystem.builder()
            .id(MATERIAL_TRADER_STAR_ID)
            .starName(STAR_NAME)
            .position(StarSystemPosition.builder()
                .x(5d)
                .y(0d)
                .z(0d)
                .build())
            .build();
        starSystemDao.save(materialTraderStarSystem);

        Station station = Station.builder()
            .id(STATION_ID)
            .starSystemId(MATERIAL_TRADER_STAR_ID)
            .stationName(STATION_NAME)
            .bodyId(BODY_ID)
            .economy(EconomyEnum.INDUSTRIAL)
            .services(LazyLoadedField.loaded(List.of(StationServiceEnum.MATERIAL_TRADER)))
            .economy(EconomyEnum.INDUSTRIAL)
            .economies(LazyLoadedField.loaded(Collections.emptyList()))
            .build();
        stationDao.save(station);

        Body body = Body.builder()
            .id(BODY_ID)
            .distanceFromStar(DISTANCE_FROM_STAR)
            .build();
        bodyDao.save(body);

        MaterialTraderOverride override = MaterialTraderOverride.builder()
            .stationId(STATION_ID)
            .materialType(MaterialType.ENCODED)
            .build();
        materialTraderOverrideDao.save(override);

        bufferSynchronizationService.synchronizeAll();

        CustomAssertions.singleListAssertThat(underTest.getNearestMaterialTraders(REFERENCE_STAR_ID, MaterialType.ENCODED, 0))
            .returns(5d, NearestMaterialTraderResponse::getDistanceFromReference)
            .returns(MATERIAL_TRADER_STAR_ID, NearestMaterialTraderResponse::getStarId)
            .returns(STAR_NAME, NearestMaterialTraderResponse::getStarName)
            .returns(STATION_ID, NearestMaterialTraderResponse::getStationId)
            .returns(STATION_NAME, NearestMaterialTraderResponse::getStationName)
            .returns(DISTANCE_FROM_STAR, NearestMaterialTraderResponse::getDistanceFromStar)
            .returns(MaterialType.ENCODED, NearestMaterialTraderResponse::getMaterialType);
    }

    @Test
    void getNearestMaterialTraders_overriddenExcluded() {
        StarSystem materialTraderStarSystem = StarSystem.builder()
            .id(MATERIAL_TRADER_STAR_ID)
            .starName(STAR_NAME)
            .position(StarSystemPosition.builder()
                .x(5d)
                .y(0d)
                .z(0d)
                .build())
            .build();
        starSystemDao.save(materialTraderStarSystem);

        Station station = Station.builder()
            .id(STATION_ID)
            .starSystemId(MATERIAL_TRADER_STAR_ID)
            .stationName(STATION_NAME)
            .bodyId(BODY_ID)
            .economy(EconomyEnum.INDUSTRIAL)
            .services(LazyLoadedField.loaded(List.of(StationServiceEnum.MATERIAL_TRADER)))
            .economy(EconomyEnum.INDUSTRIAL)
            .economies(LazyLoadedField.loaded(Collections.emptyList()))
            .build();
        stationDao.save(station);

        Body body = Body.builder()
            .id(BODY_ID)
            .distanceFromStar(DISTANCE_FROM_STAR)
            .build();
        bodyDao.save(body);

        MaterialTraderOverride override = MaterialTraderOverride.builder()
            .stationId(STATION_ID)
            .materialType(MaterialType.ENCODED)
            .build();
        materialTraderOverrideDao.save(override);

        bufferSynchronizationService.synchronizeAll();

        assertThat(underTest.getNearestMaterialTraders(REFERENCE_STAR_ID, MaterialType.MANUFACTURED, 0)).isEmpty();
    }
}