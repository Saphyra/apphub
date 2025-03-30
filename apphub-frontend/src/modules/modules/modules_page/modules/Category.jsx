import React, { useState } from "react";
import Stream from "../../../../common/js/collection/Stream";
import Module from "./category/Module";
import useCache from "../../../../common/hook/Cache";
import { GET_USER_SETTINGS, SET_USER_SETTINGS } from "../../../../common/js/dao/endpoints/UserEndpoints";
import Constants from "../../../../common/js/Constants";
import { isTrue } from "../../../../common/js/Utils";
import Button from "../../../../common/component/input/Button";

const Category = ({ categoryLocalizationHandler, moduleLocalizationHandler, categoryId, modules, query = [], updateModules, favorite = false }) => {
    const [collapsed, setCollapsed] = useState(false);

    useCache(
        "collapsed-modules",
        GET_USER_SETTINGS.createRequest(null, { category: Constants.SETTINGS_KEY_COLLAPSED_MODULES }),
        s => setCollapsed(isTrue(s[categoryId]))
    );

    const updateCollapsed = async (newCollapsed) => {
        const payload = {
            category: Constants.SETTINGS_KEY_COLLAPSED_MODULES,
            key: categoryId,
            value: newCollapsed
        };

        await SET_USER_SETTINGS.createRequest(payload)
            .send();

        setCollapsed(newCollapsed);
    }

    const displayedModules = new Stream(modules)
        .sorted((a, b) => moduleLocalizationHandler.get(a.name).localeCompare(moduleLocalizationHandler.get(b.name)))
        .filter(module => query.length === 0 || query.some(word => moduleLocalizationHandler.get(module.name).toLowerCase().includes(word)))
        .map(module =>
            <Module
                key={module.name}
                moduleLocalizationHandler={moduleLocalizationHandler}
                module={module}
                updateModules={updateModules}
            />
        )
        .toList();

    const getDisplayedModules = () => {
        if (favorite) {
            return displayedModules;
        }

        if (collapsed) {
            return null;
        }

        return displayedModules;
    }

    const getToggleCollaspeButton = () => {
        if (collapsed) {
            return <Button
                className="modules-collapse-category-toggle-button"
                label="+"
                onclick={() => updateCollapsed(false)}
            />
        } else {
            return <Button
                className="modules-collapse-category-toggle-button"
                label="-"
                onclick={() => updateCollapsed(true)}
            />
        }
    }

    return (
        <div
            id={categoryId}
            className="category"
        >
            <h3>
                {categoryLocalizationHandler.get(categoryId)}
                {!favorite && getToggleCollaspeButton()}
            </h3>

            {getDisplayedModules()}
        </div>
    );
}

export default Category;