package com.github.saphyra.apphub.service.elite_base.message_processing.saver;

import com.github.saphyra.apphub.service.elite_base.message_processing.dao.commodity.CommodityLocation;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.last_update.LastUpdateDao;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.last_update.LastUpdateFactory;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.loadout.Loadout;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.loadout.LoadoutDao;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.loadout.LoadoutFactory;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.loadout.LoadoutType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class LoadoutSaver {
    private final LastUpdateDao lastUpdateDao;
    private final LastUpdateFactory lastUpdateFactory;
    private final LoadoutDao loadoutDao;
    private final LoadoutFactory loadoutFactory;

    public synchronized void save(LocalDateTime timestamp, LoadoutType type, CommodityLocation commodityLocation, UUID externalReference, Long marketId, List<String> items) {
        if ((isNull(commodityLocation) || isNull(externalReference)) && isNull(marketId)) {
            throw new IllegalStateException("Both commodityLocation or externalReference and marketId is null");
        }

        lastUpdateDao.save(lastUpdateFactory.create(externalReference, type, timestamp));

        Map<String, Loadout> existingLoadouts = loadoutDao.getByExternalReferenceOrMarketId(externalReference, marketId)
            .stream()
            .collect(Collectors.toMap(Loadout::getName, Function.identity()));

        List<Loadout> newItems = items.stream()
            .filter(item -> !existingLoadouts.keySet().contains(item))
            .map(name -> loadoutFactory.create(timestamp, type, commodityLocation, externalReference, marketId, name))
            .toList();

        List<Loadout> deletedItems = existingLoadouts.values()
            .stream()
            .filter(loadout -> !items.contains(loadout.getName()))
            .toList();

        loadoutDao.deleteAll(deletedItems);
        loadoutDao.saveAll(newItems);
    }
}
