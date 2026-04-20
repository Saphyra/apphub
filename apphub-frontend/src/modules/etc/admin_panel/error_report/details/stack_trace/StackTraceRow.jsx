import React from "react";

const StackTraceRow = ({row}) =>{
    return (
        <div className="stack-trace-row">
            <span>at </span>
            <span>{row.className}</span>
            <span>.</span>
            <span>{row.methodName}</span>
            <span>{"("}</span>
            <span>{row.fileName}</span>
            <span>:</span>
            <span>{row.lineNumber}</span>
            <span>{")"}</span>
        </div>
    );
}

export default StackTraceRow;