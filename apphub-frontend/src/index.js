import ReactDOM from 'react-dom/client';
import {
  createBrowserRouter,
  RouterProvider,
} from "react-router-dom";
import "common/style/reset.css";
import "common/style/common.css";
import 'react-toastify/dist/ReactToastify.css';
import { QueryClient, QueryClientProvider } from 'react-query';
import ErrorPage from 'modules/platform/error/ErorPage';
import IndexPage from 'modules/etc/index/IndexPage';
import Redirection from 'Redirection';
import Constants from 'common/js/Constants';
import ModulesPage from 'modules/etc/modules/ModulesPage';
import SkyXploreMainMenuPage from 'modules/feature/skyxplore/main_menu/SkyXploreMainMenuPage';
import SkyXploreCharacterPage from 'modules/feature/skyxplore/character/SkyXploreCharacterPage';
import SkyXploreLobbyPage from 'modules/feature/skyxplore/lobby/SkyXploreLobbyPage';
import SkyXploreGamePage from 'modules/feature/skyxplore/game/SkyXploreGamePage';
import SkyXploreAdminListPage from 'modules/feature/skyxplore/admin/list/SkyXploreAdminListPage';
import SkyXploreAdminDetailsPage from 'modules/feature/skyxplore/admin/details/SkyXploreAdminPanelDeatilsPage';
import NotebookPage from 'modules/feature/notebook/index/NotebookPage';
import NotebookNewPage from 'modules/feature/notebook/new/NotebookNewPage';
import NewCategoryPage from 'modules/feature/notebook/new/new_category/NewCategoryPage';
import NewTextPage from 'modules/feature/notebook/new/new_text/NewTextPage';
import NewLinkPage from 'modules/feature/notebook/new/new_link/NewLinkPage';
import NewOnlyTitlePage from 'modules/feature/notebook/new/new_only_title/NewOnlyTitlePage';
import NewChecklistPage from 'modules/feature/notebook/new/new_checklist/NewChecklistPage';
import NewTablePage from 'modules/feature/notebook/new/new_table/NewTablePage';
import NewImagePage from 'modules/feature/notebook/new/new_image/NewImagePage';
import NewFilePage from 'modules/feature/notebook/new/new_file/NewFilePage';
import NewFilesPage from 'modules/feature/notebook/new/new_file/NewFilesPage';
import NewImagesPage from 'modules/feature/notebook/new/new_image/NewImagesPage';
import NotebookEditListItemPage from 'modules/feature/notebook/edit/NotebookEditListItemPage';
import MigrationTasksPage from 'modules/etc/admin_panel/migration_tasks/MigrationTasksPage';
import AccountPage from 'modules/etc/account/AccountPage';
import RolesForAllPage from 'modules/etc/admin_panel/roles_for_all/RolesForAllPage';
import RoleManagementPage from 'modules/etc/admin_panel/role_management/RoleManagementPage';
import DisabledRoleManagementPage from 'modules/etc/admin_panel/disabled_role_management/DisabledRoleManagementPage';
import ErrorReportOverviewPage from 'modules/etc/admin_panel/error_report/overview/ErrorReportOverviewPage';
import ErrorReportDetailsPage from 'modules/etc/admin_panel/error_report/details/ErrorReportDetailsPage';
import BanPage from 'modules/etc/admin_panel/ban/index/BanPage';
import BanDetailsPage from 'modules/etc/admin_panel/ban/details/BanDetailsPage';
import Base64Page from 'modules/feature/utils/base64/Base64Page';
import JsonFormatterPage from 'modules/feature/utils/json_formatter/JsonFormatterPage';
import EliteBase from 'modules/feature/elite_base/EliteBase';
import RandomDirectionPage from 'modules/feature/utils/random_direction/RandomDirectionPage';
import CalendarPage from 'modules/feature/calendar/index/CalendarPage';
import CalendarCreateEventPage from 'modules/feature/calendar/create_event/CalendarCreateEventPage';
import CalendarLabelsPage from 'modules/feature/calendar/labels/CalendarLabelsPage';
import CalendarSearchPage from 'modules/feature/calendar/search/CalendarSearchPage';
import CalendarEditOccurrencePage from 'modules/feature/calendar/edit_occurrence/CalendarEditOccurrencePage';
import CalendarEditEventPage from 'modules/feature/calendar/edit_event/CalendarEditEventPage';
import ExpiredEventsPage from 'modules/feature/calendar/expired_event/ExpiredEventsPage';
import MonitoringPage from 'modules/platform/monitoring/MonitoringPage';

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
    path: "/web/skyxplore/game/admin",
    element: <SkyXploreAdminListPage />,
    loader: () => {
      return {
        gameId: null,
        type: "GAME"
      }
    }
  },
  {
    path: "/web/skyxplore/game/admin/:type/:gameId",
    element: <SkyXploreAdminListPage />,
    loader: ({ params }) => {
      return {
        gameId: params.gameId,
        type: params.type
      }
    }
  },
  {
    path: "/web/skyxplore/game/admin/:gameId/:type/:id",
    element: <SkyXploreAdminDetailsPage />,
    loader: ({ params }) => {
      return {
        gameId: params.gameId,
        type: params.type,
        id: params.id
      }
    }
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
    element: <DisabledRoleManagementPage />
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
    path: "/web/utils/base64",
    element: <Base64Page />
  },
  {
    path: "/web/utils/json-formatter",
    element: <JsonFormatterPage />
  },
  {
    path: "/web/elite-base",
    element: <EliteBase />
  },
  {
    path: "/web/util/random-direction",
    element: <RandomDirectionPage />
  },
  {
    path: "/web/calendar",
    element: <CalendarPage />
  },
  {
    path: "/web/calendar/create-event",
    element: <CalendarCreateEventPage />
  },
  {
    path: "/web/calendar/labels",
    element: <CalendarLabelsPage />
  },
  {
    path: "/web/calendar/search",
    element: <CalendarSearchPage />
  },
  {
    path: "/web/calendar/edit-occurrence/:occurrenceId",
    element: <CalendarEditOccurrencePage />,
    loader: ({ params }) => {
      return {
        occurrenceId: params.occurrenceId
      }
    }
  },
  {
    path: "/web/calendar/edit-event/:eventId",
    element: <CalendarEditEventPage />,
    loader: ({ params }) => {
      return {
        eventId: params.eventId
      }
    }
  },
  {
    path: "/web/calendar/expired-events",
    element: <ExpiredEventsPage />
  },
  {
    path: "/web/monitoring",
    element: <MonitoringPage />
  },
]);

const queryClient = new QueryClient();

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <QueryClientProvider client={queryClient}>
    <RouterProvider router={router} />
  </QueryClientProvider>
);