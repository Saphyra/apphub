import React, { useEffect, useState } from "react";
import localizationData from "./villany_atesz_commissions_page_localization.json";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import sessionChecker from "../../../../common/js/SessionChecker";
import NotificationService from "../../../../common/js/notification/NotificationService";
import Header from "../../../../common/component/Header";
import VillanyAteszNavigation from "../navigation/VillanyAteszNavigation";
import VillanyAteszPage from "../navigation/VillanyAteszPage";
import Footer from "../../../../common/component/Footer";
import Constants from "../../../../common/js/Constants";
import Button from "../../../../common/component/input/Button";
import ConfirmationDialog from "../../../../common/component/confirmation_dialog/ConfirmationDialog";
import { ToastContainer } from "react-toastify";
import "./villany_atesz_commissions.css";
import useLoader from "../../../../common/hook/Loader";
import { VILLANY_ATESZ_COMMISSION_DELETE, VILLANY_ATESZ_COMMISSION_GET, VILLANY_ATESZ_COMMISSIONS_GET } from "../../../../common/js/dao/endpoints/VillanyAteszEndpoints";
import { cacheAndUpdate, cachedOrDefault, hasValue } from "../../../../common/js/Utils";
import Commission from "./Commission";
import CommissionSelector from "./CommissionSelector";
import ConfirmationDialogData from "../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import LocalDateTime from "../../../../common/js/date/LocalDateTime";
import Optional from "../../../../common/js/collection/Optional";

const CACHE_KEY_COMMISSION_ID = "villanyAteszCommissionId";

const DEFAULT_COMMISSION = {
    commissionId: null,
    cart: null,
    daysOfWork: 0,
    hoursPerDay: 0,
    departureFee: 0,
    hourlyWage: 0,
    extraCost: 0,
    deposit: 0,
    margin: 1,
    lastUpdate: null
};

const VillanyAteszCommissionsPage = ({ }) => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const [confirmationDialogData, setConfirmationDialogData] = useState(null);

    const [commissionId, setCommissionId] = useState(cachedOrDefault(CACHE_KEY_COMMISSION_ID, null, v => v === "null" ? null : v));
    const [commissions, setCommissions] = useState([]);
    const [commission, setCommission] = useState(null);
    const [cartId, setCartId] = useState(null);

    useLoader(
        VILLANY_ATESZ_COMMISSION_GET.createRequest(null, { commissionId: commissionId }),
        setCommission,
        [commissionId],
        () => hasValue(commissionId),
        DEFAULT_COMMISSION
    );

    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);
    useEffect(() => {
        if (hasValue(commission)) {
            updateCommissionId(commission.commissionId);
            setCartId(hasValue(commission.cart) ? commission.cart.cartId : null);
        }
    }, [commission]);
    useEffect(loadCommissions, [cartId]);

    return (
        <div id="villany-atesz-commissions" className="main-page villany-atesz-main-page">
            <Header label={localizationHandler.get("page-title")} />

            <main>
                <VillanyAteszNavigation
                    page={VillanyAteszPage.COMMISSIONS}
                    customs={getCustomButtons()}
                />

                <div id="villany-atesz-commissions-content">
                    {hasValue(commission) &&
                        <Commission
                            localizationHandler={localizationHandler}
                            commission={commission}
                            setCommission={setCommission}
                        />
                    }
                </div>
            </main>

            <Footer
                rightButtons={
                    <Button
                        id="villany-atesz-home-button"
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

    function getCustomButtons() {
        const result = [];

        if (commissions.length > 0) {
            result.push(
                <CommissionSelector
                    key="commission-selector"
                    localizationHandler={localizationHandler}
                    commissions={commissions}
                    commissionId={commissionId}
                    setCommissionId={setCommissionId}
                />
            );
        }

        result.push(
            <Button
                key="create-new-button"
                id="villany-atesz-commissions-new-button"
                label={localizationHandler.get("new-commission")}
                onclick={() => setCommission(DEFAULT_COMMISSION)}
            />
        );

        if (hasValue(commissionId)) {
            result.push(
                <Button
                    key="delete-commission"
                    id="villany-atesz-delete-commission-button"
                    label={localizationHandler.get("delete-commission")}
                    onclick={confirmDeleteCommission}
                />
            )
        }

        return result;
    }

    function loadCommissions() {
        load();

        async function load() {
            const response = await VILLANY_ATESZ_COMMISSIONS_GET.createRequest()
                .send();

            setCommissions(response);
        }
    }

    function updateCommissionId(newCommissionId) {
        cacheAndUpdate(CACHE_KEY_COMMISSION_ID, newCommissionId, setCommissionId);
    }

    function confirmDeleteCommission() {
        setConfirmationDialogData(new ConfirmationDialogData(
            "villany-atesz-commission-deletion-confirmation",
            localizationHandler.get("delete-commission-confirmation-title"),
            localizationHandler.get("delete-commission-confirmation-detail", { commissionName: getCommissionName() }),
            [
                <Button
                    key="confirm"
                    id="villany-atesz-commission-deletion-confirm-button"
                    label={localizationHandler.get("delete-commission")}
                    onclick={deleteCommission}
                />,
                <Button
                    key="cancel"
                    id="villany-atesz-commission-deletion-cancel-button"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        ));

        function getCommissionName() {
            const lastUpdate = LocalDateTime.fromLocalDateTime(commission.lastUpdate).formatWithoutSeconds();
            const contactName = new Optional(commission.cart)
                .map(cart => cart.contactName)
                .orElseGet(() => localizationHandler.get("unknown-contact"))

            return lastUpdate + " - " + contactName;
        }

        async function deleteCommission() {
            const response = VILLANY_ATESZ_COMMISSION_DELETE.createRequest(null, { commissionId: commissionId })
                .send();

            setCommissionId(null);
            setCommissions(response);
            setConfirmationDialogData(null);
            setCommission(DEFAULT_COMMISSION);
        }
    }
}

export default VillanyAteszCommissionsPage;