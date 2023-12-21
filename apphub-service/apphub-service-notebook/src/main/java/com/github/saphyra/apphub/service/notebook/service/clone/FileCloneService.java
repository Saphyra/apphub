package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.service.notebook.dao.file.File;
import com.github.saphyra.apphub.service.notebook.dao.file.FileDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.service.FileFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileCloneService {
    private final FileDao fileDao;
    private final FileFactory fileFactory;

    void cloneFile(ListItem toClone, ListItem listItemClone) {
        File fileToClone = fileDao.findByParentValidated(toClone.getListItemId());

        cloneFile(toClone.getUserId(), listItemClone.getListItemId(), fileToClone);
    }

    public void cloneFile(UUID userId, UUID newParent, File fileToClone) {
        File file = fileFactory.create(userId, newParent, fileToClone.getStoredFileId());

        fileDao.save(file);
    }
}
