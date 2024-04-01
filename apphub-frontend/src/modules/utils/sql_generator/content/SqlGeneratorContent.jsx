import React from "react";
import "./sql_generator_content.css";
import SqlGeneratorQueryTypeSelector from "./query_type_selector/SqlGeneratorQueryTypeSelector";

const SqlGeneratorContent = ({ queryData, setQueryData }) => {
    return (
        <div id="sql-generator-content">
            <SqlGeneratorQueryTypeSelector
                queryData={queryData}
                setQueryData={setQueryData}
            />
        </div>
    )
}

export default SqlGeneratorContent;