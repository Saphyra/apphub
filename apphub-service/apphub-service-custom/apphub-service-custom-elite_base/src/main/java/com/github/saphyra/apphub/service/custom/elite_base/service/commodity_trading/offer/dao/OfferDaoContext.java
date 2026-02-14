package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.dao;

import com.github.saphyra.apphub.lib.common_util.DateTimeConverter;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.custom.elite_base.common.EliteBaseProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Getter
class OfferDaoContext {
    private final JdbcTemplate jdbcTemplate;
    private final DateTimeUtil dateTimeUtil;
    private final UuidConverter uuidConverter;
    private final DateTimeConverter dateTimeConverter;
    private final EliteBaseProperties eliteBaseProperties;
}
