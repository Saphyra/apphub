package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.request.CreateTextRequest;
import com.github.saphyra.apphub.api.notebook.model.request.EditTextRequest;
import com.github.saphyra.apphub.api.notebook.model.response.TextResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.notebook.service.text.EditTextService;
import com.github.saphyra.apphub.service.notebook.service.text.TextQueryService;
import com.github.saphyra.apphub.service.notebook.service.text.creation.TextCreationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ContentControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private EditTextService editTextService;

    @Mock
    private TextCreationService textCreationService;

    @Mock
    private TextQueryService textQueryService;

    @InjectMocks
    private TextControllerImpl underTest;

    @Mock
    private CreateTextRequest createTextRequest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private TextResponse textResponse;

    @Mock
    private EditTextRequest editTextRequest;

    @Before
    public void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    public void createText() {
        given(textCreationService.create(createTextRequest, USER_ID)).willReturn(LIST_ITEM_ID);

        OneParamResponse<UUID> result = underTest.createText(createTextRequest, accessTokenHeader);

        assertThat(result.getValue()).isEqualTo(LIST_ITEM_ID);
    }

    @Test
    public void getText() {
        given(textQueryService.getTextResponse(LIST_ITEM_ID)).willReturn(textResponse);

        TextResponse result = underTest.getText(LIST_ITEM_ID);

        assertThat(result).isEqualTo(textResponse);
    }

    @Test
    public void editText() {
        underTest.editText(editTextRequest, LIST_ITEM_ID);

        verify(editTextService).editText(LIST_ITEM_ID, editTextRequest);
    }
}