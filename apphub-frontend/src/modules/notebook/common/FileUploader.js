import Constants from "../../../common/js/Constants";
import Utils from "../../../common/js/Utils";
import getDefaultErrorHandler from "../../../common/js/dao/DefaultErrorHandler";
import Endpoints from "../../../common/js/dao/dao";

const uploadFile = async (file, storedFileId, setDisplaySpinner = () => { }) => {
    const formData = new FormData();
    formData.append("file", file.e.target.files[0]);

    const options = {
        method: "PUT",
        body: formData,
        headers: {
            'Cache-Control': "no-cache",
            "BrowserLanguage": Utils.getBrowserLanguage(),
            "Request-Type": Constants.HEADER_REQUEST_TYPE_VALUE
        }
    }

    await fetch(Endpoints.STORAGE_UPLOAD_FILE.assembleUrl({ storedFileId: storedFileId }), options)
        .then(r => {
            setDisplaySpinner(false);

            if (!r.ok) {
                r.text()
                    .then(body => {
                        const response = new Response(r.status, body);
                        getDefaultErrorHandler()
                            .handle(response);
                    });
            } else {
                window.location.href = Constants.NOTEBOOK_PAGE;
            }
        });
}

export default uploadFile;