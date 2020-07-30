package com.github.saphyra.apphub.service.notebook.dao.list_item;

import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import com.github.saphyra.apphub.lib.exception.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ListItemDaoTest {
    private static final String USER_ID_STRING = "user-id-string";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final String LIST_ITEM_ID_STRING = "list-item-id";
    private static final UUID PARENT = UUID.randomUUID();
    private static final String PARENT_STRING = "parent";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private ListItemConverter converter;

    @Mock
    private ListItemRepository repository;

    @InjectMocks
    private ListItemDao underTest;

    @Mock
    private ListItem domain;

    @Mock
    private ListItemEntity entity;

    @Before
    public void setUp() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(LIST_ITEM_ID)).willReturn(LIST_ITEM_ID_STRING);
        given(uuidConverter.convertDomain(PARENT)).willReturn(PARENT_STRING);
    }

    @Test
    public void getByUserIdAndType() {
        given(repository.getByUserIdAndType(USER_ID_STRING, ListItemType.CHECKLIST)).willReturn(Arrays.asList(entity));
        given(converter.convertEntity(Arrays.asList(entity))).willReturn(Arrays.asList(domain));

        List<ListItem> result = underTest.getByUserIdAndType(USER_ID, ListItemType.CHECKLIST);

        assertThat(result).containsExactly(domain);
    }

    @Test
    public void findById() {
        given(repository.findById(LIST_ITEM_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(entity)).willReturn(domain);

        Optional<ListItem> result = underTest.findById(LIST_ITEM_ID);

        assertThat(result).contains(domain);
    }

    @Test
    public void findByIdValidated_notFound() {
        given(repository.findById(LIST_ITEM_ID_STRING)).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.findByIdValidated(LIST_ITEM_ID));

        assertThat(ex).isInstanceOf(NotFoundException.class);
    }

    @Test
    public void getByUserIdAndParent() {
        given(repository.getByParent(PARENT_STRING)).willReturn(Arrays.asList(entity));
        given(converter.convertEntity(Arrays.asList(entity))).willReturn(Arrays.asList(domain));

        List<ListItem> result = underTest.getByParent(PARENT);

        assertThat(result).containsExactly(domain);
    }

    @Test
    public void deleteByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        underTest.deleteByUserId(USER_ID);

        verify(repository).deleteByUserId(USER_ID_STRING);
    }
}