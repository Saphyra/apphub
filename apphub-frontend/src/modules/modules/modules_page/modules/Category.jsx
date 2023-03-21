import React from "react";
import Stream from "../../../../common/js/collection/Stream";
import Module from "./category/Module";

const Category = ({ categoryLocalizationHandler, moduleLocalizationHandler, categoryId, modules, query = [], updateModules }) => {
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

    return (
        <div
            id={categoryId}
            className="category"
        >
            <h3>{categoryLocalizationHandler.get(categoryId)}</h3>

            {displayedModules}
        </div>
    );
}

export default Category;