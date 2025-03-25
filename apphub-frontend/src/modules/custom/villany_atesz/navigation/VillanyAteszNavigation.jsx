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
                <Button
                    id="villany-atesz-index"
                    onclick={() => window.location.href = Constants.VILLANY_ATESZ_INDEX_PAGE}
                    label={localizationHandler.get("index")}
                    className={page === VillanyAteszPage.INDEX ? "opened" : ""}
                />

                <Button
                    id="villany-atesz-contacts"
                    onclick={() => window.location.href = Constants.VILLANY_ATESZ_CONTACTS_PAGE}
                    label={localizationHandler.get("contacts")}
                    className={page === VillanyAteszPage.CONTACTS ? "opened" : ""}
                />

                <Button
                    id="villany-atesz-stock"
                    onclick={() => window.location.href = Constants.VILLANY_ATESZ_STOCK_PAGE}
                    label={localizationHandler.get("stock")}
                    className={page === VillanyAteszPage.STOCK ? "opened" : ""}
                />

                <Button
                    id="villany-atesz-toolbox"
                    onclick={() => window.location.href = Constants.VILLANY_ATESZ_TOOLBOX_PAGE}
                    label={localizationHandler.get("toolbox")}
                    className={page === VillanyAteszPage.TOOLBOX ? "opened" : ""}
                />

                <Button
                    id="villany-atesz-commissions"
                    onclick={() => window.location.href = Constants.VILLANY_ATESZ_COMMISSIONS_PAGE}
                    label={localizationHandler.get("commissions")}
                    className={page === VillanyAteszPage.COMMISSIONS ? "opened" : ""}
                />
            </span>

            <span id="villany-atesz-navigation-customs">{customs}</span>
        </div>
    );
}

export default VillanyAteszNavigation;