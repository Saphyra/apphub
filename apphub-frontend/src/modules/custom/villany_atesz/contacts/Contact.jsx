import React from "react";
import Button from "../../../../common/component/input/Button";
import ConfirmationDialogData from "../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import Endpoints from "../../../../common/js/dao/dao";
import Constants from "../../../../common/js/Constants";

const Contact = ({ contact, setEditedContact, setConfirmationDialogData, deleteContact, localizationHandler }) => {
    const openConfirmDeletionDialog = () => {
        setConfirmationDialogData(new ConfirmationDialogData(
            "villany-atesz-delete-contact-confirmation",
            localizationHandler.get("delete-contact-confirmation-title"),
            localizationHandler.get("delete-contact-confirmation-content", contact),
            [
                <Button
                    key="delete"
                    id="villany-atesz-delete-contact-confirm-button"
                    label={localizationHandler.get("delete")}
                    onclick={() => deleteContact(contact.contactId)}
                />,
                <Button
                    key="cancel"
                    id="villany-atesz-delete-contact-cancel-button"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        ));
    }

    const createCart = async () => {
        const cartId = await Endpoints.VILLANY_ATESZ_CREATE_CART.createRequest({ value: contact.contactId })
            .send();

        sessionStorage.activeCart = cartId;

        window.location.href = Constants.VILLANY_ATESZ_STOCK_PAGE;
    }

    return (
        <tr
            className="villany-atesz-contacts-contact"
            onClick={() => setEditedContact(contact)}
        >
            <td className="villany-atesz-contacts-contact-code">{contact.code}</td>
            <td className="villany-atesz-contacts-contact-name">{contact.name}</td>
            <td className="villany-atesz-contacts-contact-phone">{contact.phone}</td>
            <td className="villany-atesz-contacts-contact-address">{contact.address}</td>
            <td className="villany-atesz-contacts-contact-note">{contact.note}</td>
            <td>
                <Button
                    className="villany-atesz-contacts-contact-create-cart-button"
                    onclick={() => createCart()}
                    label={localizationHandler.get("create-cart")}
                />

                <Button
                    className="villany-atesz-contacts-contact-delete-button"
                    onclick={() => openConfirmDeletionDialog()}
                    label={localizationHandler.get("delete")}
                />
            </td>
        </tr>
    );
}

export default Contact;