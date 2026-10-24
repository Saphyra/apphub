package com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.trading.commodity;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.common_util.dao.ListCachedBufferedDao;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.ItemDomainId;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.trading.Tradeable;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.trading.TradeableDao;
import com.google.common.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
@Slf4j
public class CommodityDao extends ListCachedBufferedDao<CommodityEntity, Commodity, String, Long, ItemDomainId, CommodityRepository> implements TradeableDao {
    private final UuidConverter uuidConverter;

    CommodityDao(
        CommodityConverter converter,
        CommodityRepository repository,
        Cache<Long, List<Commodity>> readCache,
        CommodityWriteBuffer writeBuffer,
        CommodityDeleteBuffer deleteBuffer,
        UuidConverter uuidConverter
    ) {
        super(converter, repository, readCache, writeBuffer, deleteBuffer);
        this.uuidConverter = uuidConverter;
    }

    @Override
    protected Long getCacheKey(Commodity commodity) {
        return commodity.getMarketId();
    }

    @Override
    protected ItemDomainId toDomainId(String id) {
        return repository.findById(id)
            .map(converter::convertEntity)
            .map(this::getDomainId)
            .orElseGet(() -> ItemDomainId.builder().entityId(id).build());
    }

    @Override
    protected ItemDomainId getDomainId(Commodity commodity) {
        return ItemDomainId.builder()
            .externalReference(commodity.getExternalReference())
            .itemName(commodity.getItemName())
            .locationType(commodity.getLocationType())
            .build();
    }

    @Override
    protected boolean matchesWithId(ItemDomainId domainId, Commodity commodity) {
        return Objects.equals(commodity.getExternalReference(), domainId.getExternalReference())
            && Objects.equals(commodity.getItemName(), domainId.getItemName())
            && Objects.equals(commodity.getLocationType(), domainId.getLocationType());
    }

    @Override
    public List<Commodity> getByMarketId(Long marketId) {
        return searchList(marketId, () -> repository.getByMarketId(marketId));
    }

    @Override
    public void deleteAllTradeables(List<Tradeable> tradeables) {
        List<Commodity> commodities = tradeables.stream()
            .map(tradeable -> (Commodity) tradeable)
            .toList();
        deleteAll(commodities);
    }

    @Override
    public void saveAll(List<Tradeable> tradeables) {
        List<Commodity> commodities = tradeables.stream()
            .map(tradeable -> (Commodity) tradeable)
            .toList();
        saveAll(commodities);
    }

    public List<Commodity> getByIds(List<BiWrapper<UUID, String>> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }

        List<String> externalReferences = ids.stream()
            .map(BiWrapper::getEntity1)
            .distinct()
            .map(uuidConverter::convertDomain)
            .toList();
        List<String> itemNames = ids.stream()
            .map(BiWrapper::getEntity2)
            .distinct()
            .toList();

        List<CommodityEntity> entities = new java.util.ArrayList<>();
        entities.addAll(repository.getByStationIdInAndItemNameIn(externalReferences, itemNames));
        entities.addAll(repository.getByFleetCarrierIdInAndItemNameIn(externalReferences, itemNames));

        List<Commodity> commodities = converter.convertEntity(entities)
            .stream()
            .filter(commodity -> ids.stream().anyMatch(id ->
                Objects.equals(id.getEntity1(), commodity.getExternalReference())
                    && Objects.equals(id.getEntity2(), commodity.getItemName())))
            .toList();

        return syncWithCaches(commodities);
    }
}
