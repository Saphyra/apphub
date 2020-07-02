package com.github.saphyra.apphub.service.notebook.dao.list_item;

import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import com.github.saphyra.converter.Converter;
import com.github.saphyra.dao.AbstractDao;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
//TODO unit test
public class ListItemDao extends AbstractDao<ListItemEntity, ListItem, String, ListItemRepository> {
    private final UuidConverter uuidConverter;

    public ListItemDao(Converter<ListItemEntity, ListItem> converter, ListItemRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public List<ListItem> getByUserIdAndType(UUID userId, ListItemType type) {
        return converter.convertEntity(repository.getByUserIdAndType(uuidConverter.convertDomain(userId), type));
    }

    public Optional<ListItem> findById(UUID listItemId) {
        return findById(uuidConverter.convertDomain(listItemId));
    }
}
