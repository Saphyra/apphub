import { useEffect } from "react";
import { hasValue } from "../js/Utils";

const useLoader = (
    request,
    mapper,
    listener = [],
    condition = () => true,
    alternativeResult = undefined,
    errorHandler
) => {
    useEffect(() => loader(), listener);

    const loader = () => {
        if (condition()) {
            if (hasValue(errorHandler)) {
                request.addErrorHandler(errorHandler)
            }
            const fetch = async () => {
                const response = await request.send();
                mapper(response);
            }
            fetch();
        } else if (alternativeResult !== undefined) {
            mapper(alternativeResult);
        }
    }
}

export default useLoader;