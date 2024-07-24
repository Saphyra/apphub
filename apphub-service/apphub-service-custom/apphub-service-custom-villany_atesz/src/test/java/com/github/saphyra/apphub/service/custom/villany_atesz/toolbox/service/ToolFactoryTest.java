package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.CreateToolRequest;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolStatus;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.Tool;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ToolFactoryTest {
    private static final UUID TOOL_ID = UUID.randomUUID();
    private static final String BRAND = "brand";
    private static final String NAME = "name";
    private static final Integer COST = 234;
    private static final LocalDate ACQUIRED_AT = LocalDate.now();
    private static final LocalDate WARRANTY_EXPIRES_AT = ACQUIRED_AT.minusDays(1);
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private ToolFactory underTest;

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(TOOL_ID);

        CreateToolRequest request = CreateToolRequest.builder()
            .brand(BRAND)
            .name(NAME)
            .cost(COST)
            .acquiredAt(ACQUIRED_AT)
            .warrantyExpiresAt(WARRANTY_EXPIRES_AT)
            .build();

        assertThat(underTest.create(USER_ID, request))
            .returns(TOOL_ID, Tool::getToolId)
            .returns(USER_ID, Tool::getUserId)
            .returns(BRAND, Tool::getBrand)
            .returns(NAME, Tool::getName)
            .returns(COST, Tool::getCost)
            .returns(ACQUIRED_AT, Tool::getAcquiredAt)
            .returns(ToolStatus.DEFAULT, Tool::getStatus)
            .returns(WARRANTY_EXPIRES_AT, Tool::getWarrantyExpiresAt)
            .returns(null, Tool::getScrappedAt);
    }
}