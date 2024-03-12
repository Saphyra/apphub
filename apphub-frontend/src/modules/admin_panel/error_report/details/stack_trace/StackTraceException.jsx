import React from "react";
import Stream from "../../../../../common/js/collection/Stream";
import StackTraceRow from "./StackTraceRow";
import Utils from "../../../../../common/js/Utils";

const StackTraceException = ({ exception, cause = false }) => {
    const getItems = () => {
        return new Stream(exception.stackTrace)
            .map((row, index) => <StackTraceRow
                key={index}
                row={row}
            />)
            .toList();
    }

    return (
        <div className="error-report-details-stack-trace">
            <div className="error-report-details-stack-trace-label">
                {cause &&
                    <span>Caused by: </span>
                }
                <span>{exception.type}</span>
                <span>: </span>
                <span>{exception.message}</span>
            </div>
            <div className="error-report-details-stack-trace-rows">
                {getItems()}
            </div>
            {Utils.hasValue(exception.cause) &&
                <StackTraceException
                    exception={exception.cause}
                    cause={true}
                />
            }
        </div>
    )
}

export default StackTraceException;