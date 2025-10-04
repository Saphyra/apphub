import { useEffect, useState } from "react";
import { hasValue } from "../js/Utils";

export const useEffectValidated = (callback, property, validation = (p) => hasValue(p)) => {
    useEffect(() => {
        if (validation(property)) {
            callback(property);
        }
    }, [property]);
}

export const useExtractAsync = (
    extract,
    property,
    initialValue = null,
    validation = (p) => hasValue(p)
) => {
    const [value, setValue] = useState(initialValue);

    useEffect(() => {
        if (validation(property)) {
            setValue(extract(property));
        }
    }, [property]);

    return [value, setValue];
}