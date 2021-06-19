package com.github.saphyra.apphub.service.notebook.dao.checklist_item;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ChecklistItemDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";
    private static final UUID PARENT = UUID.randomUUID();
    private static final String PARENT_STRING = "parent";
    private static final UUID CHECKLIST_ITEM_ID = UUID.randomUUID();
    private static final String CHECKLIST_ITEM_ID_STRING = "checklist-item-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private ChecklistItemRepository repository;

    @Mock
    private ChecklistItemConverter converter;

    @InjectMocks
    private ChecklistItemDao underTest;

    @Mock
    private ChecklistItem domain;

    @Mock
    private ChecklistItemEntity entity;

    @Test
    public void deleteByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        underTest.deleteByUserId(USER_ID);

        verify(repository).deleteByUserId(USER_ID_STRING);
    }

    @Test
    public void getByParent() {
        given(uuidConverter.convertDomain(PARENT)).willReturn(PARENT_STRING);
        given(repository.getByParent(PARENT_STRING)).willReturn(Arrays.asList(entity));
        given(converter.convertEntity(Arrays.asList(entity))).willReturn(Arrays.asList(domain));

        List<ChecklistItem> result = underTest.getByParent(PARENT);

        assertThat(result).containsExactly(domain);
    }

    @Test
    public void findByIdValidated_notFound() {
        given(uuidConverter.convertDomain(CHECKLIST_ITEM_ID)).willReturn(CHECKLIST_ITEM_ID_STRING);

        given(repository.findById(CHECKLIST_ITEM_ID_STRING)).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.findByIdValidated(CHECKLIST_ITEM_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.LIST_ITEM_NOT_FOUND);
    }

    @Test
    public void findByIdValidated() {
        given(uuidConverter.convertDomain(CHECKLIST_ITEM_ID)).willReturn(CHECKLIST_ITEM_ID_STRING);

        given(repository.findById(CHECKLIST_ITEM_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        ChecklistItem result = underTest.findByIdValidated(CHECKLIST_ITEM_ID);

        assertThat(result).isEqualTo(domain);
    }
}