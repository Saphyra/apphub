package com.github.saphyra.apphub.service.feature.elite_base.service.powerplay.merit_farm.mining.nakato_kaine;

import com.github.saphyra.apphub.api.feature.elite_base.model.merit_farm.MiningMeritFarmResponse;
import com.github.saphyra.apphub.api.feature.elite_base.model.merit_farm.PowerplayActivityType;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.FutureWrapper;
import com.github.saphyra.apphub.lib.sql_builder.SqlBuilder;
import com.github.saphyra.apphub.lib.sql_builder.column.QualifiedColumn;
import com.github.saphyra.apphub.lib.sql_builder.condition.OperationCondition;
import com.github.saphyra.apphub.lib.sql_builder.keyword.PowerSegment;
import com.github.saphyra.apphub.lib.sql_builder.operation.Equation;
import com.github.saphyra.apphub.lib.sql_builder.operation.Operation;
import com.github.saphyra.apphub.lib.sql_builder.operation.SubtractSegment;
import com.github.saphyra.apphub.lib.sql_builder.operation.SumSegment;
import com.github.saphyra.apphub.lib.sql_builder.table.QualifiedTable;
import com.github.saphyra.apphub.lib.sql_builder.value.NumberValue;
import com.github.saphyra.apphub.lib.sql_builder.value.WrappedValue;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.Body;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.BodyDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.BodyType;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.body_ring.BodyRingDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.body_ring.RingType;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.trading.commodity.CommodityDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.StarSystemDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.star_system_data.Power;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.star_system_data.PowerplayState;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.star_system_data.StarSystemData;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.star_system_data.StarSystemDataDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.station.Station;
import com.github.saphyra.apphub.service.feature.elite_base.dao.station.StationDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_ID;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_POWERPLAY_STATE;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_STAR_SYSTEM_ID;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_X_POS;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_Y_POS;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.COLUMN_Z_POS;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_STAR_SYSTEM;
import static com.github.saphyra.apphub.service.feature.elite_base.common.DatabaseConstants.TABLE_STAR_SYSTEM_DATA;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class NakatoKaineMeritMinerService {
    private static final List<String> COMMODITIES = List.of("painite", "platinum");
    private static final Map<PowerplayState, Integer> ACQUISITION_RANGES = Map.of(
        PowerplayState.FORTIFIED, 20,
        PowerplayState.STRONGHOLD, 30
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

    public List<MiningMeritFarmResponse> getLocations() {
        Map<PowerplayState, List<StarSystemData>> nakatoKaineSystems = starSystemDataDao.getByControllingPower(Power.NAKATO_KAINE)
            .stream()
            .collect(Collectors.groupingBy(StarSystemData::getPowerplayState));

        Map<UUID, StarSystem> starSystemMap = new ConcurrentHashMap<>();

        List<FutureWrapper<List<MiningMeritFarmResponse>>> futures = Stream.of(
                executorServiceBean.asyncProcess(() -> getReinforcement(starSystemMap, extractByType(nakatoKaineSystems, PowerplayState.EXPLOITED, PowerplayState.FORTIFIED, PowerplayState.STRONGHOLD))),
                executorServiceBean.asyncProcess(() -> getAcquisition(starSystemMap, extractByType(nakatoKaineSystems, PowerplayState.FORTIFIED, PowerplayState.STRONGHOLD)))
                //executorServiceBean.asyncProcess(() -> getUndermining(extractByType(nakatoKaineSystems, PowerplayState.FORTIFIED, PowerplayState.STRONGHOLD)))
            )
            .toList();

        return futures.stream()
            .map(FutureWrapper::get)
            .map(ExecutionResult::getOrThrow)
            .flatMap(List::stream)
            .toList();
    }

    private List<MiningMeritFarmResponse> getAcquisition(Map<UUID, StarSystem> starSystemMap, List<StarSystemData> sourceSystems) {
        List<FutureWrapper<List<MiningMeritFarmResponse>>> futures = sourceSystems.stream()
            .map(source -> executorServiceBean.asyncProcess(() -> getAcquisitionTargets(source, starSystemMap)))
            .toList();

        return futures.stream()
            .map(FutureWrapper::get)
            .map(ExecutionResult::getOrThrow)
            .flatMap(List::stream)
            .toList();
    }

    private List<MiningMeritFarmResponse> getAcquisitionTargets(StarSystemData sourceSystem, Map<UUID, StarSystem> starSystemMap) {
        if (!hasMetallicRing(sourceSystem.getStarSystemId())) {
            log.info("{} has no metallic ring, cannot be an Acquisition source.", sourceSystem.getStarSystemId());
            return List.of();
        }
        int range = ACQUISITION_RANGES.get(sourceSystem.getPowerplayState());
        log.info("Looking for stations within {} ly to {}", range, sourceSystem.getStarSystemId());

        String sql = SqlBuilder.select()
            .from(new QualifiedTable(SCHEMA, TABLE_STAR_SYSTEM))
            .column(new QualifiedColumn(TABLE_STAR_SYSTEM, COLUMN_ID))
            .condition(new OperationCondition(
                distanceSumSegment(getStarSystem(sourceSystem.getStarSystemId(), starSystemMap)),
                Operation.LOWER_OR_EQUAL,
                new NumberValue(Math.pow(range, 2))
            ))
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

        List<UUID> targetSystemIds = jdbcTemplate.query(sql, (rs, _) -> uuidConverter.convertEntity(rs.getString(COLUMN_ID)));
        log.info("{} StarSystems found within {} ly to {}", targetSystemIds.size(), range, sourceSystem.getStarSystemId());
        List<FutureWrapper<List<MiningMeritFarmResponse>>> futures = targetSystemIds
            .stream()
            .map(targetId -> executorServiceBean.asyncProcess(() -> getOffers(sourceSystem.getStarSystemId(), targetId, starSystemMap, PowerplayActivityType.ACQUISITION)))
            .toList();

        return futures.stream()
            .map(FutureWrapper::get)
            .map(ExecutionResult::getOrThrow)
            .flatMap(List::stream)
            .toList();
    }

    private static SumSegment distanceSumSegment(StarSystem referenceSystem) {
        return new SumSegment(
            new PowerSegment(
                new SubtractSegment(
                    new QualifiedColumn(TABLE_STAR_SYSTEM, COLUMN_X_POS),
                    new NumberValue(referenceSystem.getPosition().getX())
                ),
                2
            ),
            new PowerSegment(
                new SubtractSegment(
                    new QualifiedColumn(TABLE_STAR_SYSTEM, COLUMN_Y_POS),
                    new NumberValue(referenceSystem.getPosition().getY())
                ),
                2
            ),
            new PowerSegment(
                new SubtractSegment(
                    new QualifiedColumn(TABLE_STAR_SYSTEM, COLUMN_Z_POS),
                    new NumberValue(referenceSystem.getPosition().getZ())
                ),
                2
            )
        );
    }

    private List<MiningMeritFarmResponse> getReinforcement(Map<UUID, StarSystem> starSystemMap, List<StarSystemData> starSystemData) {
        List<FutureWrapper<List<MiningMeritFarmResponse>>> futures = starSystemData.stream()
            .map(StarSystemData::getStarSystemId)
            .map(starSystemId -> executorServiceBean.asyncProcess(() -> getForReinforcement(starSystemId, starSystemMap)))
            .toList();

        return futures.stream()
            .map(FutureWrapper::get)
            .map(ExecutionResult::getOrThrow)
            .flatMap(List::stream)
            .toList();
    }

    private List<MiningMeritFarmResponse> getForReinforcement(UUID starSystemId, Map<UUID, StarSystem> starSystemMap) {
        if (!hasMetallicRing(starSystemId)) {
            log.info("{} has no metallic ring, cannot be reinforced.", starSystemId);
            return List.of();
        }

        return getOffers(starSystemId, starSystemId, starSystemMap, PowerplayActivityType.REINFORCEMENT);
    }

    private List<MiningMeritFarmResponse> getOffers(UUID sourceSystemId, UUID targetSystemId, Map<UUID, StarSystem> starSystemMap, PowerplayActivityType activityType) {
        List<Station> stations = stationDao.getByStarSystemId(targetSystemId);
        log.info("There are {} Stations at {}", stations.size(), targetSystemId);

        List<FutureWrapper<List<MiningMeritFarmResponse>>> futures = stations.stream()
            .map(station -> executorServiceBean.asyncProcess(() -> getOffers(sourceSystemId, targetSystemId, station, starSystemMap, activityType)))
            .toList();

        return futures.stream()
            .map(FutureWrapper::get)
            .map(ExecutionResult::getOrThrow)
            .flatMap(List::stream)
            .toList();
    }

    private List<MiningMeritFarmResponse> getOffers(UUID sourceSystemId, UUID targetSystemId, Station station, Map<UUID, StarSystem> starSystemMap, PowerplayActivityType activityType) {
        log.info("Loading offers for Station {} in StarSystem {}", station.getId(), targetSystemId);
        List<MiningMeritFarmResponse> result = commodityDao.getByMarketId(station.getMarketId())
            .stream()
            .filter(commodity -> COMMODITIES.contains(commodity.getItemName()))
            .filter(commodity -> commodity.getDemand() > 0)
            .map(commodity -> MiningMeritFarmResponse.builder()
                .sourceStarSystemId(sourceSystemId)
                .sourceStarSystemName(getStarSystem(sourceSystemId, starSystemMap).getStarName())
                .targetStarSystemId(targetSystemId)
                .targetStarSystemName(getStarSystem(targetSystemId, starSystemMap).getStarName())
                .stationId(station.getId())
                .stationName(station.getName())
                .commodityName(commodity.getItemName())
                .demand(commodity.getDemand())
                .price(commodity.getBuyPrice())
                .activityType(activityType)
                .build()
            )
            .toList();

        log.info("{} offers found at Station {} in StarSystem {}", result.size(), station.getId(), targetSystemId);

        return result;
    }

    private StarSystem getStarSystem(UUID starSystemId, Map<UUID, StarSystem> starSystemMap) {
        return starSystemMap.computeIfAbsent(starSystemId, starSystemDao::findByIdValidated);
    }

    private boolean hasMetallicRing(UUID starSystemId) {
        List<UUID> bodyIds = bodyDao.getByStarSystemId(starSystemId)
            .stream()
            .filter(body -> body.getType() == BodyType.PLANET)
            .map(Body::getId)
            .toList();

        return bodyRingDao.getByBodyIds(bodyIds)
            .stream()
            .anyMatch(bodyRing -> bodyRing.getType() == RingType.METALLIC);
    }

    private List<StarSystemData> extractByType(Map<PowerplayState, List<StarSystemData>> systems, PowerplayState... states) {
        return Arrays.stream(states)
            .flatMap(powerplayState -> systems.getOrDefault(powerplayState, List.of()).stream())
            .toList();
    }
}
