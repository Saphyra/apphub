package com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ChecklistItemDaoTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final String LIST_ITEM_ID_STRING = "list-item-id";
    private static final UUID CHECKLIST_ITEM_ID = UUID.randomUUID();
    private static final String CHECKLIST_ITEM_ID_STRING = "checklist-item-id";

    @Mock
    private ChecklistItemRepository repository;

    @Mock
    private ChecklistItemConverter converter;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private ChecklistItemDao underTest;

    @Mock
    private ChecklistItem domain;

    @Mock
    private ChecklistItemEntity entity;

    @Test
    void findByIdValidated_notFound() {
        given(uuidConverter.convertDomain(LIST_ITEM_ID)).willReturn(LIST_ITEM_ID_STRING);
        given(uuidConverter.convertDomain(CHECKLIST_ITEM_ID)).willReturn(CHECKLIST_ITEM_ID_STRING);
        given(repository.findById(LIST_ITEM_ID_STRING, CHECKLIST_ITEM_ID_STRING)).willReturn(Optional.empty());
        given(converter.convertEntity(Optional.empty())).willReturn(Optional.empty());

        ExceptionValidator.validateNotFoundException(() -> underTest.findByIdValidated(LIST_ITEM_ID, CHECKLIST_ITEM_ID));
    }

    @Test
    void findByIdValidated() {
        given(uuidConverter.convertDomain(LIST_ITEM_ID)).willReturn(LIST_ITEM_ID_STRING);
        given(uuidConverter.convertDomain(CHECKLIST_ITEM_ID)).willReturn(CHECKLIST_ITEM_ID_STRING);
        given(repository.findById(LIST_ITEM_ID_STRING, CHECKLIST_ITEM_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        ChecklistItem result = underTest.findByIdValidated(LIST_ITEM_ID, CHECKLIST_ITEM_ID);

        assertThat(result).isEqualTo(domain);
    }

    @Test
    void delete_single() {
        given(uuidConverter.convertDomain(LIST_ITEM_ID)).willReturn(LIST_ITEM_ID_STRING);
        given(uuidConverter.convertDomain(CHECKLIST_ITEM_ID)).willReturn(CHECKLIST_ITEM_ID_STRING);

        underTest.delete(LIST_ITEM_ID, CHECKLIST_ITEM_ID);

        then(repository).should().delete(LIST_ITEM_ID_STRING, CHECKLIST_ITEM_ID_STRING);
    }

    @Test
    void save_single() {
        given(converter.convertDomain(domain)).willReturn(entity);

        underTest.save(domain);

        then(repository).should().save(entity);
    }

    @Test
    void getByListItemId() {
        given(uuidConverter.convertDomain(LIST_ITEM_ID)).willReturn(LIST_ITEM_ID_STRING);
        given(repository.getByListItemId(LIST_ITEM_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        List<ChecklistItem> result = underTest.getByListItemId(LIST_ITEM_ID);

        assertThat(result).containsExactly(domain);
    }

    @Test
    void delete_list() {
        given(domain.getListItemId()).willReturn(LIST_ITEM_ID);
        given(domain.getChecklistItemId()).willReturn(CHECKLIST_ITEM_ID);
        given(uuidConverter.convertDomain(LIST_ITEM_ID)).willReturn(LIST_ITEM_ID_STRING);
        given(uuidConverter.convertDomain(CHECKLIST_ITEM_ID)).willReturn(CHECKLIST_ITEM_ID_STRING);

        underTest.delete(List.of(domain));

        then(repository).should().delete(List.of(new BiWrapper<>(LIST_ITEM_ID_STRING, CHECKLIST_ITEM_ID_STRING)));
    }

    @Test
    void save_list() {
        given(converter.convertDomain(List.of(domain))).willReturn(List.of(entity));

        underTest.save(List.of(domain));

        then(repository).should().save(List.of(entity));
    }
}




