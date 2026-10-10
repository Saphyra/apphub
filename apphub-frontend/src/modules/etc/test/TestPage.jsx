import Footer from "common/component/Footer";
import Header from "common/component/Header";
import Button from "common/component/input/Button";
import localizationData from "./test_page_localization.json";
import LocalizationHandler from "common/js/LocalizationHandler";
import { MODULES_PAGE } from "../modules/ModulesEndpoints";

const TestPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("page-title");

    return (
        <div className="main-page">
            <Header label={localizationHandler.get("title")} />

            <main>
                Test page
            </main>

            <Footer
                rightButtons={[
                    <Button
                        key="home"
                        label={localizationHandler.get("home")}
                        onclick={() => window.location.href = MODULES_PAGE}
                    />
                ]}
            />
        </div>
    );
}

export default TestPage;