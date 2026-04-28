package com.github.saphyra.apphub.ci.menu.settings.s3;

import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.menu.MenuOrder;
import com.github.saphyra.apphub.ci.value.EnvironmentSpecificProperties;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
public class S3DeleteEnvironmentMenuOption implements MenuOption {
    private final int order;
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
        return LocalizedText.S3_DELETE_ENVIRONMENT_SETTINGS;
    }

    @Override
    public boolean process() {
        boolean confirmation = context.getValidatingInputReader()
            .getInput(
                language -> LocalizedText.S3_CONFIRM_ENVIRONMENT_DELETION.getLocalizedText(language).formatted(context.getEnvironment()),
                s -> context.getBooleanParser().parse(s),
                aBoolean -> isNull(aBoolean) ? Optional.of(LocalizedText.PROVIDE_YES_OR_NO) : Optional.empty()
            );

        if (confirmation) {
            EnvironmentSpecificProperties s3Configuration = context.getPropertyDao()
                .getS3Configuration();

            s3Configuration.remove(context.getEnvironment());
            context.getPropertyDao()
                .save(PropertyName.S3_CONFIGURATION, s3Configuration);
        }

        return false;
    }
}
