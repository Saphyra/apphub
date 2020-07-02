package com.github.saphyra.apphub.service.notebook.service.category.creation;

import com.github.saphyra.apphub.api.notebook.model.request.CreateCategoryRequest;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CategoryCreationService {
    private final CategoryFactory categoryFactory;
    private final CreateCategoryRequestValidator createCategoryRequestValidator;
    private final ListItemDao listItemDao;


    public void createCategory(UUID userId, CreateCategoryRequest request) {
        createCategoryRequestValidator.validate(request);

        ListItem listItem = categoryFactory.create(userId, request.getTitle(), request.getParent());
        listItemDao.save(listItem);
    }
}
