package com.github.saphyra.apphub.service.feature.elite_base.dao.item.trading;

import java.util.UUID;

public interface Tradeable {
    UUID getExternalReference();

    String getItemName();
}
