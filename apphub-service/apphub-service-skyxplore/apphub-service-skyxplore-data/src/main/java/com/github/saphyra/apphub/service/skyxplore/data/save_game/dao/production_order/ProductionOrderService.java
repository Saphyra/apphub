package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.production_order;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProductionOrderModel;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ProductionOrderService implements GameItemService {
    private final ProductionOrderDao productionOrderDao;
    private final ProductionOrderModelValidator productionOrderModelValidator;

    @Override
    public void deleteByGameId(UUID gameId) {
        log.info("Deleting {}s by gameId {}", getClass().getSimpleName(), gameId);
        productionOrderDao.deleteByGameId(gameId);
    }

    @Override
    public GameItemType getType() {
        return GameItemType.PRODUCTION_ORDER;
    }

    @Override
    public void save(List<GameItem> gameItems) {
        List<ProductionOrderModel> models = gameItems.stream()
            .filter(gameItem -> gameItem instanceof ProductionOrderModel)
            .map(gameItem -> (ProductionOrderModel) gameItem)
            .peek(productionOrderModelValidator::validate)
            .collect(Collectors.toList());


        productionOrderDao.saveAll(models);
    }

    @Override
    public Optional<ProductionOrderModel> findById(UUID id) {
        return productionOrderDao.findById(id);
    }

    @Override
    public List<ProductionOrderModel> getByParent(UUID parent) {
        return productionOrderDao.getByLocation(parent);
    }

    @Override
    public void deleteById(UUID id) {
        productionOrderDao.deleteById(id);
    }
}
