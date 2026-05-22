package com.github.saphyra.apphub.service.notebook.service.file;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.request.CreateFileRequest;
import com.github.saphyra.apphub.api.feature.notebook.model.request.FileMetadata;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemFactory;
import com.github.saphyra.apphub.service.notebook.service.StorageProxy;
import com.github.saphyra.apphub.service.notebook.service.validator.CreateFileRequestValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class FileCreationService {
    private final ListItemFactory listItemFactory;
    private final ListItemDao listItemDao;
    private final StorageProxy storageProxy;
    private final CreateFileRequestValidator createFileRequestValidator;
    private final UuidConverter uuidConverter;

    public UUID create(UUID userId, CreateFileRequest request, ListItemType listItemType) {
        createFileRequestValidator.validate(userId, request);

        FileMetadata metadata = request.getMetadata();
        UUID storedFileId = storageProxy.createFile(metadata.getFileName(), metadata.getSize());

        ListItem listItem = listItemFactory.create(userId, request.getParent(), request.getTitle(), listItemType, uuidConverter.convertDomain(storedFileId));

        listItemDao.save(listItem);

        return storedFileId;
    }
}
