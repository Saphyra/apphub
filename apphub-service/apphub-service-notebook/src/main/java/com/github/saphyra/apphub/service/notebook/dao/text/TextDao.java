package com.github.saphyra.apphub.service.notebook.dao.text;

import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import com.github.saphyra.dao.AbstractDao;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
//TODO unit test
public class TextDao extends AbstractDao<TextEntity, Text, String, TextRepository> {
    private final UuidConverter uuidConverter;

    public TextDao(TextConverter converter, TextRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public void deleteByParent(UUID parent) {
        repository.deleteByParent(uuidConverter.convertDomain(parent));
    }
}
