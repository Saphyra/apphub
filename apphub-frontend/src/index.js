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
import MemoryMonitoring from './modules/admin_panel/memory_monitoring/MemoryMonitoring';
import MigrationTasksPage from './modules/admin_panel/migration_tasks/MigrationTasksPage';
import SkyXploreGamePage from './modules/skyxplore/game/SkyXploreGamePage';
import { QueryClient, QueryClientProvider } from 'react-query';
import AccountPage from './modules/account/AccountPage';
import RolesForAllPage from './modules/admin_panel/roles_for_all/RolesForAllPage';
import RoleManagementPage from './modules/admin_panel/role_management/RoleManagementPage';
import DisabledRoleManagement from './modules/admin_panel/disabled_role_management/DisabledRoleManagementPage';
import ErrorReportOverviewPage from './modules/admin_panel/error_report/overview/ErrorReportOverviewPage';
import ErrorReportDetailsPage from './modules/admin_panel/error_report/details/ErrorReportDetailsPage';
import BanPage from './modules/admin_panel/ban//index/BanPage';
import BanDetailsPage from './modules/admin_panel/ban/details/BanDetailsPage';
import VillanyAteszIndexPage from './modules/custom/villany_atesz/VillanyAteszIndexPage';
import VillanyAteszContactsPage from './modules/custom/villany_atesz/contacts/VillanyAteszContactsPage';
import VillanyAteszStockPage from './modules/custom/villany_atesz/stock/VillanyAteszStockPage';
import Base64Page from './modules/utils/base64/Base64Page';
import JsonFormatterPage from './modules/utils/json_formatter/JsonFormatterPage';
import NewFilesPage from './modules/notebook/new/new_file/NewFilesPage';
import NewImagesPage from './modules/notebook/new/new_image/NewImagesPage';
import VillanyAteszToolboxPage from './modules/custom/villany_atesz/toolbox/VillanyAteszToolboxPage';
import Constants from './common/js/Constants';
import Redirection from './Redirection';
import ErrorPage from './modules/error/ErorPage';
import PerformanceReporting from './modules/admin_panel/performance_reporting/PerformanceReporting';
import EliteBase from './modules/custom/elite_base/EliteBase';
import VillanyAteszCommissionsPage from './modules/custom/villany_atesz/commissions/VillanyAteszCommissionsPage';
import RandomDirectionPage from './modules/custom/random_route/RandomDirectionPage';

const router = createBrowserRouter([
  {
    path: "/web/error",
    element: <ErrorPage />,
  },
  {
    path: "/",
    element: <Redirection url={Constants.INDEX_PAGE} />,
  },
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
    path: "/web/skyxplore/game",
    element: <SkyXploreGamePage />
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
    element: <NewTablePage checklist={true} custom={false} />,
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
    path: "/web/notebook/new/files/:parent",
    element: <NewFilesPage />,
    loader: ({ params }) => {
      return {
        parent: params.parent
      }
    }
  },
  {
    path: "/web/notebook/new/images/:parent",
    element: <NewImagesPage />,
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
  },
  {
    path: "/web/user/account",
    element: <AccountPage />
  },
  {
    path: "/web/admin-panel/roles-for-all",
    element: <RolesForAllPage />
  },
  {
    path: "/web/admin-panel/role-management",
    element: <RoleManagementPage />
  },
  {
    path: "/web/admin-panel/disabled-role-management",
    element: <DisabledRoleManagement />
  },
  {
    path: "/web/admin-panel/error-report",
    element: <ErrorReportOverviewPage />
  },
  {
    path: "/web/admin-panel/error-report/:errorReportId",
    element: <ErrorReportDetailsPage />,
    loader: ({ params }) => {
      return {
        errorReportId: params.errorReportId
      }
    }
  },
  {
    path: "/web/admin-panel/ban",
    element: <BanPage />
  },
  {
    path: "/web/admin-panel/ban/:userId",
    element: <BanDetailsPage />,
    loader: ({ params }) => {
      return {
        userId: params.userId
      }
    }
  },
  {
    path: "/web/villany-atesz",
    element: <VillanyAteszIndexPage />
  },
  {
    path: "/web/villany-atesz/contacts",
    element: <VillanyAteszContactsPage />
  },
  {
    path: "/web/villany-atesz/stock",
    element: <VillanyAteszStockPage />
  },
  {
    path: "/web/villany-atesz/toolbox",
    element: <VillanyAteszToolboxPage />
  },
  {
    path: "/web/villany-atesz/commissions",
    element: <VillanyAteszCommissionsPage />
  },
  {
    path: "/web/utils/base64",
    element: <Base64Page />
  },
  {
    path: "/web/utils/json-formatter",
    element: <JsonFormatterPage />
  },
  {
    path: "/web/admin-panel/performance-reporting",
    element: <PerformanceReporting />
  },
  {
    path: "/web/elite-base",
    element: <EliteBase />
  },
  {
    path: "/web/random-direction",
    element: <RandomDirectionPage />
  },
]);

const queryClient = new QueryClient();

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <QueryClientProvider client={queryClient}>
    <RouterProvider router={router} />
  </QueryClientProvider>
);