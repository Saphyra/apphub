package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolStatus;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.Tool;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.ToolDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class SetToolStatusServiceTest {
    private static final UUID TOOL_ID = UUID.randomUUID();
    private static final LocalDate CURRENT_DATE = LocalDate.now();

    @Mock
    private ToolDao toolDao;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @InjectMocks
    private SetToolStatusService underTest;

    @Mock
    private Tool tool;

    @Test
    void getStatus_null() {
        ExceptionValidator.validateInvalidParam(() -> underTest.setStatus(TOOL_ID, null), "status", "must not be null");
    }

    @Test
    void setStatus() {
        given(toolDao.findByIdValidated(TOOL_ID)).willReturn(tool);

        underTest.setStatus(TOOL_ID, ToolStatus.DAMAGED);

        then(tool).should().setStatus(ToolStatus.DAMAGED);
        then(toolDao).should().save(tool);
        then(tool).should(times(0)).setScrappedAt(any());
    }

    @Test
    void setStatusToScrapped() {
        given(toolDao.findByIdValidated(TOOL_ID)).willReturn(tool);
        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE);

        underTest.setStatus(TOOL_ID, ToolStatus.SCRAPPED);

        then(tool).should().setStatus(ToolStatus.SCRAPPED);
        then(tool).should().setScrappedAt(CURRENT_DATE);
        then(toolDao).should().save(tool);
    }
}