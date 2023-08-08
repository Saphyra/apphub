package com.github.saphyra.apphub.integraton.frontend.modules;

import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.structure.api.modules.Favorite;
import com.github.saphyra.apphub.integration.structure.api.modules.Module;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FavoriteTest extends SeleniumTest {
    @Test
    public void addToFavorites() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        //Add to favorite
        Module module = getModule(driver);
        module.addFavorite();
        Favorite favorite = getFavorite(driver);
        assertThat(module.getModuleName()).isEqualTo(favorite.getModuleName());

        //Remove by favorite button
        favorite.removeFromFavorites();
        List<Favorite> favorites = AwaitilityWrapper.getListWithWait(() -> ModulesPageActions.getFavorites(driver), List::isEmpty);
        assertThat(favorites).isEmpty();
        module = getModule(driver);
        assertThat(module.isFavorite()).isFalse();

        //Remove by module button
        module = getModule(driver);
        module.addFavorite();
        module = AwaitilityWrapper.getListWithWait(() -> ModulesPageActions.getModules(driver), r -> r.stream().anyMatch(Module::isFavorite))
            .stream()
            .filter(Module::isFavorite)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No module found"));
        module.removeFavorite();
        favorites = AwaitilityWrapper.getListWithWait(() -> ModulesPageActions.getFavorites(driver), List::isEmpty);
        assertThat(favorites).isEmpty();
        module = getModule(driver);
        assertThat(module.isFavorite()).isFalse();
    }

    private Module getModule(WebDriver driver) {
        return AwaitilityWrapper.getListWithWait(() -> ModulesPageActions.getModules(driver), modules -> !modules.isEmpty())
            .stream()
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No module found"));
    }

    private Favorite getFavorite(WebDriver driver) {
        return AwaitilityWrapper.getWithWait(() -> ModulesPageActions.getFavorites(driver), favorites -> favorites.size() == 1)
            .orElse(Collections.emptyList())
            .stream()
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No favorite found"));
    }
}
