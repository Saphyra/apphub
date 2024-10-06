import { NOTEBOOK_CREATE_FILE } from "../../../../common/js/dao/endpoints/NotebookEndpoints";
import NotificationService from "../../../../common/js/notification/NotificationService";
import uploadFile from "../../common/FileUploader";
import validateFile from "../../common/validator/FileValidator";
import validateListItemTitle from "../../common/validator/ListItemTitleValidator";

const create = async (listItemTitle, file, parent, setDisplaySpinner) => {
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

    const storedFileResponse = await NOTEBOOK_CREATE_FILE.createRequest(payload)
        .send();

    return uploadFile(file, storedFileResponse.value, setDisplaySpinner);
}

export default create;