import React, { useState } from "react";
import ScheduledInputField from "../../../../common/component/input/ScheduledInputField";
import PreLabeledInputField from "../../../../common/component/input/PreLabeledInputField";
import { VILLANY_ATESZ_COMMISSION_CREATE_OR_UPDATE, VILLANY_ATESZ_COMMISSION_GET, VILLANY_ATESZ_COMMISSION_GET_CART, VILLANY_ATESZ_GET_CARTS } from "../../../../common/js/dao/endpoints/VillanyAteszEndpoints";
import MapStream from "../../../../common/js/collection/MapStream";
import PostLabeledInputField from "../../../../common/component/input/PostLabeledInputField";
import LabelWrappedInputField from "../../../../common/component/input/LabelWrappedInputField";
import { formatNumber, hasValue } from "../../../../common/js/Utils";
import Button from "../../../../common/component/input/Button";
import useLoader from "../../../../common/hook/Loader";
import SelectInput, { SelectOption } from "../../../../common/component/input/SelectInput";
import Stream from "../../../../common/js/collection/Stream";

const DEFAULT_COMMISSION = {
    commissionId: null,
    cartId: null,
    daysOfWork: 0,
    hoursPerDay: 0,
    departureFee: 0,
    hourlyWage: 0,
    extraCost: 0,
    deposit: 0,
    margin: 1,
    lastUpdate: null
};

const Commission = ({ localizationHandler, commissionId, setCommissionId, cartId, setCartId }) => {
    const [commission, setCommission] = useState(DEFAULT_COMMISSION);
    const [cart, setCart] = useState(null);
    const [carts, setCarts] = useState([]);

    useLoader(
        {
            request: VILLANY_ATESZ_COMMISSION_GET.createRequest(null, { commissionId: commissionId }),
            mapper: setCommission,
            listener: [commissionId],
            condition: () => hasValue(commissionId),
            alternativeResult: DEFAULT_COMMISSION
        }
    );
    useLoader({ request: VILLANY_ATESZ_GET_CARTS.createRequest(), mapper: setCarts });
    useLoader(
        {
            request: VILLANY_ATESZ_COMMISSION_GET_CART.createRequest(null, { cartId: commission.cartId }),
            mapper: response => {
                setCart(response);
                if (hasValue(response)) {
                    setCartId(response.cartId)
                } else {
                    setCartId(null);
                }
            },
            listener: [commission.cartId],
            condition: () => hasValue(commission.cartId),
            alternativeResult: null
        }
    );

    const totalWage = commission.daysOfWork * commission.hoursPerDay * commission.hourlyWage + Number(commission.departureFee);
    const cartCost = hasValue(cart) ? cart.cartCost * cart.margin : 0;
    const materialCost = cartCost + Number(commission.extraCost);
    const totalMaterialCost = materialCost * commission.margin;
    const toBePaid = totalWage + totalMaterialCost;

    return (
        <div id="villany-atesz-commissions-commission" className="selectable">
            <fieldset>
                <legend>{localizationHandler.get("wage")}</legend>

                <PreLabeledInputField
                    label={localizationHandler.get("days-of-work") + ":"}
                    input={<ScheduledInputField
                        id="villany-atesz-commission-days-of-work"
                        type={"number"}
                        value={commission.daysOfWork}
                        placeholder={localizationHandler.get("days-of-work")}
                        scheduledCallback={v => saveCommission("daysOfWork", v)}
                        onchangeCallback={v => updateProperty("daysOfWork", v)}
                    />}
                />

                <PostLabeledInputField
                    label={localizationHandler.get("hours-per-day")}
                    input={<ScheduledInputField
                        id="villany-atesz-commission-hours-per-day"
                        type={"number"}
                        value={commission.hoursPerDay}
                        placeholder={localizationHandler.get("hours-per-day")}
                        scheduledCallback={v => saveCommission("hoursPerDay", v)}
                        onchangeCallback={v => updateProperty("hoursPerDay", v)}
                    />}
                />

                <LabelWrappedInputField
                    preLabel={localizationHandler.get("hourly-wage") + ":"}
                    postLabel={"Ft"}
                    inputField={<ScheduledInputField
                        id="villany-atesz-commission-hourly-wage"
                        type={"number"}
                        value={commission.hourlyWage}
                        placeholder={localizationHandler.get("hourly-wage")}
                        scheduledCallback={v => saveCommission("hourlyWage", v)}
                        onchangeCallback={v => updateProperty("hourlyWage", v)}
                    />}
                />

                <LabelWrappedInputField
                    preLabel={localizationHandler.get("departure-fee") + ":"}
                    postLabel={"Ft"}
                    inputField={<ScheduledInputField
                        id="villany-atesz-commission-departure-fee"
                        type={"number"}
                        value={commission.departureFee}
                        placeholder={localizationHandler.get("departure-fee")}
                        scheduledCallback={v => saveCommission("departureFee", v)}
                        onchangeCallback={v => updateProperty("departureFee", v)}
                    />}
                />

                <div className="villany-atesz-commission-highlighted-red">
                    <span>{localizationHandler.get("total-wage")}: </span>
                    <span id="villany-atesz-commission-total-wage">{formatNumber(totalWage)}</span>
                    <span> Ft</span>
                </div>
            </fieldset>

            <fieldset>
                <legend>{localizationHandler.get("material-price")}</legend>

                <LabelWrappedInputField
                    preLabel={localizationHandler.get("extra-cost") + ":"}
                    postLabel="Ft"
                    inputField={<ScheduledInputField
                        id="villany-atesz-commission-extra-cost"
                        type={"number"}
                        value={commission.extraCost}
                        placeholder={localizationHandler.get("extra-cost")}
                        scheduledCallback={v => saveCommission("extraCost", v)}
                        onchangeCallback={v => updateProperty("extraCost", v)}
                    />}
                />

                <div>
                    <SelectInput
                        id="villany-atesz-commission-cart-selector"
                        value={hasValue(commission.cartId) ? commission.cartId : ""}
                        options={getCartOptions()}
                        onchangeCallback={v => saveCommission("cartId", v)}
                    />
                </div>

                {hasValue(cart) &&
                    <div>
                        <span>{localizationHandler.get("cart-price")}: </span>
                        <span id="villany-atesz-commission-cart-cost">{cartCost}</span>
                        <span> Ft</span>
                    </div>
                }

                <div>
                    <span>{localizationHandler.get("material-cost")}: </span>
                    <span id="villany-atesz-commission-material-cost">{formatNumber(materialCost)}</span>
                    <span> Ft</span>
                </div>

                <fieldset>
                    <div>
                        <span>{localizationHandler.get("margin")}: </span>
                        <span id="villany-atesz-commission-margin-percent">{formatNumber(commission.margin * 100)}</span>
                        <span>%</span>
                    </div>

                    <div>
                        <Button
                            id="villany-atesz-commission-margin-minus-10-percent-button"
                            label="-10%"
                            onclick={() => saveCommission("margin", commission.margin - 0.1)}
                        />

                        <Button
                            id="villany-atesz-commission-margin-minus-1-percent-button"
                            label="-1%"
                            onclick={() => saveCommission("margin", commission.margin - 0.01)}
                        />

                        <Button
                            id="villany-atesz-commission-margin-reset-button"
                            label="100%"
                            onclick={() => saveCommission("margin", 1)}
                        />

                        <Button
                            id="villany-atesz-commission-margin-plus-1-percent-button"
                            label="+1%"
                            onclick={() => saveCommission("margin", commission.margin + 0.01)}
                        />

                        <Button
                            id="villany-atesz-commission-margin-plus-10-percent-button"
                            label="+10%"
                            onclick={() => saveCommission("margin", commission.margin + 0.1)}
                        />
                    </div>
                </fieldset>

                <div className="villany-atesz-commission-highlighted-red">
                    <span>{localizationHandler.get("total-material-cost")}: </span>
                    <span id="villany-atesz-commission-total-material-cost">{formatNumber(totalMaterialCost)}</span>
                    <span> Ft</span>
                </div>
            </fieldset>

            <fieldset>
                <legend>{localizationHandler.get("payment")}</legend>

                <div className="villany-atesz-commission-highlighted-blue">
                    <span>{localizationHandler.get("to-be-paid")}: </span>
                    <span id="villany-atesz-commission-to-be-paid">{formatNumber(toBePaid)}</span>
                    <span> Ft</span>
                </div>

                <LabelWrappedInputField
                    preLabel={localizationHandler.get("deposit") + ":"}
                    postLabel={"Ft"}
                    inputField={<ScheduledInputField
                        id="villany-atesz-commission-deposit"
                        type={"number"}
                        value={commission.deposit}
                        placeholder={localizationHandler.get("deposit")}
                        scheduledCallback={v => saveCommission("deposit", v)}
                        onchangeCallback={v => updateProperty("deposit", v)}
                    />}
                />

                <div className="villany-atesz-commission-highlighted-red">
                    <span>{localizationHandler.get("remaining")}: </span>
                    <span id="villany-atesz-commission-remaining">{formatNumber(toBePaid - commission.deposit)}</span>
                    <span> Ft</span>
                </div>
            </fieldset>
        </div>
    );

    function getCartOptions() {
        return new Stream(carts)
            .sorted((a, b) => a.contact.name.localeCompare(b.contact.name))
            .map(cart => new SelectOption(cart.contact.name, cart.cartId))
            .reverse()
            .add(new SelectOption(localizationHandler.get("no-cart"), ""))
            .reverse()
            .toList();
    }

    function updateProperty(property, value) {
        const newCommission = new MapStream(commission)
            .add(property, value)
            .toObject();
        setCommission(newCommission);
    }

    async function saveCommission(property, value) {
        commission[property] = value;

        const response = await VILLANY_ATESZ_COMMISSION_CREATE_OR_UPDATE.createRequest(commission)
            .send();

        setCommissionId(response.commissionId);
        setCommission(response);

        return true;
    }
}

export default Commission;