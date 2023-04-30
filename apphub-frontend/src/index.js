import React from 'react';
import ReactDOM from 'react-dom/client';
import {
  createBrowserRouter,
  RouterProvider,
} from "react-router-dom";
import IndexPage from './modules/index/IndexPage';
import "./common/style/reset.css";
import "./common/style/common.css";
import ModulesPage from './modules/modules/ModulesPage';
import SkyXploreCharacterPage from './modules/skyxplore/character/SkyXploreCharacterPage';
import SkyXploreLobbyPage from './modules/skyxplore/lobby/SkyXploreLobbyPage';
import SkyXploreMainMenuPage from './modules/skyxplore/main_menu/SkyXploreMainMenuPage';

const router = createBrowserRouter([
  {
    path: "/web",
    element: <IndexPage />,
  },
  {
    path: "/web/modules",
    element: <ModulesPage />,
  },
  {
    path: "/web/skyxplore",
    element: <SkyXploreMainMenuPage />
  },
  {
    path: "/web/skyxplore/character",
    element: <SkyXploreCharacterPage />
  },
  {
    path: "/web/skyxplore/lobby",
    element: <SkyXploreLobbyPage />
  }
]);

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <RouterProvider router={router} />
);