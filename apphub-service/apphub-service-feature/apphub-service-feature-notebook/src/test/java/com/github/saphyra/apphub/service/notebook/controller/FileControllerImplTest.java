package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.request.CreateFileRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.notebook.service.file.FileCreationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class FileControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID STORED_FILE_ID = UUID.randomUUID();

    @Mock
    private FileCreationService fileCreationService;

    @InjectMocks
    private FileControllerImpl underTest;

    @Mock
    private AccessToken accessToken;

    @Mock
    private CreateFileRequest createFileRequest;

    @BeforeEach
    public void setUp() {
        given(accessToken.getUserId()).willReturn(USER_ID);
    }

    @Test
    public void createImage() {
        given(fileCreationService.create(USER_ID, createFileRequest, ListItemType.FILE)).willReturn(STORED_FILE_ID);

        OneParamResponse<UUID> result = underTest.createFile(createFileRequest, accessToken);

        assertThat(result.getValue()).isEqualTo(STORED_FILE_ID);
    }
}