package com.github.saphyra.apphub.service.notebook.dao.text;

import com.github.saphyra.dao.AbstractDao;
import org.springframework.stereotype.Component;

@Component
public class TextDao extends AbstractDao<TextEntity, Text, String, TextRepository> {
    public TextDao(TextConverter converter, TextRepository repository) {
        super(converter, repository);
    }
}
