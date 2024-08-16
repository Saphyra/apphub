import React, { useEffect, useState } from "react";
import localizationData from "./villany_atesz_stock_acquisition_history_localization.json";
import LocalizationHandler from "../../../../../../common/js/LocalizationHandler";
import useLoader from "../../../../../../common/hook/Loader";
import Endpoints from "../../../../../../common/js/dao/dao";
import Utils from "../../../../../../common/js/Utils";
import SelectInput, { SelectOption } from "../../../../../../common/component/input/SelectInput";
import Stream from "../../../../../../common/js/collection/Stream";
import AcquisitionHistoryItem from "./AcquisitionHistoryItem";
import "./villany_atesz_stock_acquisition_history.css";

const VillanyAteszStockAcquisitionHistory = ({ }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [dates, setDates] = useState([]);
    const [selectedDate, setSelectedDate] = useState("");
    const [acquisitions, setAcquisitions] = useState([]);

    useLoader(Endpoints.VILLANY_ATESZ_GET_ACQUISITION_DATES.createRequest(), setDates);

    useEffect(() => loadAcquisitions(), [selectedDate]);
    useEffect(() => setDefaultSelectedDate(), [dates]);

    const loadAcquisitions = () => {
        if (Utils.isBlank(selectedDate)) {
            return;
        }

        const fetch = async () => {
            const response = await Endpoints.VILLANY_ATESZ_GET_ACQUISITIONS.createRequest(null, { acquiredAt: selectedDate })
                .send();
            setAcquisitions(response);
        }
        fetch();
    }

    const setDefaultSelectedDate = () => {
        if (!Utils.isBlank(selectedDate)) {
            return;
        }

        if (dates.length === 0) {
            return;
        }

        setSelectedDate(getOrderedDates()[0]);
    }

    const getOrderedDates = () => {
        return new Stream(dates)
            .sorted((a, b) => b.localeCompare(a))
            .toList();
    }

    const getAvailableDates = () => {
        return new Stream(getOrderedDates())
            .map(date => new SelectOption(date, date))
            .toList();
    }

    const getContentForSelectedDate = () => {
        const padding = new Stream(acquisitions)
            .map(item => item.amount)
            .max(Utils.numberOfDigits)
            .orElse(0);


        return new Stream(acquisitions)
            .sorted((a, b) => a.stockItemName.localeCompare(b.stockItemName))
            .map(item => <AcquisitionHistoryItem
                key={item.acquisitionId}
                item={item}
                padding={padding}
            />
            )
            .toList();
    }

    return (
        <div id="villany-atesz-stock-acquisition-history">
            <fieldset>
                <legend>{localizationHandler.get("select-date")}</legend>

                <SelectInput
                    id="villany-atesz-stock-acquisition-history-date-input"
                    onchangeCallback={setSelectedDate}
                    value={selectedDate}
                    options={getAvailableDates()}
                />
            </fieldset>

            {Utils.hasValue(selectedDate) &&
                <ul>
                    {getContentForSelectedDate()}
                </ul>
            }
        </div>
    );
}

export default VillanyAteszStockAcquisitionHistory;