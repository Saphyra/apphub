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
import Stream from "../../../common/js/collection/Stream";
import "./villany_atesz.css";
import { VILLANY_ATESZ_INDEX_GET_STOCK_ITEMS_MARKED_FOR_ACQUISITION, VILLANY_ATESZ_INDEX_TOTAL_STOCK_VALUE, VILLANY_ATESZ_INDEX_TOTAL_TOOLBOX_VALUE } from "../../../common/js/dao/endpoints/VillanyAteszEndpoints";

const VillanyAteszIndexPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const [totalStockValue, setTotalStockValue] = useState(0);
    const [totalToolboxValue, setTotalToolboxValue] = useState(0);
    const [items, setItems] = useState([]);

    useLoader({ request: VILLANY_ATESZ_INDEX_TOTAL_STOCK_VALUE.createRequest(), mapper: (response) => setTotalStockValue(response.value) });
    useLoader({ request: VILLANY_ATESZ_INDEX_TOTAL_TOOLBOX_VALUE.createRequest(), mapper: (response) => setTotalToolboxValue(response.value) });
    useLoader({ request: VILLANY_ATESZ_INDEX_GET_STOCK_ITEMS_MARKED_FOR_ACQUISITION.createRequest(), mapper: setItems });

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
        <div id="villany-atesz" className="main-page villany-atesz-main-page">
            <Header label={localizationHandler.get("page-title")} />

            <main>
                <VillanyAteszNavigation page={VillanyAteszPage.INDEX} />

                <div >
                    <div id="villany-atesz-total-stock-value" className="selectable">
                        <span>{localizationHandler.get("total-stock-value")}</span>
                        <span>: </span>
                        <span id="villany-atesz-total-stock-value-value">{totalStockValue}</span>
                        <span> Ft</span>
                    </div>

                    <fieldset id="villany-atesz-stock-items-marked-for-acquisition">
                        <legend>{localizationHandler.get("stock-items-marked-for-acquisition")}</legend>

                        <ul className="selectable">
                            {getStockItems()}
                        </ul>
                    </fieldset>

                    <div id="villany-atesz-total-toolbox-value" className="selectable">
                        <span>{localizationHandler.get("total-toolbox-value")}</span>
                        <span>: </span>
                        <span id="villany-atesz-total-toolbox-value-value">{totalToolboxValue}</span>
                        <span> Ft</span>
                    </div>
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