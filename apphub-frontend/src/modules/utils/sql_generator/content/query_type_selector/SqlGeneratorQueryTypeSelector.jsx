import React from "react";
import Stream from "../../../../../common/js/collection/Stream";
import QueryType from "../../QueryType";
import "./sql_generator_query_type_selector.css";
import Button from "../../../../../common/component/input/Button";
import QueryData from "../../QueryData";

const SqlGeneratorQueryTypeSelector = ({ queryData, setQueryData }) => {
    const updateQueryType = (queryType) => {
        setQueryData(new QueryData(queryType));
    }

    const getContent = () => {
        return new Stream(Object.values(QueryType))
            .map(queryType => <Button
                key={queryType}
                className={queryType == queryData.queryType ? "selected" : ""}
                label={queryType}
                onclick={() => updateQueryType(queryType)}
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