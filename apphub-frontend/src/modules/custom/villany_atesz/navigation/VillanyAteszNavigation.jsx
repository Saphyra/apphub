import React from "react";
import localizationData from "./navigation_localization.json";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import Button from "../../../../common/component/input/Button";
import Constants from "../../../../common/js/Constants";
import "./navigation.css";
import VillanyAteszPage from "./VillanyAteszPage";

const VillanyAteszNavigation = ({ page }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    return (
        <div id="villany-atesz-navigation">
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
        </div>
    );
}

export default VillanyAteszNavigation;