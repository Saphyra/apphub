import React from "react";
import "./sql_generator_preview.css";
import Utils from "../../../../../common/js/Utils";
import QueryType from "../../model/QueryType";
import InsertSql from "./query_type/InsertSql";

const SqlGeneratorPreview = ({ queryType, segments }) => {
    const assembleSql = () => {
        switch (queryType) {
            case QueryType.INSERT:
                return <InsertSql
                    queryType={queryType}
                    segments={segments}
                />
            default:
                Utils.throwException("IllegalArgument", "Preview cannot be displayed for queryType " + queryType);
        }
    }

    return (
        <div id="sql-generator-preview" className="selectable">
            {assembleSql()}
        </div>
    );
}

export default SqlGeneratorPreview;