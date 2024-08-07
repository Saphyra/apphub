import React from "react";
import localizationData from "./navigation_localization.json";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import Button from "../../../../common/component/input/Button";
import Constants from "../../../../common/js/Constants";
import "./navigation.css";
import VillanyAteszPage from "./VillanyAteszPage";

const VillanyAteszNavigation = ({ page, customs }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    return (
        <div id="villany-atesz-navigation">
            <span id="villany-atesz-navigation-defaults">
                {page !== VillanyAteszPage.INDEX &&
                    <Button
                        id="villany-atesz-index"
                        onclick={() => window.location.href = Constants.VILLANY_ATESZ_INDEX_PAGE}
                        label={localizationHandler.get("index")}
                    />
                }

                {page !== VillanyAteszPage.CONTACTS &&
                    <Button
                        id="villany-atesz-contacts"
                        onclick={() => window.location.href = Constants.VILLANY_ATESZ_CONTACTS_PAGE}
                        label={localizationHandler.get("contacts")}
                    />
                }

                {page !== VillanyAteszPage.STOCK &&
                    <Button
                        id="villany-atesz-stock"
                        onclick={() => window.location.href = Constants.VILLANY_ATESZ_STOCK_PAGE}
                        label={localizationHandler.get("stock")}
                    />
                }

                {page !== VillanyAteszPage.TOOLBOX &&
                    <Button
                        id="villany-atesz-toolbox"
                        onclick={() => window.location.href = Constants.VILLANY_ATESZ_TOOLBOX_PAGE}
                        label={localizationHandler.get("toolbox")}
                    />
                }
            </span>

            <span id="villany-atesz-navigation-customs">{customs}</span>
        </div>
    );
}

export default VillanyAteszNavigation;