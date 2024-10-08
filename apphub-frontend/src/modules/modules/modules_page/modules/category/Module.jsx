import React from "react";
import Button from "../../../../../common/component/input/Button";
import { MODULES_SET_FAVORITE } from "../../../../../common/js/dao/endpoints/ModulesEndpoints";

const Module = ({ moduleLocalizationHandler, module, updateModules }) => {
    const favoriteClassName = module.favorite ? "favorite" : "non-favorite"

    const setFavorite = async () => {
        const newValue = !module.favorite;
        const body = {
            value: newValue
        }

        const pathVariables = {
            module: module.name
        };

        const newModules = await MODULES_SET_FAVORITE.createRequest(body, pathVariables)
            .send();

        updateModules(newModules);
    }

    return (
        <div
            id={module.name}
            className="module"
        >
            <Button
                className={"favorite-button " + favoriteClassName}
                onclick={setFavorite}
            />

            <a className="module-link" href={module.url}>{moduleLocalizationHandler.get(module.name)}</a>
        </div>
    );
}

export default Module;