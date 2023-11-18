import React from 'react';
import ReactDOM from 'react-dom/client';
import {
  createBrowserRouter,
  RouterProvider,
} from "react-router-dom";
import IndexPage from './modules/index/IndexPage';
import "./common/style/reset.css";
import "./common/style/common.css";
import 'react-toastify/dist/ReactToastify.css';
import ModulesPage from './modules/modules/ModulesPage';
import SkyXploreCharacterPage from './modules/skyxplore/character/SkyXploreCharacterPage';
import SkyXploreLobbyPage from './modules/skyxplore/lobby/SkyXploreLobbyPage';
import SkyXploreMainMenuPage from './modules/skyxplore/main_menu/SkyXploreMainMenuPage';
import NotebookPage from './modules/notebook/index/NotebookPage';
import NotebookNewPage from './modules/notebook/new/NotebookNewPage';
import NewCategoryPage from './modules/notebook/new/new_category/NewCategoryPage';
import NewTextPage from './modules/notebook/new/new_text/NewTextPage';
import NotebookEditListItemPage from './modules/notebook/edit/NotebookEditListItemPage';
import NewLinkPage from './modules/notebook/new/new_link/NewLinkPage';
import NewOnlyTitlePage from './modules/notebook/new/new_only_title/NewOnlyTitlePage';
import NewChecklistPage from './modules/notebook/new/new_checklist/NewChecklistPage';
import NewTablePage from './modules/notebook/new/new_table/NewTablePage';
import NewImagePage from './modules/notebook/new/new_image/NewImagePage';
import NewFilePage from './modules/notebook/new/new_file/NewFilePage';
import NewCustomTable from './modules/notebook/new/new_custom_table/NewCustomTable';
import MemoryMonitoring from './modules/admin_panel/memory_monitoring/MemoryMonitoring';
import MigrationTasksPage from './modules/admin_panel/migration_tasks/MigrationTasksPage';

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
  },
  {
    path: "/web/notebook",
    element: <NotebookPage />
  },
  {
    path: "/web/notebook/new/:parent",
    element: <NotebookNewPage />,
    loader: ({ params }) => {
      return {
        parent: params.parent
      }
    }
  },
  {
    path: "/web/notebook/new/category/:parent",
    element: <NewCategoryPage />,
    loader: ({ params }) => {
      return {
        parent: params.parent
      }
    }
  },
  {
    path: "/web/notebook/new/text/:parent",
    element: <NewTextPage />,
    loader: ({ params }) => {
      return {
        parent: params.parent
      }
    }
  },
  {
    path: "/web/notebook/new/link/:parent",
    element: <NewLinkPage />,
    loader: ({ params }) => {
      return {
        parent: params.parent
      }
    }
  },
  {
    path: "/web/notebook/new/only-title/:parent",
    element: <NewOnlyTitlePage />,
    loader: ({ params }) => {
      return {
        parent: params.parent
      }
    }
  },
  {
    path: "/web/notebook/new/checklist/:parent",
    element: <NewChecklistPage />,
    loader: ({ params }) => {
      return {
        parent: params.parent
      }
    }
  },
  {
    path: "/web/notebook/new/table/:parent",
    element: <NewTablePage checklist={false} custom={false} />,
    loader: ({ params }) => {
      return {
        parent: params.parent
      }
    }
  },
  {
    path: "/web/notebook/new/custom-table/:parent",
    element: <NewTablePage checklist={false} custom={true} />,
    loader: ({ params }) => {
      return {
        parent: params.parent
      }
    }
  },
  {
    path: "/web/notebook/new/checklist-table/:parent",
    element: <NewTablePage checklist={true} custom={false}/>,
    loader: ({ params }) => {
      return {
        parent: params.parent
      }
    }
  },
  {
    path: "/web/notebook/new/image/:parent",
    element: <NewImagePage />,
    loader: ({ params }) => {
      return {
        parent: params.parent
      }
    }
  },
  {
    path: "/web/notebook/new/file/:parent",
    element: <NewFilePage />,
    loader: ({ params }) => {
      return {
        parent: params.parent
      }
    }
  },
  {
    path: "/web/notebook/edit/:listItemId",
    element: <NotebookEditListItemPage />,
    loader: ({ params }) => {
      return {
        listItemId: params.listItemId
      }
    }
  },
  {
    path: "/web/admin-panel/memory-monitoring",
    element: <MemoryMonitoring />
  },
  {
    path: "/web/admin-panel/migration-tasks",
    element: <MigrationTasksPage />
  }
]);

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <RouterProvider router={router} />
);