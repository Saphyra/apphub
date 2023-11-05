import Endpoints from "../../../../common/js/dao/dao";
import NotificationService from "../../../../common/js/notification/NotificationService";
import uploadFile from "../../common/FileUploader";
import validateFile from "../../common/validator/FileValidator";
import validateListItemTitle from "../../common/validator/ListItemTitleValidator";

const create = async (listItemTitle, file, setDisplaySpinner, parent) => {
    const listItemTitleValidationResult = validateListItemTitle(listItemTitle);
    if (!listItemTitleValidationResult.valid) {
        NotificationService.showError(listItemTitleValidationResult.message);
        return;
    }

    const imageValidationResult = validateFile(file);
    if (!imageValidationResult.valid) {
        NotificationService.showError(imageValidationResult.message);
        return;
    }

    setDisplaySpinner(true);

    const payload = {
        title: listItemTitle,
        parent: parent,
        metadata: {
            fileName: file.fileName,
            size: file.size
        }
    }

    const storedFileResponse = await Endpoints.NBOTEBOOK_CREATE_IMAGE.createRequest(payload)
        .send();

    uploadFile(file, storedFileResponse.value, setDisplaySpinner)
}

export default create;