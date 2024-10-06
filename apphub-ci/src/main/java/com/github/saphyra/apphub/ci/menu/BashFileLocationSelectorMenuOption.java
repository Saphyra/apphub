package com.github.saphyra.apphub.ci.menu;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.utils.ValidatingInputReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
class BashFileLocationSelectorMenuOption implements MenuOption {
    private final PropertyDao propertyDao;
    private final ValidatingInputReader validatingInputReader;

    @Override
    public Menu getMenu() {
        throw new UnsupportedOperationException("This should not be called");
    }

    @Override
    public List<Menu> getMenus() {
        return List.of(Menu.MINIKUBE_MENU, Menu.PRODUCTION_MENU, Menu.PREPROD_MENU);
    }

    @Override
    public String getCommand() {
        return "99";
    }

    @Override
    public LocalizationProvider getName() {
        return LocalizedText.SET_BASH_LOCATION;
    }

    @Override
    public boolean process() {
        String location = validatingInputReader.getInput(
            LocalizedText.BASH_FILE_LOCATION,
            input -> {
                if (isBlank(input)) {
                    return Optional.of(LocalizedText.MUST_NOT_BE_BLANK);
                }

                return Optional.empty();
            }
        );

        propertyDao.save(PropertyName.BASH_FILE_LOCATION, location);

        return false;
    }
}
