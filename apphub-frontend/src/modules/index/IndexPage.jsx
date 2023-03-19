import "./index_page.css";
import LocalizationHandler from "../../common/js/LocalizationHandler";
import localizationData from "./index_page_localization.json";
import LoginForm from "./LoginForm";
import Header from "../../common/component/Header";
import RegistrationForm from "./RegistrationForm";

const IndexPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);

    document.title = localizationHandler.get("title");

    return (
        <div className="main-page">
            <Header label={localizationHandler.get("title")} />
            <main>
                <LoginForm localizationHandler={localizationHandler} />

                <RegistrationForm localizationHandler={localizationHandler} />
            </main>
        </div>
    );
}

export default IndexPage;
