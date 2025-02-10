package com.github.saphyra.apphub.service.custom.elite_base.dao.station.material_trader_override;

import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import org.springframework.stereotype.Component;

@Component
public class MaterialTraderOverrideDao extends AbstractDao<MaterialTraderOverrideEntity, MaterialTraderOverride, String, MaterialTraderOverrideRepository> {
    MaterialTraderOverrideDao(MaterialTraderOverrideConverter converter, MaterialTraderOverrideRepository repository) {
        super(converter, repository);
    }
}
