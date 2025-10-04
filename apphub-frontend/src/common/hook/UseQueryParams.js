import { useLocation } from "react-router";

const useQueryParams = () => {
    const location = useLocation();
    const searchParams = new URLSearchParams(location.search);

    // Create an object to hold all key-value pairs of the query parameters
    const queryParams = Array.from(searchParams.entries()).reduce((acc, [key, value]) => {
        acc[key] = value;
        return acc;
    }, {});

    return queryParams;
};

export default useQueryParams;