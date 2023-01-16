package com.github.saphyra.apphub.service.notebook.dao.table.row;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ChecklistTableRowDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";
    private static final String PARENT_STRING = "parent";
    private static final UUID PARENT = UUID.randomUUID();
    private static final int ROW_INDEX = 345;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private ChecklistTableRowConverter converter;

    @Mock
    private ChecklistTableRowRepository repository;

    @InjectMocks
    private ChecklistTableRowDao underTest;

    @Mock
    private ChecklistTableRow domain;

    @Mock
    private ChecklistTableRowEntity entity;

    @Test
    public void deleteByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        underTest.deleteByUserId(USER_ID);

        verify(repository).deleteByUserId(USER_ID_STRING);
    }

    @Test
    public void findByParentAndRowIndex() {
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));
        given(repository.findByParentAndRowIndex(PARENT_STRING, ROW_INDEX)).willReturn(Optional.of(entity));
        given(uuidConverter.convertDomain(PARENT)).willReturn(PARENT_STRING);

        Optional<ChecklistTableRow> result = underTest.findByParentAndRowIndex(PARENT, ROW_INDEX);

        assertThat(result).contains(domain);
    }

    @Test
    public void getByParent() {
        given(uuidConverter.convertDomain(PARENT)).willReturn(PARENT_STRING);
        given(repository.getByParent(PARENT_STRING)).willReturn(Arrays.asList(entity));
        given(converter.convertEntity(Arrays.asList(entity))).willReturn(Arrays.asList(domain));

        List<ChecklistTableRow> result = underTest.getByParent(PARENT);

        assertThat(result).containsExactly(domain);
    }

    @Test
    public void deleteByParentAndRowIndexGreaterThanEqual() {
        given(uuidConverter.convertDomain(PARENT)).willReturn(PARENT_STRING);

        underTest.deleteByParentAndRowIndexGreaterThanEqual(PARENT, ROW_INDEX);

        verify(repository).deleteByParentAndRowIndexGreaterThanEqual(PARENT_STRING, ROW_INDEX);
    }

    @Test
    public void deleteByParent() {
        given(uuidConverter.convertDomain(PARENT)).willReturn(PARENT_STRING);

        underTest.deleteByParent(PARENT);

        verify(repository).deleteByParent(PARENT_STRING);
    }
}