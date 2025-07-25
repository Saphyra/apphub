import { useEffect } from "react";
import { hasValue, throwException } from "../js/Utils";

const useLoader = (args) => {
    const request = args.request || throwException("IllegalArgument", "request must not be null");
    const mapper = args.mapper || throwException("IllegalArgument", "mapper must not be null");
    const listener = args.listener || [];
    const condition = args.condition || (() => true);
    const alternativeResult = args.alternativeResult;
    const errorHandler = args.errorHandler;
    const setDisplaySpinner = args.setDisplaySpinner;

    useEffect(() => loader(), listener);

    const loader = () => {
        if (condition()) {
            if (hasValue(errorHandler)) {
                request.addErrorHandler(errorHandler)
            }
            const fetch = async () => {
                const response = await request.send(setDisplaySpinner);
                mapper(response);
            }
            fetch();
        } else if (alternativeResult !== undefined) {
            mapper(alternativeResult);
        }
    }
}

export default useLoader;