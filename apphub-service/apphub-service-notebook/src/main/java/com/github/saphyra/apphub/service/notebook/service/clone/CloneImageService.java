package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.service.notebook.dao.image.Image;
import com.github.saphyra.apphub.service.notebook.dao.image.ImageDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.service.StorageProxy;
import com.github.saphyra.apphub.service.notebook.service.image.creation.ImageFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class CloneImageService {
    private final ImageDao imageDao;
    private final StorageProxy storageProxy;
    private final ImageFactory imageFactory;

    void cloneImage(ListItem toClone, ListItem listItemClone) {
        Image imageToClone = imageDao.findByParentValidated(toClone.getListItemId());

        UUID fileId = storageProxy.duplicateFile(imageToClone.getFileId());

        Image image = imageFactory.create(toClone.getUserId(), listItemClone.getListItemId(), fileId);

        imageDao.save(image);
    }
}
