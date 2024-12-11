package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource;

import com.github.saphyra.apphub.lib.data.ValidationAbstractDataService;
import com.github.saphyra.apphub.lib.data.loader.ContentLoaderFactory;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.util.List;
import java.util.stream.Collectors;


@Component
@NoArgsConstructor
public class ResourceDataService extends ValidationAbstractDataService<String, ResourceData> {
    public ResourceDataService(ContentLoaderFactory contentLoaderFactory, ResourceValidator resourceValidator) {
        super("/data/resource", contentLoaderFactory, resourceValidator);
    }

    @Override
    @PostConstruct
    public void init() {
        load(ResourceData.class);
    }

    @Override
    public void addItem(ResourceData content, String fileName) {
        put(content.getId(), content);
    }

    public List<ResourceData> getByStorageType(StorageType storageType) {
        return values()
            .stream()
            .filter(resourceData -> resourceData.getStorageType().equals(storageType))
            .collect(Collectors.toList());
    }
}
