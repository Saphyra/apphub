package com.github.saphyra.apphub.service.notebook.service.validator;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.notebook.common.NotebookConstants;
import org.springframework.stereotype.Component;

@Component
public class TitleValidator {
    public void validate(String title) {
        ValidationUtil.notBlank(title, "title");
        ValidationUtil.maxLength(title, NotebookConstants.MAX_LIST_ITEM_TITLE_LENGTH, "title");
    }
}
