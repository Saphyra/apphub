package com.github.saphyra.apphub.service.notebook.service.text;

import com.github.saphyra.apphub.api.notebook.model.response.TextResponse;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.text.Text;
import com.github.saphyra.apphub.service.notebook.dao.text.TextDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Component
public class TextQueryService {
    private final ListItemDao listItemDao;
    private final TextDao textDao;

    public TextResponse getTextResponse(UUID textId) {
        ListItem listItem = listItemDao.findByIdValidated(textId);
        Text text = textDao.findByParentValidated(textId);

        return TextResponse.builder()
            .textId(textId)
            .title(listItem.getTitle())
            .content(text.getContent())
            .build();
    }
}
