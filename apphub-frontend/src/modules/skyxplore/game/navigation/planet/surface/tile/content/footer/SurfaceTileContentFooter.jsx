import React, { useEffect, useState } from "react";
import Utils from "../../../../../../../../../common/js/Utils";
import Button from "../../../../../../../../../common/component/input/Button";
import { useQuery } from "react-query";
import Endpoints from "../../../../../../../../../common/js/dao/dao";

const SurfaceTileContentFooter = ({ surface, localizationHandler }) => {
    const [maxLevel, setMaxLevel] = useState(0);

    const { data: itemData } = useQuery(
        surface.building.dataId,
        async () => {
            return await Endpoints.SKYXPLORE_GET_ITEM_DATA.createRequest(null, { dataId: surface.building.dataId })
                .send()
        },
        {
            staleTime: Infinity,
            cacheTime: Infinity,
            enabled: Utils.hasValue(surface.building)
        }
    );

    useEffect(
        () => {
            if (Utils.hasValue(itemData)) {
                setMaxLevel(itemData.maxLevel);
            }
        },
        [itemData]
    );

    const getContent = () => {
        if (Utils.hasValue(surface.building)) {
            if (Utils.hasValue(surface.building.construction)) {
                //TODO Construction in progress
            } else {
                return (
                    <div>
                        <Button
                            className="skyxplore-game-planet-surface-building-deconstruct-button"
                            label="X"
                            title={localizationHandler.get("deconstruct")}
                            onclick={() => { }} //TODO deconstruct building
                        />

                        {isUpgradeAvailable() &&
                            <Button
                                className="skyxplore-game-planet-surface-building-upgrade-button"
                                label="."
                                title={localizationHandler.get("upgrade")}
                                onclick={() => { }} //TODO upgrade building
                            />
                        }
                    </div>
                );
            }
        } else if (Utils.hasValue(surface.terraformation)) {
            //TODO Terraformation in progress
        }
    }

    const isUpgradeAvailable = () => {
        return maxLevel > surface.building.level;
    }

    return (
        <div className="skyxplore-game-planet-surface-tile-content-footer">
            {getContent()}
        </div>
    );
}

export default SurfaceTileContentFooter;