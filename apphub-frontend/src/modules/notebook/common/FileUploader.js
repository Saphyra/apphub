import Constants from "../../../common/js/Constants";
import { getBrowserLanguage } from "../../../common/js/Utils";
import getDefaultErrorHandler from "../../../common/js/dao/DefaultErrorHandler";
import { STORAGE_UPLOAD_FILE } from "../../../common/js/dao/endpoints/StorageEndpoints";

const uploadFile = async (file, storedFileId, setDisplaySpinner = () => { }) => {
    const formData = new FormData();
    formData.append("file", file.file);

    const options = {
        method: "PUT",
        body: formData,
        headers: {
            'Cache-Control': "no-cache",
            "BrowserLanguage": getBrowserLanguage()
        }
    }

    return fetch(STORAGE_UPLOAD_FILE.assembleUrl({ storedFileId: storedFileId }), options)
        .then(r => {
            setDisplaySpinner(false);

            if (!r.ok) {
                r.text()
                    .then(body => {
                        const response = new Response(r.status, body);
                        getDefaultErrorHandler()
                            .handle(response);
                    });
                return false;
            } else {
                return true;
            }
        });
}

export default uploadFile;