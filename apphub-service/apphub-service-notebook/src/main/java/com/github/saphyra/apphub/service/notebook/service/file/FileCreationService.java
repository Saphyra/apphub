package com.github.saphyra.apphub.service.notebook.service.file;

import com.github.saphyra.apphub.api.notebook.model.request.CreateFileRequest;
import com.github.saphyra.apphub.api.notebook.model.request.FileMetadata;
import com.github.saphyra.apphub.service.notebook.dao.file.File;
import com.github.saphyra.apphub.service.notebook.dao.file.FileDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemType;
import com.github.saphyra.apphub.service.notebook.service.validator.CreateFileRequestValidator;
import com.github.saphyra.apphub.service.notebook.service.FileFactory;
import com.github.saphyra.apphub.service.notebook.service.ListItemFactory;
import com.github.saphyra.apphub.service.notebook.service.StorageProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileCreationService {
    private final ListItemFactory listItemFactory;
    private final ListItemDao listItemDao;
    private final FileFactory fileFactory;
    private final StorageProxy storageProxy;
    private final FileDao fileDao;
    private final CreateFileRequestValidator createFileRequestValidator;

    public UUID createFile(UUID userId, CreateFileRequest request) {
        createFileRequestValidator.validate(request);

        FileMetadata metadata = request.getMetadata();

        UUID fileId = storageProxy.createFile(metadata.getFileName(), FilenameUtils.getExtension(metadata.getFileName()), metadata.getSize());

        ListItem listItem = listItemFactory.create(userId, request.getTitle(), request.getParent(), ListItemType.FILE);
        File file = fileFactory.create(userId, listItem.getListItemId(), fileId);

        listItemDao.save(listItem);
        fileDao.save(file);

        return fileId;
    }
}
