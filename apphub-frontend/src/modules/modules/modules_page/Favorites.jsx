import React from "react";
import Stream from "../../../common/js/collection/Stream";
import Entry from "../../../common/js/collection/Entry";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import categoryLocalization from "./modules/category_localization.json";
import moduleLocalization from "./modules/module_localization.json";
import Category from "./modules/Category";

const Favorites = ({ pageLocalizationHandler, modules, updateModules }) => {
    const categoryLocalizationHandler = new LocalizationHandler(categoryLocalization);
    const moduleLocalizationHandler = new LocalizationHandler(moduleLocalization);

    const moduleList = new Stream(Object.keys(modules))
        .map(category => {
            return new Entry(
                category,
                new Stream(modules[category])
                    .filter(module => module.favorite)
                    .toList()
            )
        })
        .filter(entry => entry.value.length > 0)
        .sorted((a, b) => categoryLocalizationHandler.get(a.key).localeCompare(categoryLocalizationHandler.get(b.key)))
        .map((entry) =>
            <Category
                key={entry.key}
                categoryLocalizationHandler={categoryLocalizationHandler}
                moduleLocalizationHandler={moduleLocalizationHandler}
                categoryId={entry.key}
                modules={entry.value}
                updateModules={updateModules}
            />
        )
        .toList();

    return (
        <div id="favorites" className="bar">
            <div className="bar-header">
                <h2 className="bar-title">{pageLocalizationHandler.get("favorites")}</h2>
            </div>

            {moduleList.length === 0 ? <h2 className="no-result">{pageLocalizationHandler.get("no-result")}</h2> : moduleList}
        </div>
    );
}

export default Favorites;