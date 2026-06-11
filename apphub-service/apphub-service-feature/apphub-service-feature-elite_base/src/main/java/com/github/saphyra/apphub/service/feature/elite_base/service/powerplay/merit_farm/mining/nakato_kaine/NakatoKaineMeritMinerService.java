package com.github.saphyra.apphub.service.feature.elite_base.service.powerplay.merit_farm.mining.nakato_kaine;

import com.github.saphyra.apphub.api.feature.elite_base.model.merit_farm.MiningMeritFarmRequest;
import com.github.saphyra.apphub.api.feature.elite_base.model.merit_farm.MiningMeritFarmResponse;
import com.github.saphyra.apphub.api.feature.elite_base.model.merit_farm.PowerplayActivityType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.TriWrapper;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.FutureWrapper;
import com.github.saphyra.apphub.lib.geometry.n_dimension.NDimensionCoordinate;
import com.github.saphyra.apphub.lib.geometry.n_dimension.NDimensionDistanceCalculator;
import com.github.saphyra.apphub.lib.sql_builder.SqlBuilder;
import com.github.saphyra.apphub.lib.sql_builder.column.DefaultColumn;
import com.github.saphyra.apphub.lib.sql_builder.column.QualifiedColumn;
import com.github.saphyra.apphub.lib.sql_builder.condition.BetweenCondition;
import com.github.saphyra.apphub.lib.sql_builder.operation.Equation;
import com.github.saphyra.apphub.lib.sql_builder.table.QualifiedTable;
import com.github.saphyra.apphub.lib.sql_builder.value.NumberValue;
import com.github.saphyra.apphub.lib.sql_builder.value.WrappedValue;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.Body;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.BodyDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.BodyType;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.body_data.BodyData;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.body_data.BodyDataDao;
import com.github.saphyra.apphub.api.feature.elite_base.model.ReserveLevel;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.body_ring.BodyRing;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.body_ring.BodyRingDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.body_ring.RingType;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.ItemType;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.trading.commodity.Commodity;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.trading.commodity.CommodityDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdateDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.StarSystemDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.StarSystemPosition;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.star_system_data.Power;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.star_system_data.PowerplayState;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.star_system_data.StarSystemData;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.star_system_data.StarSystemDataDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.station.Station;
import com.github.saphyra.apphub.service.feature.elite_base.dao.station.StationDao;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_ID;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_POWERPLAY_STATE;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_STAR_NAME;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_STAR_SYSTEM_ID;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_X_POS;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_Y_POS;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_Z_POS;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_STAR_SYSTEM;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_STAR_SYSTEM_DATA;
import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
//TODO split
public class NakatoKaineMeritMinerService {
    private static final List<String> COMMODITIES = List.of("painite", "platinum");
    private static final int STRONGHOLD_RANGE = 30;
    private static final Map<PowerplayState, Integer> ACQUISITION_RANGES = Map.of(
        PowerplayState.FORTIFIED, 20,
        PowerplayState.STRONGHOLD, STRONGHOLD_RANGE
    );

    private final StarSystemDataDao starSystemDataDao;
    private final ExecutorServiceBean executorServiceBean;
    private final BodyDao bodyDao;
    private final BodyRingDao bodyRingDao;
    private final StationDao stationDao;
    private final CommodityDao commodityDao;
    private final StarSystemDao starSystemDao;
    private final JdbcTemplate jdbcTemplate;
    private final UuidConverter uuidConverter;
    private final NDimensionDistanceCalculator distanceCalculator;
    private final LastUpdateDao lastUpdateDao;
    private final BodyDataDao bodyDataDao;
    private final DateTimeUtil dateTimeUtil;

    public List<MiningMeritFarmResponse> getLocations(MiningMeritFarmRequest request) {
        Map<PowerplayState, List<StarSystemData>> nakatoKaineSystems = starSystemDataDao.getByControllingPower(Power.NAKATO_KAINE)
            .stream()
            .collect(Collectors.groupingBy(StarSystemData::getPowerplayState));

        List<FutureWrapper<List<MiningMeritFarmResponse>>> futures = Stream.of(
                executorServiceBean.asyncProcess(() -> getReinforcement(extractByType(nakatoKaineSystems, PowerplayState.EXPLOITED, PowerplayState.FORTIFIED, PowerplayState.STRONGHOLD))),
                executorServiceBean.asyncProcess(() -> getAcquisition(extractByType(nakatoKaineSystems, PowerplayState.FORTIFIED, PowerplayState.STRONGHOLD)))
            )
            .toList();

        LocalDateTime currentTime = dateTimeUtil.getCurrentDateTime();

        return futures.stream()
            .map(FutureWrapper::get)
            .map(ExecutionResult::getOrThrow)
            .flatMap(List::stream)
            .filter(response -> response.getReserveLevel().getLevel() >= request.getMinimumReserveLevel().getLevel())
            .filter(response -> response.getPrice() >= request.getMinimumPrice())
            .filter(response -> response.getDemand() >= request.getMinimumDemand())
            .filter(response -> response.getLastUpdate().isAfter(currentTime.minus(request.getMaxTimeSinceLastUpdated())))
            .filter(response -> isNull(request.getPowerplayActivity()) || response.getActivityType() == request.getPowerplayActivity())
            .toList();
    }

    private List<MiningMeritFarmResponse> getAcquisition(List<StarSystemData> sourceSystemCandidates) {
        log.info("There are {} source systems for acquisition", sourceSystemCandidates.size());
        Map<UUID, ReserveLevel> sourceSystemIds = getSystemsWithMetallicRing(sourceSystemCandidates.stream().map(StarSystemData::getStarSystemId).toList());
        log.info("There are {} source systems for acquisition with metallic ring", sourceSystemIds.size());

        Map<UUID, StarSystem> sourceSystems = starSystemDao.getByIds(sourceSystemIds.keySet())
            .stream()
            .collect(Collectors.toMap(StarSystem::getId, starSystem -> starSystem));

        Map<UUID, List<StarSystem>> targetsSourceMapping = getAcquisitionTargets(sourceSystems.values(), sourceSystemCandidates);

        List<UUID> targetStarSystemIds = targetsSourceMapping.values()
            .stream()
            .flatMap(Collection::stream)
            .map(StarSystem::getId)
            .distinct()
            .toList();
        log.info("There are {} unoccupied systems available for acquisition", targetStarSystemIds.size());

        List<Station> stations = stationDao.getByStarSystemIds(targetStarSystemIds);
        log.info("There are {} stations in acquirable systems", stations.size());

        List<Commodity> offers = getOffers(stations, PowerplayActivityType.ACQUISITION);

        Map<UUID, String> starSystemNames = Stream.concat(
                sourceSystems.values().stream(),
                targetsSourceMapping.values().stream().flatMap(Collection::stream)
            )
            .map(starSystem -> new BiWrapper<>(starSystem.getId(), starSystem.getStarName()))
            .distinct()
            .collect(Collectors.toMap(BiWrapper::getEntity1, BiWrapper::getEntity2));

        Map<UUID, Station> stationMap = stations.stream()
            .collect(Collectors.toMap(Station::getId, station -> station));

        return targetsSourceMapping.entrySet()
            .stream()
            .flatMap(entry -> entry.getValue().stream().map(targetSystem -> new BiWrapper<>(entry.getKey(), targetSystem.getId())))
            .flatMap(route -> {
                UUID sourceSystemId = route.getEntity1();
                UUID targetSystemId = route.getEntity2();

                return offers.stream()
                    .filter(commodity -> stationMap.get(commodity.getExternalReference()).getStarSystemId().equals(targetSystemId))
                    .map(offer -> new TriWrapper<>(sourceSystemId, targetSystemId, offer));
            })
            .map(tw -> {
                UUID sourceSystemId = tw.getEntity1();
                UUID targetSystemId = tw.getEntity2();
                Commodity offer = tw.getEntity3();

                return MiningMeritFarmResponse.builder()
                    .sourceStarSystemId(sourceSystemId)
                    .sourceStarSystemName(starSystemNames.get(sourceSystemId))
                    .reserveLevel(sourceSystemIds.get(sourceSystemId))
                    .targetStarSystemId(targetSystemId)
                    .targetStarSystemName(starSystemNames.get(targetSystemId))
                    .stationId(offer.getExternalReference())
                    .stationName(stationMap.get(offer.getExternalReference()).getStationName())
                    .commodityName(offer.getItemName())
                    .demand(offer.getDemand())
                    .price(offer.getBuyPrice())
                    .lastUpdate(lastUpdateDao.findByIdValidated(offer.getExternalReference(), ItemType.COMMODITY).getLastUpdate())
                    .activityType(PowerplayActivityType.ACQUISITION)
                    .build();
            })
            .toList();
    }

    private Map<UUID, List<StarSystem>> getAcquisitionTargets(Collection<StarSystem> starSystems, List<StarSystemData> sourceSystemData) {
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;
        double minZ = Double.MAX_VALUE;
        double maxZ = Double.MIN_VALUE;
        for (StarSystem starSystem : starSystems) {
            StarSystemPosition position = starSystem.getPosition();
            if (position.getX() < minX) {
                minX = position.getX();
            }
            if (position.getX() > maxX) {
                maxX = position.getX();
            }
            if (position.getY() < minY) {
                minY = position.getX();
            }
            if (position.getY() > maxY) {
                maxY = position.getX();
            }
            if (position.getZ() < minZ) {
                minZ = position.getX();
            }
            if (position.getZ() > maxZ) {
                maxZ = position.getX();
            }
        }

        String sql = SqlBuilder.select()
            .from(new QualifiedTable(SCHEMA, TABLE_STAR_SYSTEM))
            .columns(
                new QualifiedColumn(TABLE_STAR_SYSTEM, COLUMN_ID),
                new DefaultColumn(COLUMN_STAR_NAME),
                new DefaultColumn(COLUMN_X_POS),
                new DefaultColumn(COLUMN_Y_POS),
                new DefaultColumn(COLUMN_Z_POS)
            )
            .condition(new BetweenCondition(new QualifiedColumn(TABLE_STAR_SYSTEM, COLUMN_X_POS), new NumberValue(minX - STRONGHOLD_RANGE), new NumberValue(maxX + STRONGHOLD_RANGE)))
            .and()
            .condition(new BetweenCondition(new QualifiedColumn(TABLE_STAR_SYSTEM, COLUMN_Y_POS), new NumberValue(minY - STRONGHOLD_RANGE), new NumberValue(maxY + STRONGHOLD_RANGE)))
            .and()
            .condition(new BetweenCondition(new QualifiedColumn(TABLE_STAR_SYSTEM, COLUMN_Z_POS), new NumberValue(minZ - STRONGHOLD_RANGE), new NumberValue(maxZ + STRONGHOLD_RANGE)))
            .and()
            .condition(new Equation(
                new QualifiedColumn(TABLE_STAR_SYSTEM_DATA, COLUMN_POWERPLAY_STATE),
                new WrappedValue(PowerplayState.UNOCCUPIED.name())
            ))
            .innerJoin(
                new QualifiedTable(SCHEMA, TABLE_STAR_SYSTEM_DATA),
                new QualifiedColumn(TABLE_STAR_SYSTEM, COLUMN_ID),
                new QualifiedColumn(TABLE_STAR_SYSTEM_DATA, COLUMN_STAR_SYSTEM_ID)
            )
            .build();

        List<StarSystem> unoccupiedSystemsInCube = jdbcTemplate.query(
            sql,
            (row, _) -> StarSystem.builder()
                .id(uuidConverter.convertEntity(row.getString(COLUMN_ID)))
                .starName(row.getString(COLUMN_STAR_NAME))
                .position(StarSystemPosition.builder()
                    .x(row.getDouble(COLUMN_X_POS))
                    .y(row.getDouble(COLUMN_Y_POS))
                    .z(row.getDouble(COLUMN_Z_POS))
                    .build())
                .build()
        );

        return starSystems.stream()
            .collect(Collectors.toMap(StarSystem::getId, starSystem -> getTargetsInRange(starSystem, sourceSystemData, unoccupiedSystemsInCube)));
    }

    private List<StarSystem> getTargetsInRange(StarSystem sourceSystem, List<StarSystemData> sourceSystemData, List<StarSystem> unoccupiedSystemsInCube) {
        Map<UUID, StarSystemData> dataMap = sourceSystemData.stream()
            .collect(Collectors.toMap(StarSystemData::getStarSystemId, starSystemData -> starSystemData));

        int range = ACQUISITION_RANGES.get(dataMap.get(sourceSystem.getId()).getPowerplayState());

        StarSystemPosition sourcePosition = sourceSystem.getPosition();
        NDimensionCoordinate sourceCoordinate = new NDimensionCoordinate(sourcePosition.getX(), sourcePosition.getY(), sourcePosition.getZ());

        return unoccupiedSystemsInCube.stream()
            .filter(starSystem -> {
                StarSystemPosition targetPosition = starSystem.getPosition();
                NDimensionCoordinate targetCoordinate = new NDimensionCoordinate(targetPosition.getX(), targetPosition.getY(), targetPosition.getZ());
                double distance = distanceCalculator.calculateDistance(sourceCoordinate, targetCoordinate);

                return distance <= range;
            })
            .toList();
    }

    private List<MiningMeritFarmResponse> getReinforcement(List<StarSystemData> starSystemData) {
        log.info("{} systems found for reinforcement", starSystemData.size());
        Map<UUID, ReserveLevel> starSystemsWithMetallicRing = getSystemsWithMetallicRing(starSystemData.stream().map(StarSystemData::getStarSystemId).toList());
        log.info("{} reinforcement systems have metallic ring", starSystemsWithMetallicRing.size());

        List<Station> stations = stationDao.getByStarSystemIds(starSystemsWithMetallicRing.keySet());
        log.info("{} stations found in reinforcement systems", stations.size());

        Map<UUID, Station> stationMap = stations.stream()
            .collect(Collectors.toMap(Station::getId, station -> station));

        List<Commodity> offers = getOffers(stations, PowerplayActivityType.REINFORCEMENT);

        List<UUID> starSystemIds = offers.stream()
            .map(commodity -> stationMap.get(commodity.getExternalReference()).getStarSystemId())
            .distinct()
            .toList();

        Map<UUID, String> starSystemNames = starSystemDao.getByIds(starSystemIds)
            .stream()
            .collect(Collectors.toMap(StarSystem::getId, StarSystem::getStarName));

        return offers.stream()
            .map(offer -> {
                UUID starSystemId = stationMap.get(offer.getExternalReference())
                    .getStarSystemId();
                return MiningMeritFarmResponse.builder()
                    .sourceStarSystemId(starSystemId)
                    .sourceStarSystemName(starSystemNames.get(starSystemId))
                    .reserveLevel(starSystemsWithMetallicRing.get(starSystemId))
                    .targetStarSystemId(starSystemId)
                    .targetStarSystemName(starSystemNames.get(starSystemId))
                    .stationId(offer.getExternalReference())
                    .stationName(stationMap.get(offer.getExternalReference()).getStationName())
                    .commodityName(offer.getItemName())
                    .demand(offer.getDemand())
                    .price(offer.getBuyPrice())
                    .lastUpdate(lastUpdateDao.findByIdValidated(offer.getExternalReference(), ItemType.COMMODITY).getLastUpdate())
                    .activityType(PowerplayActivityType.REINFORCEMENT)
                    .build();
            })
            .toList();
    }

    private List<Commodity> getOffers(List<Station> stations, PowerplayActivityType activityType) {
        Map<UUID, List<Station>> stationsOfStarSystems = stations.stream()
            .collect(Collectors.groupingBy(Station::getStarSystemId));

        List<BiWrapper<UUID, String>> stationIdCommodityMapping = stationsOfStarSystems.values()
            .stream()
            .flatMap(Collection::stream)
            .map(Station::getId)
            .flatMap(stationId -> COMMODITIES.stream().map(commodity -> new BiWrapper<>(stationId, commodity)))
            .toList();
        log.info("Searching for {} offers for {} systems", stationIdCommodityMapping.size(), activityType);

        List<FutureWrapper<List<Commodity>>> futures = Lists.partition(stationIdCommodityMapping, 1000)
            .stream()
            .map(batch -> executorServiceBean.asyncProcess(() -> commodityDao.getByIds(batch)))
            .toList();

        List<Commodity> offers = futures.stream()
            .map(FutureWrapper::get)
            .map(ExecutionResult::getOrThrow)
            .flatMap(Collection::stream)
            .filter(commodity -> commodity.getDemand() > 0)
            .toList();
        log.info("{} offers found for {} systems", offers.size(), activityType);
        return offers;
    }

    private Map<UUID, ReserveLevel> getSystemsWithMetallicRing(Collection<UUID> starSystemIds) {
        log.info("Get bodies of {} starSystems", starSystemIds.size());
        Map<UUID, List<UUID>> starSystemBodiesMap = bodyDao.getByStarSystemIds(starSystemIds)
            .stream()
            .filter(body -> body.getType() == BodyType.PLANET)
            .collect(Collectors.groupingBy(Body::getStarSystemId))
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream().map(Body::getId).toList()));

        List<UUID> bodyIds = starSystemBodiesMap.values()
            .stream()
            .flatMap(Collection::stream)
            .distinct()
            .toList();
        log.info("Bodies found: {}", bodyIds.size());

        List<BodyRing> metallicRings = bodyRingDao.getByBodyIds(bodyIds)
            .stream()
            .filter(bodyRing -> bodyRing.getType() == RingType.METALLIC)
            .toList();
        log.info("Metallic rings found: {}", metallicRings.size());

        Map<UUID, ReserveLevel> bodyReserveLevelMap = getReserveLevels(metallicRings.stream().map(BodyRing::getBodyId).collect(Collectors.toSet()));

        return metallicRings.stream()
            .map(BodyRing::getBodyId)
            .distinct()
            .map(bodyId -> starSystemBodiesMap.entrySet()
                .stream()
                .filter(e -> e.getValue().contains(bodyId))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("StarSystemId not found for bodyId" + bodyId))
            )
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue()
                    .stream()
                    /*
                    bodyReserveLevelMap contains only reserveLevel of metallic rings,
                    while starSystemBodiesMap contains all the bodies of a StarSystem.
                     */
                    .map(bodyId -> bodyReserveLevelMap.getOrDefault(bodyId, ReserveLevel.UNKNOWN))
                    .max(Comparator.comparingInt(ReserveLevel::getLevel))
                    .orElse(ReserveLevel.UNKNOWN),
                //Planet might have multiple metallic rings, if this happens, pick the ring with higher reserve level (in reality, reserve level should be equal for all rings in the same system)
                (r1, r2) -> r1.getLevel() >= r2.getLevel() ? r1 : r2
            ));
    }

    private Map<UUID, ReserveLevel> getReserveLevels(Collection<UUID> bodyIds) {
        return bodyDataDao.getByIds(bodyIds)
            .stream()
            .collect(Collectors.toMap(BodyData::getBodyId, bodyData -> Optional.ofNullable(bodyData.getReserveLevel()).orElse(ReserveLevel.UNKNOWN)));
    }

    private List<StarSystemData> extractByType(Map<PowerplayState, List<StarSystemData>> systems, PowerplayState... states) {
        return Arrays.stream(states)
            .flatMap(powerplayState -> systems.getOrDefault(powerplayState, List.of()).stream())
            .toList();
    }
}
