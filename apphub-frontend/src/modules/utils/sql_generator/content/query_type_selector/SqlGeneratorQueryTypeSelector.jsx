import React from "react";
import Stream from "../../../../../common/js/collection/Stream";
import QueryType from "../../model/QueryType";
import "./sql_generator_query_type_selector.css";
import Button from "../../../../../common/component/input/Button";

const SqlGeneratorQueryTypeSelector = ({ queryType, setQueryType }) => {
    const getContent = () => {
        return new Stream(Object.values(QueryType))
            .map(queryType => <Button
                key={queryType}
                className={queryType == queryType ? "selected" : ""}
                label={queryType}
                onclick={() => setQueryType(queryType)}
            />)
            .toList();
    }

    return (
        <div id="sql-generator-query-type-selector">
            {getContent()}
        </div>
    );
}

export default SqlGeneratorQueryTypeSelector;