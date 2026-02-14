package com.github.saphyra.apphub.service.custom.elite_base.dao.item.loadout;

import java.util.List;

public interface LoadoutDao {
    List<? extends Loadout> getByMarketId(Long marketId);

    void deleteAllLoadout(List<Loadout> loadouts);

    void saveAllLoadout(List<Loadout> loadouts);
}
