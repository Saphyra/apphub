package com.github.saphyra.apphub.service.notebook.service.image.deletion;

import com.github.saphyra.apphub.service.notebook.dao.image.Image;
import com.github.saphyra.apphub.service.notebook.dao.image.ImageDao;
import com.github.saphyra.apphub.service.notebook.service.StorageProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ImageDeletionService {
    private final ImageDao imageDao;
    private final StorageProxy storageProxy;

    public void deleteImage(UUID listItemId) {
        Image image = imageDao.findByParentValidated(listItemId);
        storageProxy.deleteFile(image.getFileId());
        imageDao.delete(image);
    }
}
