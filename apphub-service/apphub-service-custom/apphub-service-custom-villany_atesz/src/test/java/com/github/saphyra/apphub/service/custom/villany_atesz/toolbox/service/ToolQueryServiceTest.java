package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolStatus;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.Tool;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.ToolDao;
import com.github.saphyra.apphub.test.common.CustomAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ToolQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID TOOL_ID = UUID.randomUUID();
    private static final String BRAND = "brand";
    private static final String NAME = "name";
    private static final Integer COST = 24;
    private static final LocalDate ACQUIRED_AT = LocalDate.now();
    private static final LocalDate WARRANTY_EXPIRES_AT = ACQUIRED_AT.minusDays(1);
    private static final LocalDate SCRAPPED_AT = WARRANTY_EXPIRES_AT.minusDays(1);

    @Mock
    private ToolDao toolDao;

    @InjectMocks
    private ToolQueryService underTest;

    @Mock
    private Tool tool;

    @Test
    void getTools() {
        given(toolDao.getByUserId(USER_ID)).willReturn(List.of(tool));

        given(tool.getToolId()).willReturn(TOOL_ID);
        given(tool.getBrand()).willReturn(BRAND);
        given(tool.getName()).willReturn(NAME);
        given(tool.getCost()).willReturn(COST);
        given(tool.getAcquiredAt()).willReturn(ACQUIRED_AT);
        given(tool.getWarrantyExpiresAt()).willReturn(WARRANTY_EXPIRES_AT);
        given(tool.getStatus()).willReturn(ToolStatus.DEFAULT);
        given(tool.getScrappedAt()).willReturn(SCRAPPED_AT);

        CustomAssertions.singleListAssertThat(underTest.getTools(USER_ID))
            .returns(TOOL_ID, ToolResponse::getToolId)
            .returns(BRAND, ToolResponse::getBrand)
            .returns(NAME, ToolResponse::getName)
            .returns(COST, ToolResponse::getCost)
            .returns(ACQUIRED_AT, ToolResponse::getAcquiredAt)
            .returns(WARRANTY_EXPIRES_AT, ToolResponse::getWarrantyExpiresAt)
            .returns(ToolStatus.DEFAULT, ToolResponse::getStatus)
            .returns(SCRAPPED_AT, ToolResponse::getScrappedAt);
    }

    @Test
    void getTotalValue() {
        given(toolDao.getByUserId(USER_ID)).willReturn(List.of(tool, tool));
        given(tool.getCost()).willReturn(COST);

        assertThat(underTest.getTotalValue(USER_ID)).isEqualTo(2 * COST);
    }
}