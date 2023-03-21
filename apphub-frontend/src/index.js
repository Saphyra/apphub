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

const router = createBrowserRouter([
  {
    path: "/web",
    element: <IndexPage />,
  },
  {
    path: "/web/modules",
    element: <ModulesPage />,
  }
]);

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <RouterProvider router={router} />
);