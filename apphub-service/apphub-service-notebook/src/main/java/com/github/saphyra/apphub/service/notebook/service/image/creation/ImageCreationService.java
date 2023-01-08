package com.github.saphyra.apphub.service.notebook.service.image.creation;

import com.github.saphyra.apphub.api.notebook.model.request.CreateImageRequest;
import com.github.saphyra.apphub.service.notebook.dao.image.Image;
import com.github.saphyra.apphub.service.notebook.dao.image.ImageDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemType;
import com.github.saphyra.apphub.service.notebook.service.ListItemFactory;
import com.github.saphyra.apphub.service.notebook.service.ListItemRequestValidator;
import com.github.saphyra.apphub.service.notebook.service.StorageProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ImageCreationService {
    private final ListItemRequestValidator listItemRequestValidator;
    private final ListItemFactory listItemFactory;
    private final ListItemDao listItemDao;
    private final ImageFactory imageFactory;
    private final StorageProxy storageProxy;
    private final ImageDao imageDao;

    public UUID createImage(UUID userId, CreateImageRequest request) {
        listItemRequestValidator.validate(request.getTitle(), request.getParent());

        UUID fileId = storageProxy.createFile(request.getFileName(), FilenameUtils.getExtension(request.getFileName()), request.getSize());

        ListItem listItem = listItemFactory.create(userId, request.getTitle(), request.getParent(), ListItemType.IMAGE);
        Image image = imageFactory.create(userId, listItem.getListItemId(), fileId);

        listItemDao.save(listItem);
        imageDao.save(image);

        return fileId;
    }
}
