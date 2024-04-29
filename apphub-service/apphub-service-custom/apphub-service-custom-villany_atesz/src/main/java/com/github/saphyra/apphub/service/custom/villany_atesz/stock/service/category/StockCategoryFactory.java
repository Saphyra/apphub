package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.category;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockCategoryModel;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.category.StockCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class StockCategoryFactory {
    private final IdGenerator idGenerator;

    StockCategory create(UUID userId, StockCategoryModel request) {
        return StockCategory.builder()
            .stockCategoryId(idGenerator.randomUuid())
            .userId(userId)
            .name(request.getName())
            .measurement(request.getMeasurement())
            .build();
    }
}
