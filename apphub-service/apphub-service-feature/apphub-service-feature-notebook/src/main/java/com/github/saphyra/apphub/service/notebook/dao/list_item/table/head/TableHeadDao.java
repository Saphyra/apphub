package com.github.saphyra.apphub.service.notebook.dao.list_item.table.head;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class TableHeadDao {
    private final TableHeadConverter converter;
    private final TableHeadRepository repository;
    private final UuidConverter uuidConverter;

    public void save(UUID listItemId, List<TableHead> tableHeads) {
        repository.save(converter.convertDomain(listItemId, tableHeads));
    }

    /**
     * Deletes all {@link TableHead}s of the given Table
     */
    public void delete(UUID listItemId) {
        repository.delete(uuidConverter.convertDomain(listItemId));
    }

    public List<TableHead> findByListItemIdValidated(UUID listItemId) {
        TableHeadEntity entity = repository.findByListItemId(uuidConverter.convertDomain(listItemId))
            .orElseThrow(() -> ExceptionFactory.notFound("TableHead not found for Table " + listItemId));
        return converter.convertEntity(entity);
    }
}
