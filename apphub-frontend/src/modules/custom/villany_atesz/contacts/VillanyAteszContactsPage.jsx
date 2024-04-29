import React, { useEffect, useState } from "react";
import Header from "../../../../common/component/Header";
import Footer from "../../../../common/component/Footer";
import localizationData from "./villany_atesz_contacts_page_localization.json";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import { ToastContainer } from "react-toastify";
import VillanyAteszNavigation from "../navigation/VillanyAteszNavigation";
import sessionChecker from "../../../../common/js/SessionChecker";
import NotificationService from "../../../../common/js/notification/NotificationService";
import VillanyAteszPage from "../navigation/VillanyAteszPage";
import "./villany_atesz_contacts_page.css";
import Button from "../../../../common/component/input/Button";
import Constants from "../../../../common/js/Constants";
import PreLabeledInputField from "../../../../common/component/input/PreLabeledInputField";
import InputField from "../../../../common/component/input/InputField";
import Utils from "../../../../common/js/Utils";
import Endpoints from "../../../../common/js/dao/dao";
import Stream from "../../../../common/js/collection/Stream";
import Contact from "./Contact";
import useLoader from "../../../../common/hook/Loader";
import ConfirmationDialog from "../../../../common/component/confirmation_dialog/ConfirmationDialog";
import ContactOrder from "./ContactOrder";

const VillanyAteszContactsPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const [contacts, setContacts] = useState([]);
    const [editedContact, setEditedContact] = useState(null);
    const [confirmationDialogData, setConfirmationDialogData] = useState(null);
    const [order, setOrder] = useState(ContactOrder.DEFAULT)
    const [reversed, setReversed] = useState(false);

    const [code, setCode] = useState("");
    const [name, setName] = useState("");
    const [phone, setPhone] = useState("");
    const [address, setAddress] = useState("");
    const [note, setNote] = useState("");

    const [search, setSearch] = useState("");

    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);
    useEffect(() => {
        if (!Utils.hasValue(editedContact)) {
            resetInputFields();
        } else {
            setCode(editedContact.code);
            setName(editedContact.name);
            setPhone(editedContact.phone);
            setAddress(editedContact.address);
            setNote(editedContact.note);
        }
    },
        [editedContact]
    );

    useLoader(Endpoints.VILLANY_ATESZ_GET_CONTACTS.createRequest(), setContacts);

    const resetInputFields = () => {
        setCode("");
        setName("");
        setPhone("");
        setAddress("");
        setNote("");
    }

    const tableContent = () => {
        return new Stream(contacts)
            .filter(contact => {
                return Utils.isBlank(search) ||
                    new Stream([contact.code, contact.name, contact.phone, contact.address, contact.note])
                        .join("")
                        .indexOf(search) > -1;
            })
            .sorted((a, b) => (reversed ? -1 : 1) * order(a, b))
            .map(contact => <Contact
                key={contact.contactId}
                contact={contact}
                setEditedContact={setEditedContact}
                setConfirmationDialogData={setConfirmationDialogData}
                deleteContact={deleteContact}
                localizationHandler={localizationHandler}
            />)
            .toList();

    }

    const save = async () => {
        //TODO validation
        const payload = {
            code: code,
            name: name,
            phone: phone,
            address: address,
            note: note
        }

        if (Utils.hasValue(editedContact)) {
            const response = await Endpoints.VILLANY_ATESZ_EDIT_CONTACT.createRequest(payload, { contactId: editedContact.contactId })
                .send();

            setContacts(response);
        } else {
            const response = await Endpoints.VILLANY_ATESZ_CREATE_CONTACT.createRequest(payload)
                .send();

            setContacts(response);
            resetInputFields();
        }
    }

    const deleteContact = async (contactId) => {
        const response = await Endpoints.VILLANY_ATESZ_DELETE_CONTACT.createRequest(null, { contactId: contactId })
            .send();

        setContacts(response);
        if (Utils.hasValue(editedContact) && editedContact.contactId === contactId) {
            setEditedContact(null);
        }

        setConfirmationDialogData(null);
    }

    const updateOrder = (newOrder) => {
        if (order.toString() == newOrder().toString()) {
            setReversed(r => !r);
        } else {
            setOrder(newOrder);
        }
    }

    return (
        <div id="villany-atesz-contacts" className="main-page">
            <Header label={localizationHandler.get("page-title")} />

            <main>
                <VillanyAteszNavigation page={VillanyAteszPage.CONTACTS} />

                <div id="villany-atesz-contacts-content">
                    <InputField
                        id="villany-atesz-contacts-search"
                        placeholder={localizationHandler.get("search")}
                        value={search}
                        onchangeCallback={setSearch}
                    />

                    <table id="villany-atesz-contacts-content-table" className="formatted-table">
                        <thead>
                            <tr>
                                <th onClick={() => updateOrder(ContactOrder.CODE)}>{localizationHandler.get("code")}</th>
                                <th onClick={() => updateOrder(ContactOrder.NAME)}>{localizationHandler.get("name")}</th>
                                <th onClick={() => updateOrder(ContactOrder.PHONE)}>{localizationHandler.get("phone")}</th>
                                <th onClick={() => updateOrder(ContactOrder.ADDRESS)}>{localizationHandler.get("address")}</th>
                                <th onClick={() => updateOrder(ContactOrder.NOTE)}>{localizationHandler.get("note")}</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            {tableContent()}
                        </tbody>
                    </table>
                </div>

                <div id="villany-atesz-contacts-inputs-wrapper">
                    <div id="villany-atesz-contacts-inputs-title">
                        {localizationHandler.get(Utils.hasValue(editedContact) ? "edit-contact" : "new-contact")}
                    </div>

                    <div id="villany-atesz-contacts-inputs">
                        <PreLabeledInputField
                            label={localizationHandler.get("code")}
                            input={<InputField
                                id="villany-atesz-contacts-code-input"
                                placeholder={localizationHandler.get("code")}
                                value={code}
                                onchangeCallback={setCode}
                            />}
                        />

                        <PreLabeledInputField
                            label={localizationHandler.get("name")}
                            input={<InputField
                                id="villany-atesz-contacts-name-input"
                                placeholder={localizationHandler.get("name")}
                                value={name}
                                onchangeCallback={setName}
                            />}
                        />

                        <PreLabeledInputField
                            label={localizationHandler.get("phone")}
                            input={<InputField
                                id="villany-atesz-contacts-phone-input"
                                placeholder={localizationHandler.get("phone")}
                                value={phone}
                                onchangeCallback={setPhone}
                            />}
                        />

                        <PreLabeledInputField
                            label={localizationHandler.get("address")}
                            input={<InputField
                                id="villany-atesz-contacts-address-input"
                                placeholder={localizationHandler.get("address")}
                                value={address}
                                onchangeCallback={setAddress}
                            />}
                        />

                        <PreLabeledInputField
                            label={localizationHandler.get("note")}
                            input={<InputField
                                id="villany-atesz-contacts-note-input"
                                placeholder={localizationHandler.get("note")}
                                value={note}
                                onchangeCallback={setNote}
                            />}
                        />
                    </div>

                    <Button
                        id="villany-atesz-contacts-save-button"
                        onclick={save}
                        label={localizationHandler.get("save")}
                    />
                </div>

            </main>

            <Footer
                centerButtons={
                    <Button
                        id="villany-atesz-new-contact"
                        onclick={() => setEditedContact(null)}
                        label={localizationHandler.get("new-contact")}
                    />
                }

                rightButtons={
                    <Button
                        id="villany-atesz-contacts-home-button"
                        onclick={() => window.location.href = Constants.MODULES_PAGE}
                        label={localizationHandler.get("home")}
                    />
                }
            />

            {confirmationDialogData &&
                <ConfirmationDialog
                    id={confirmationDialogData.id}
                    title={confirmationDialogData.title}
                    content={confirmationDialogData.content}
                    choices={confirmationDialogData.choices}
                />
            }

            <ToastContainer />
        </div>
    );
}

export default VillanyAteszContactsPage;