import React from 'react';
import ReactDOM from 'react-dom/client';
import {
  createBrowserRouter,
  RouterProvider,
} from "react-router-dom";
import IndexPage from './modules/index/IndexPage';
import "./common/style/reset.css";
import "./common/style/common.css";

const router = createBrowserRouter([
  {
    path: "/web",
    element: <IndexPage />,
  }
]);

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <RouterProvider router={router} />
  </React.StrictMode>
);