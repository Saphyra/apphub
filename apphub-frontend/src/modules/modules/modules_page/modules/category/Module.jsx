import React from "react";
import Button from "../../../../../common/component/input/Button";
import Endpoints from "../../../../../common/js/dao/dao";

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

        const newModules = await Endpoints.MODULES_SET_FAVORITE.createRequest(body, pathVariables)
            .send();

        updateModules(newModules);
    }

    //TODO replace a with Link when all the pages were migrated
    return (
        <div
            id={"module-" + module.name}
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