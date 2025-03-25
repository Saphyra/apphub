import React, { useEffect, useState } from "react";
import ScheduledInputField from "../../../../common/component/input/ScheduledInputField";
import PreLabeledInputField from "../../../../common/component/input/PreLabeledInputField";
import { VILLANY_ATESZ_COMMISSION_CREATE_OR_UPDATE, VILLANY_ATESZ_GET_CARTS } from "../../../../common/js/dao/endpoints/VillanyAteszEndpoints";
import MapStream from "../../../../common/js/collection/MapStream";
import PostLabeledInputField from "../../../../common/component/input/PostLabeledInputField";
import LabelWrappedInputField from "../../../../common/component/input/LabelWrappedInputField";
import { formatNumber, hasValue } from "../../../../common/js/Utils";
import Button from "../../../../common/component/input/Button";
import useLoader from "../../../../common/hook/Loader";
import SelectInput, { SelectOption } from "../../../../common/component/input/SelectInput";
import Stream from "../../../../common/js/collection/Stream";

const Commission = ({ localizationHandler, commission, setCommission }) => {
    const [carts, setCarts] = useState([]);

    useLoader(VILLANY_ATESZ_GET_CARTS.createRequest(), setCarts);

    const totalWage = commission.daysOfWork * commission.hoursPerDay * commission.hourlyWage + Number(commission.departureFee);
    const cartCost = hasValue(commission.cart) ? commission.cart.cartCost * commission.cart.margin : 0;
    const materialCost = cartCost + Number(commission.extraCost);
    const totalCost = totalWage + materialCost;
    const toBePaid = totalCost * commission.margin;

    return (
        <div id="villany-atesz-commissions-commission">
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

                <div>
                    <span>{localizationHandler.get("total-wage")}: </span>
                    <span>{formatNumber(totalWage)}</span>
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
                        value={hasValue(commission.cart) ? commission.cart.cartId : ""}
                        options={getCartOptions()}
                        onchangeCallback={v => saveCommission("cartId", v)}
                    />
                </div>

                {hasValue(commission.cart) &&
                    <div>
                        <span>{localizationHandler.get("cart-price")}: </span>
                        <span>{cartCost}</span>
                        <span> Ft</span>
                    </div>
                }

                <div>
                    <span>{localizationHandler.get("material-cost")}: </span>
                    <span>{formatNumber(materialCost)}</span>
                    <span> Ft</span>
                </div>
            </fieldset>

            <fieldset>
                <legend>{localizationHandler.get("payment")}</legend>

                <div>
                    <span>{localizationHandler.get("total-cost")}: </span>
                    <span>{formatNumber(totalCost)}</span>
                    <span> Ft</span>
                </div>

                <fieldset>
                    <div>
                        <span>{localizationHandler.get("margin")}: </span>
                        <span>{formatNumber(commission.margin * 100)}</span>
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

                <div>
                    <span>{localizationHandler.get("to-be-paid")}: </span>
                    <span>{formatNumber(toBePaid)}</span>
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

                <div>
                    <span>{localizationHandler.get("remaining")}: </span>
                    <span>{formatNumber(toBePaid - commission.deposit)}</span>
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

        console.log(commission);

        const response = await VILLANY_ATESZ_COMMISSION_CREATE_OR_UPDATE.createRequest(commission)
            .send();

        setCommission(response);

        return true;
    }
}

export default Commission;