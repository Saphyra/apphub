package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.production_order;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProductionOrderModel;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProductionOrderDao extends AbstractDao<ProductionOrderEntity, ProductionOrderModel, String, ProductionOrderRepository> {
    private final UuidConverter uuidConverter;

    public ProductionOrderDao(ProductionOrderConverter converter, ProductionOrderRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public void deleteByGameId(UUID gameId) {
        repository.deleteByGameId(uuidConverter.convertDomain(gameId));
    }

    public Optional<ProductionOrderModel> findById(UUID productionOrderId) {
        return findById(uuidConverter.convertDomain(productionOrderId));
    }

    public List<ProductionOrderModel> getByLocation(UUID location) {
        return converter.convertEntity(repository.getByLocation(uuidConverter.convertDomain(location)));
    }

    public void deleteById(UUID productionOrderId) {
        deleteById(uuidConverter.convertDomain(productionOrderId));
    }
}
