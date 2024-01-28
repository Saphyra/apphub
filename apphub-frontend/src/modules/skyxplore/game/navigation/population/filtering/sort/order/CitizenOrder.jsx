import React from "react";
import PostLabeledInputField from "../../../../../../../../common/component/input/PostLabeledInputField";
import localizationData from "./CitizenOrderLocalization.json";
import LocalizationHandler from "../../../../../../../../common/js/LocalizationHandler";
import RadioButtonInput from "../../../../../../../../common/component/input/RadioButtonInput";
import Order from "../Order";

const CitizenOrder = ({ order, setOrder }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    return (
        <div>
            <PostLabeledInputField
                label={localizationHandler.get("ascending")}
                input={
                    <RadioButtonInput
                        id="skyxplore-game-population-order-ascending"
                        name="skyxplore-game-population-order"
                        value={Order.ASCENDING.name}
                        checked={order === Order.ASCENDING}
                        onchangeCallback={newOrder => setOrder(Order[newOrder])}
                    />
                }
            />

            <PostLabeledInputField
                label={localizationHandler.get("descending")}
                input={
                    <RadioButtonInput
                        id="skyxplore-game-population-order-descending"
                        name="skyxplore-game-population-order"
                        value={Order.DESCENDING.name}
                        checked={order === Order.DESCENDING}
                        onchangeCallback={newOrder => setOrder(Order[newOrder])}
                    />
                }
            />
        </div>
    );
}

export default CitizenOrder;