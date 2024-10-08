import React, { useEffect, useState } from "react";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import localizationData from "./villany_atesz_stock_page_localization.json";
import "./villany_atesz_stock_page.css";
import sessionChecker from "../../../../common/js/SessionChecker";
import NotificationService from "../../../../common/js/notification/NotificationService";
import Header from "../../../../common/component/Header";
import VillanyAteszNavigation from "../navigation/VillanyAteszNavigation";
import VillanyAteszPage from "../navigation/VillanyAteszPage";
import Footer from "../../../../common/component/Footer";
import Button from "../../../../common/component/input/Button";
import Constants from "../../../../common/js/Constants";
import { ToastContainer } from "react-toastify";
import StockTab from "./VillanyAteszStockTab";
import VillanyAteszStockCategories from "./categories/VillanyAteszStockCategories";
import ConfirmationDialog from "../../../../common/component/confirmation_dialog/ConfirmationDialog";
import VillanyAteszStockNewItem from "./new_item/VillanyAteszStockNewItem";
import VillanyAteszStockOverview from "./overview/VillanyAteszStockOverview";
import VillanyAteszStockAcquisition from "./acquisition/VillanyAteszStockAcquisition";
import VillanyAteszStockInventory from "./inventory/VillanyAteszStockInventory";

const VillanyAteszStockPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const [openedTab, setOpenedTab] = useState(sessionStorage.villanyAteszStockOpenedTab || StockTab.OVERVIEW);
    const [confirmationDialogData, setConfirmationDialogData] = useState(null);

    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);

    const updateOpenedTab = (tab) => {
        sessionStorage.villanyAteszStockOpenedTab = tab;

        setOpenedTab(tab);
    }

    const getNavButtons = () => {
        return [
            <Button
                key="overview"
                id="villany-atesz-stock-navigation-overview-button"
                className={openedTab === StockTab.OVERVIEW ? "opened" : ""}
                onclick={() => updateOpenedTab(StockTab.OVERVIEW)}
                label={localizationHandler.get("overview")}
            />,
            <Button
                key="categories"
                id="villany-atesz-stock-navigation-categories-button"
                className={openedTab === StockTab.CATEGORIES ? "opened" : ""}
                onclick={() => updateOpenedTab(StockTab.CATEGORIES)}
                label={localizationHandler.get("categories")}
            />,
            <Button
                key="new-item"
                id="villany-atesz-stock-navigation-new-item-button"
                className={openedTab === StockTab.NEW_ITEM ? "opened" : ""}
                onclick={() => updateOpenedTab(StockTab.NEW_ITEM)}
                label={localizationHandler.get("new-item")}
            />,
            <Button
                key="acquisition"
                id="villany-atesz-stock-navigation-acquisition-button"
                className={openedTab === StockTab.ACQUISITION ? "opened" : ""}
                onclick={() => updateOpenedTab(StockTab.ACQUISITION)}
                label={localizationHandler.get("acquisition")}
            />,
            <Button
                key="inventory"
                id="villany-atesz-stock-navigation-inventory-button"
                className={openedTab === StockTab.INVENTORY ? "opened" : ""}
                onclick={() => updateOpenedTab(StockTab.INVENTORY)}
                label={localizationHandler.get("inventory")}
            />,
        ];
    }

    const getContent = () => {
        switch (openedTab) {
            case StockTab.OVERVIEW:
                return <VillanyAteszStockOverview
                    setConfirmationDialogData={setConfirmationDialogData}
                />
            case StockTab.CATEGORIES:
                return <VillanyAteszStockCategories
                    setConfirmationDialogData={setConfirmationDialogData}
                />
            case StockTab.NEW_ITEM:
                return <VillanyAteszStockNewItem />
            case StockTab.ACQUISITION:
                return <VillanyAteszStockAcquisition
                    setConfirmationDialogData={setConfirmationDialogData}
                />
            case StockTab.INVENTORY:
                return <VillanyAteszStockInventory
                    setConfirmationDialogData={setConfirmationDialogData}
                />
            default:
                return "Unhandled StockTab: " + openedTab;
        }
    }

    return (
        <div id="villany-atesz-stock" className="main-page villany-atesz-main-page">
            <Header label={localizationHandler.get("page-title")} />

            <main>
                <VillanyAteszNavigation
                    page={VillanyAteszPage.STOCK}
                    customs={getNavButtons()}
                />

                <div id="villany-atesz-stock-content">
                    {getContent()}
                </div>
            </main>

            <Footer
                rightButtons={
                    <Button
                        id="villany-atesz-home-button"
                        onclick={() => window.location.href = Constants.MODULES_PAGE}
                        label={localizationHandler.get("home")}
                    />
                }
            />

            {confirmationDialogData &&
                <ConfirmationDialog
                    id={confirmationDialogData.id}
                    title={confirmationDialogData.title}
                    content={confirmationDialogData.content}
                    choices={confirmationDialogData.choices}
                />
            }

            <ToastContainer />
        </div>
    );
}

export default VillanyAteszStockPage;