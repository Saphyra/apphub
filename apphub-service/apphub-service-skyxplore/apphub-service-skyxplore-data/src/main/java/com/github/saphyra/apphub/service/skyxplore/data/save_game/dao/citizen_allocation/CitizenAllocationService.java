package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.citizen_allocation;

import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenAllocationModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
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
public class CitizenAllocationService implements GameItemService {
    private final CitizenAllocationDao dao;

    @Override
    public GameItemType getType() {
        return GameItemType.CITIZEN_ALLOCATION;
    }

    @Override
    public void deleteByGameId(UUID gameId) {
        dao.deleteByGameId(gameId);
    }

    @Override
    public void save(List<GameItem> gameItems) {
        List<CitizenAllocationModel> models = gameItems.stream()
            .filter(gameItem -> gameItem instanceof CitizenAllocationModel)
            .map(gameItem -> (CitizenAllocationModel) gameItem)
            .collect(Collectors.toList());

        dao.saveAll(models);
    }

    @Override
    public Optional<? extends GameItem> findById(UUID id) {
        throw new UnsupportedOperationException("Deprecated");
    }

    @Override
    public List<? extends GameItem> getByParent(UUID parent) {
        throw new UnsupportedOperationException("Deprecated");
    }

    @Override
    public void deleteById(UUID citizenAllocationId) {
        dao.deleteById(citizenAllocationId);
    }
}
