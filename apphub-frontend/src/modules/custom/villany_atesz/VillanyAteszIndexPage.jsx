import React, { useEffect, useState } from "react";
import Footer from "../../../common/component/Footer";
import Button from "../../../common/component/input/Button";
import Header from "../../../common/component/Header";
import "./villany_atesz_page.css";
import localizationData from "./villany_atesz_page_localization.json";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import Constants from "../../../common/js/Constants";
import { ToastContainer } from "react-toastify";
import sessionChecker from "../../../common/js/SessionChecker";
import NotificationService from "../../../common/js/notification/NotificationService";
import VillanyAteszNavigation from "./navigation/VillanyAteszNavigation";
import VillanyAteszPage from "./navigation/VillanyAteszPage";
import useLoader from "../../../common/hook/Loader";
import Endpoints from "../../../common/js/dao/dao";
import Stream from "../../../common/js/collection/Stream";

const VillanyAteszIndexPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const [totalValue, setTotalValue] = useState(0);
    const [items, setItems] = useState([]);

    useLoader(Endpoints.VILLANY_ATESZ_INDEX_TOTAL_VALUE.createRequest(), (response) => setTotalValue(response.value));
    useLoader(Endpoints.VILLANY_ATESZ_INDEX_GET_STOCK_ITEMS_MARKED_FOR_ACQUISITION.createRequest(), setItems);

    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);

    const getStockItems = () => {
        return new Stream(items)
            .map(item => (
                <li
                    key={item.stockItemId}
                    className="villany-atesz-stock-item-marked-for-acquisition"
                >
                    {item.name}
                </li>
            ))
            .toList();
    }

    return (
        <div id="villany-atesz" className="main-page">
            <Header label={localizationHandler.get("page-title")} />

            <main>
                <VillanyAteszNavigation page={VillanyAteszPage.INDEX} />

                <div >
                    <div id="villany-atesz-total-stock-value">
                        <span>{localizationHandler.get("total-value")}</span>
                        <span>: </span>
                        <span id="villany-atesz-total-stock-value-value">{totalValue}</span>
                        <span> Ft</span>
                    </div>

                    <fieldset id="villany-atesz-stock-items-marked-for-acquisition">
                        <legend>{localizationHandler.get("stock-items-marked-for-acquisition")}</legend>

                        <ul>
                            {getStockItems()}
                        </ul>
                    </fieldset>
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

            <ToastContainer />
        </div>
    );
}

export default VillanyAteszIndexPage;