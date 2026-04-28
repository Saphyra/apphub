package com.github.saphyra.apphub.ci.menu.settings.s3;

import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.menu.MenuOrder;
import com.github.saphyra.apphub.ci.value.EnvironmentSpecificProperties;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Builder
class S3PropertyMenuOption implements MenuOption {
    private final int order;
    private final String propertyName;
    private final S3MenuContext context;

    @Override
    public Menu getMenu() {
        return Menu.S3_PROPERTIES_MENU;
    }

    @Override
    public MenuOrder getOrder() {
        return () -> order;
    }

    @Override
    public LocalizationProvider getName() {
        String propertyValue = context.getPropertyDao()
            .getS3Configuration()
            .getOrDefault(context.getEnvironment(), new HashMap<>())
            .get(propertyName);

        return _ -> propertyName + " (%s)".formatted(propertyValue);
    }

    @Override
    public boolean process() {
        String value = context.getValidatingInputReader()
            .getInput(
                language -> LocalizedText.S3_PROPERTY_INPUT_LABEL.getLocalizedText(language).formatted(context.getEnvironment(), propertyName),
                s -> s,
                _ -> Optional.empty()
            );

        EnvironmentSpecificProperties s3Configuration = context.getPropertyDao()
            .getS3Configuration();
        Map<String, String> environmentProperties = s3Configuration.computeIfAbsent(context.getEnvironment(), k -> new HashMap<>());
        environmentProperties.put(propertyName, value);

        context.getPropertyDao()
            .save(PropertyName.S3_CONFIGURATION, s3Configuration);

        return false;
    }
}
