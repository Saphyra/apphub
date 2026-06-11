package com.github.saphyra.apphub.service.feature.elite_base.service.powerplay.merit_farm.mining.nakato_kaine;

import com.github.saphyra.apphub.api.feature.elite_base.model.merit_farm.MiningMeritFarmRequest;
import com.github.saphyra.apphub.api.feature.elite_base.model.merit_farm.MiningMeritFarmResponse;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.FutureWrapper;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.star_system_data.Power;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.star_system_data.PowerplayState;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.star_system_data.StarSystemData;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.star_system_data.StarSystemDataDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class NakatoKaineMeritMinerService {
    private final StarSystemDataDao starSystemDataDao;
    private final ExecutorServiceBean executorServiceBean;
    private final DateTimeUtil dateTimeUtil;
    private final ReinforcementOfferCollector reinforcementOfferCollector;
    private final AcquisitionOfferCollector acquisitionOfferCollector;

    public List<MiningMeritFarmResponse> getLocations(MiningMeritFarmRequest request) {
        Map<PowerplayState, List<StarSystemData>> nakatoKaineSystems = starSystemDataDao.getByControllingPower(Power.NAKATO_KAINE)
            .stream()
            .collect(Collectors.groupingBy(StarSystemData::getPowerplayState));

        List<FutureWrapper<List<MiningMeritFarmResponse>>> futures = Stream.of(
                executorServiceBean.asyncProcess(() -> reinforcementOfferCollector.getReinforcement(extractByType(nakatoKaineSystems, PowerplayState.EXPLOITED, PowerplayState.FORTIFIED, PowerplayState.STRONGHOLD))),
                executorServiceBean.asyncProcess(() -> acquisitionOfferCollector.getAcquisition(extractByType(nakatoKaineSystems, PowerplayState.FORTIFIED, PowerplayState.STRONGHOLD)))
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

    private List<StarSystemData> extractByType(Map<PowerplayState, List<StarSystemData>> systems, PowerplayState... states) {
        return Arrays.stream(states)
            .flatMap(powerplayState -> systems.getOrDefault(powerplayState, List.of()).stream())
            .toList();
    }
}
