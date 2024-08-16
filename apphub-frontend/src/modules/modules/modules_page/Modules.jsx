import React, { useState } from "react";
import InputField from "../../../common/component/input/InputField";
import Stream from "../../../common/js/collection/Stream";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import Category from "./modules/Category";
import categoryLocalization from "./modules/category_localization.json";
import moduleLocalization from "./modules/module_localization.json";

const Modules = ({ pageLocalizationHandler, modules, updateModules }) => {
    const [query, setQuery] = useState([]);

    const categoryLocalizationHandler = new LocalizationHandler(categoryLocalization);
    const moduleLocalizationHandler = new LocalizationHandler(moduleLocalization);

    const moduleList = new Stream(Object.keys(modules))
        .filter(category => {
            if (query.length === 0) {
                return true;
            }

            const categoryName = categoryLocalizationHandler.get(category);
            if (query.some(word => categoryName.toLowerCase().includes(word))) {
                return true;
            }

            return new Stream(modules[category])
                .anyMatch(module => query.some(word => moduleLocalizationHandler.get(module.name).toLowerCase().includes(word)));
        })
        .sorted((a, b) => categoryLocalizationHandler.get(a).localeCompare(categoryLocalizationHandler.get(b)))
        .map((category) =>
            <Category
                key={category}
                categoryLocalizationHandler={categoryLocalizationHandler}
                moduleLocalizationHandler={moduleLocalizationHandler}
                categoryId={category}
                modules={modules[category]}
                query={query}
                updateModules={updateModules}
            />
        )
        .toList();

    const queryModified = (newQuery) => {
        setQuery(newQuery.toLowerCase().split(" "));
    }

    return (
        <div
            id="all-modules"
            className="bar"
        >
            <div className="bar-header">
                <h2 className="bar-title">{pageLocalizationHandler.get("all-modules")}</h2>
                <InputField
                    id={"modules-search-bar"}
                    type="text"
                    placeholder={pageLocalizationHandler.get("search")}
                    onchangeCallback={queryModified}
                    value={query}
                />
            </div>

            {moduleList.length === 0 ? <h2 className="no-result">{pageLocalizationHandler.get("no-result")}</h2> : moduleList}
        </div>
    );
}

export default Modules;