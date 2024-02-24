import { useEffect } from "react";

const useLoader = (request, mapper, listener = []) => {
    useEffect(() => loader(), listener);

    const loader = () => {
        const fetch = async () => {
            const response = await request.send();
            mapper(response);
        }
        fetch();
    }
}

export default useLoader;