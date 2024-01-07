import { useEffect } from "react";
import { useQuery } from "react-query";
import Utils from "../js/Utils";

const useCache = (key, request, mapper, enabled = true) => {
    const { data } = useQuery(
        key,
        async () => {
            return await request.send()
        },
        {
            staleTime: Infinity,
            cacheTime: Infinity,
            enabled: enabled
        }
    );

    useEffect(
        () => {
            if (Utils.hasValue(data)) {
                mapper(data);
            }
        },
        [data]
    );

}

export default useCache;