package com.github.saphyra.apphub.service.custom.villany_atesz.index;

import com.github.saphyra.apphub.api.custom.villany_atesz.server.IndexController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.custom.villany_atesz.index.service.StockTotalValueQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
class IndexControllerImpl implements IndexController {
    private final StockTotalValueQueryService stockTotalValueQueryService;

    @Override
    public OneParamResponse<Integer> getTotalValue(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the total value of their stock", accessTokenHeader.getUserId());

        return new OneParamResponse<>(stockTotalValueQueryService.getTotalValue(accessTokenHeader.getUserId()));
    }
}
