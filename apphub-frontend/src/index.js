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
import { MODULES_PAGE } from 'modules/etc/modules/ModulesEndpoints';
import { ERROR_PAGE, INDEX_PAGE } from 'common/js/GenericEndpoints';
import { SKYXPLORE_MAIN_MENU_PAGE } from 'modules/feature/skyxplore/main_menu/SkyXploreMainMenuEndpoints';
import { SKYXPLORE_CHARACTER_PAGE } from 'modules/feature/skyxplore/character/SkyXploreCharacterEndpoints';
import { SKYXPLORE_LOBBY_PAGE } from 'modules/feature/skyxplore/lobby/SkyXploreLobbyEndpoints';
import { SKYXPLORE_GAME_PAGE } from 'modules/feature/skyxplore/game/SkyXploreGameEndpoints';
import { SKYXPLORE_ADMIN_DETAILS_PAGE, SKYXPLORE_ADMIN_LIST_PAGE, SKYXPLORE_ADMIN_MAIN_PAGE } from 'modules/feature/skyxplore/admin/SkyXploreAdminEndpoints';
import { NOTEBOOK_EDIT_LIST_ITEM_PAGE, NOTEBOOK_NEW_CATEGORY_PAGE, NOTEBOOK_NEW_CHECKLIST_PAGE, NOTEBOOK_NEW_CHECKLIST_TABLE_PAGE, NOTEBOOK_NEW_CUSTOM_TABLE_PAGE, NOTEBOOK_NEW_FILE_PAGE, NOTEBOOK_NEW_FILES_PAGE, NOTEBOOK_NEW_IMAGE_PAGE, NOTEBOOK_NEW_IMAGES_PAGE, NOTEBOOK_NEW_LINK_PAGE, NOTEBOOK_NEW_ONLY_TITLE_PAGE, NOTEBOOK_NEW_PAGE, NOTEBOOK_NEW_TABLE_PAGE, NOTEBOOK_NEW_TEXT_PAGE, NOTEBOOK_PAGE } from 'modules/feature/notebook/NotebookEndpoints';
import { ADMIN_PANEL_DISABLED_ROLE_MANAGEMENT_PAGE, ADMIN_PANEL_MIGRATION_TASKS_PAGE, ADMIN_PANEL_ROLE_MANAGEMENT_PAGE, ADMIN_PANEL_ROLES_FOR_ALL_PAGE, ADMIN_PANEL_ERROR_REPORT_PAGE, ADMIN_PANEL_ERROR_REPORT_DETAILS_PAGE, ADMIN_PANEL_BAN_PAGE, ADMIN_PANEL_BAN_DETAILS_PAGE } from 'modules/etc/admin_panel/AdminPanelEndpoints';
import { ACCOUNT_PAGE } from 'modules/etc/account/AccountEndpoints';
import { UTILS_BASE_64_PAGE, UTILS_JSON_FORMATTER_PAGE, UTILS_RANDOM_DIRECTION_PAGE } from 'modules/feature/utils/UtilsEndpoints';
import { ELITE_BASE_PAGE } from 'modules/feature/elite_base/EliteBaseEndpoints';
import { CALENDAR_CREATE_EVENT_PAGE, CALENDAR_EDIT_EVENT_PAGE, CALENDAR_EDIT_OCCURRENCE_PAGE, CALENDAR_EXPIRED_EVENTS_PAGE, CALENDAR_LABELS_PAGE, CALENDAR_PAGE, CALENDAR_SEARCH_PAGE, CALENDAR_SHARE_PAGE } from 'modules/feature/calendar/CalendarEndpoints';
import { MONITORING_PAGE } from 'modules/platform/monitoring/MonitoringEndpoints';
import { TASK_MANAGER_CREATE_ORGANIZATION_PAGE, TASK_MANAGER_ORGANIZATION_INDEX_PAGE, TASK_MANAGER_PAGE } from 'modules/feature/task_manager/TaskManagerEndpoints';
import TaskManagerIndexPage from 'modules/feature/task_manager/index/TaskManagerIndexPage';
import TaskManagerCreateOrganizationPage from 'modules/feature/task_manager/organization/create/TaskManagerCreateOrganizationPage';
import TaskManagerOrganizationIndexPage from 'modules/feature/task_manager/organization/index/TaskManagerOrganizationIndexPage';
import CalendarSharePage from 'modules/feature/calendar/share/CalendarSharePage';

const router = createBrowserRouter([
  {
    path: ERROR_PAGE,
    element: <ErrorPage />,
  },
  {
    path: "/",
    element: <Redirection url={INDEX_PAGE} />,
  },
  {
    path: INDEX_PAGE,
    element: <IndexPage />,
  },
  {
    path: MODULES_PAGE,
    element: <ModulesPage />,
  },
  {
    path: SKYXPLORE_MAIN_MENU_PAGE,
    element: <SkyXploreMainMenuPage />
  },
  {
    path: SKYXPLORE_CHARACTER_PAGE,
    element: <SkyXploreCharacterPage />
  },
  {
    path: SKYXPLORE_LOBBY_PAGE,
    element: <SkyXploreLobbyPage />
  },
  {
    path: SKYXPLORE_GAME_PAGE,
    element: <SkyXploreGamePage />
  },
  {
    path: SKYXPLORE_ADMIN_MAIN_PAGE,
    element: <SkyXploreAdminListPage />,
    loader: () => {
      return {
        gameId: null,
        type: "GAME"
      }
    }
  },
  {
    path: SKYXPLORE_ADMIN_LIST_PAGE.toPageUrl(),
    element: <SkyXploreAdminListPage />,
    loader: ({ params }) => {
      return {
        gameId: params.gameId,
        type: params.type
      }
    }
  },
  {
    path: SKYXPLORE_ADMIN_DETAILS_PAGE.toPageUrl(),
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
    path: NOTEBOOK_PAGE,
    element: <NotebookPage />
  },
  {
    path: NOTEBOOK_NEW_PAGE.toPageUrl(),
    element: <NotebookNewPage />,
    loader: ({ params }) => {
      return {
        parent: params.parent
      }
    }
  },
  {
    path: NOTEBOOK_NEW_CATEGORY_PAGE.toPageUrl(),
    element: <NewCategoryPage />,
    loader: ({ params }) => {
      return {
        parent: params.parent
      }
    }
  },
  {
    path: NOTEBOOK_NEW_TEXT_PAGE.toPageUrl(),
    element: <NewTextPage />,
    loader: ({ params }) => {
      return {
        parent: params.parent
      }
    }
  },
  {
    path: NOTEBOOK_NEW_LINK_PAGE.toPageUrl(),
    element: <NewLinkPage />,
    loader: ({ params }) => {
      return {
        parent: params.parent
      }
    }
  },
  {
    path: NOTEBOOK_NEW_ONLY_TITLE_PAGE.toPageUrl(),
    element: <NewOnlyTitlePage />,
    loader: ({ params }) => {
      return {
        parent: params.parent
      }
    }
  },
  {
    path: NOTEBOOK_NEW_CHECKLIST_PAGE.toPageUrl(),
    element: <NewChecklistPage />,
    loader: ({ params }) => {
      return {
        parent: params.parent
      }
    }
  },
  {
    path: NOTEBOOK_NEW_TABLE_PAGE.toPageUrl(),
    element: <NewTablePage checklist={false} custom={false} />,
    loader: ({ params }) => {
      return {
        parent: params.parent
      }
    }
  },
  {
    path: NOTEBOOK_NEW_CUSTOM_TABLE_PAGE.toPageUrl(),
    element: <NewTablePage checklist={false} custom={true} />,
    loader: ({ params }) => {
      return {
        parent: params.parent
      }
    }
  },
  {
    path: NOTEBOOK_NEW_CHECKLIST_TABLE_PAGE.toPageUrl(),
    element: <NewTablePage checklist={true} custom={false} />,
    loader: ({ params }) => {
      return {
        parent: params.parent
      }
    }
  },
  {
    path: NOTEBOOK_NEW_IMAGE_PAGE.toPageUrl(),
    element: <NewImagePage />,
    loader: ({ params }) => {
      return {
        parent: params.parent
      }
    }
  },
  {
    path: NOTEBOOK_NEW_FILE_PAGE.toPageUrl(),
    element: <NewFilePage />,
    loader: ({ params }) => {
      return {
        parent: params.parent
      }
    }
  },
  {
    path: NOTEBOOK_NEW_FILES_PAGE.toPageUrl(),
    element: <NewFilesPage />,
    loader: ({ params }) => {
      return {
        parent: params.parent
      }
    }
  },
  {
    path: NOTEBOOK_NEW_IMAGES_PAGE.toPageUrl(),
    element: <NewImagesPage />,
    loader: ({ params }) => {
      return {
        parent: params.parent
      }
    }
  },
  {
    path: NOTEBOOK_EDIT_LIST_ITEM_PAGE.toPageUrl(),
    element: <NotebookEditListItemPage />,
    loader: ({ params }) => {
      return {
        listItemId: params.listItemId
      }
    }
  },
  {
    path: ADMIN_PANEL_MIGRATION_TASKS_PAGE,
    element: <MigrationTasksPage />
  },
  {
    path: ACCOUNT_PAGE,
    element: <AccountPage />
  },
  {
    path: ADMIN_PANEL_ROLES_FOR_ALL_PAGE,
    element: <RolesForAllPage />
  },
  {
    path: ADMIN_PANEL_ROLE_MANAGEMENT_PAGE,
    element: <RoleManagementPage />
  },
  {
    path: ADMIN_PANEL_DISABLED_ROLE_MANAGEMENT_PAGE,
    element: <DisabledRoleManagementPage />
  },
  {
    path: ADMIN_PANEL_ERROR_REPORT_PAGE,
    element: <ErrorReportOverviewPage />
  },
  {
    path: ADMIN_PANEL_ERROR_REPORT_DETAILS_PAGE.toPageUrl(),
    element: <ErrorReportDetailsPage />,
    loader: ({ params }) => {
      return {
        errorReportId: params.errorReportId
      }
    }
  },
  {
    path: ADMIN_PANEL_BAN_PAGE,
    element: <BanPage />
  },
  {
    path: ADMIN_PANEL_BAN_DETAILS_PAGE.toPageUrl(),
    element: <BanDetailsPage />,
    loader: ({ params }) => {
      return {
        userId: params.userId
      }
    }
  },
  {
    path: UTILS_BASE_64_PAGE,
    element: <Base64Page />
  },
  {
    path: UTILS_JSON_FORMATTER_PAGE,
    element: <JsonFormatterPage />
  },
  {
    path: ELITE_BASE_PAGE,
    element: <EliteBase />
  },
  {
    path: UTILS_RANDOM_DIRECTION_PAGE,
    element: <RandomDirectionPage />
  },
  {
    path: CALENDAR_PAGE,
    element: <CalendarPage />
  },
  {
    path: CALENDAR_CREATE_EVENT_PAGE.toPageUrl(),
    element: <CalendarCreateEventPage />
  },
  {
    path: CALENDAR_LABELS_PAGE,
    element: <CalendarLabelsPage />
  },
  {
    path: CALENDAR_SEARCH_PAGE,
    element: <CalendarSearchPage />
  },
  {
    path: CALENDAR_SHARE_PAGE.toPageUrl(),
    element: <CalendarSharePage />,
    loader: ({ params }) => {
      return {
        type: params.type,
        id: params.id,
        parent: params.parent
      }
    }
  },
  {
    path: CALENDAR_EDIT_OCCURRENCE_PAGE.toPageUrl(),
    element: <CalendarEditOccurrencePage />,
    loader: ({ params }) => {
      return {
        eventId: params.eventId,
        occurrenceId: params.occurrenceId,
      }
    }
  },
  {
    path: CALENDAR_EDIT_EVENT_PAGE.toPageUrl(),
    element: <CalendarEditEventPage />,
    loader: ({ params }) => {
      return {
        eventId: params.eventId
      }
    }
  },
  {
    path: CALENDAR_EXPIRED_EVENTS_PAGE,
    element: <ExpiredEventsPage />
  },
  {
    path: MONITORING_PAGE,
    element: <MonitoringPage />
  },
  {
    path: TASK_MANAGER_PAGE,
    element: <TaskManagerIndexPage />
  },
  {
    path: TASK_MANAGER_CREATE_ORGANIZATION_PAGE,
    element: <TaskManagerCreateOrganizationPage />
  },
  {
    path: TASK_MANAGER_ORGANIZATION_INDEX_PAGE.toPageUrl(),
    element: <TaskManagerOrganizationIndexPage />,
    loader: ({ params }) => {
      return {
        organizationId: params.organizationId
      }
    }
  },
]);

const queryClient = new QueryClient();

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <QueryClientProvider client={queryClient}>
    <RouterProvider router={router} />
  </QueryClientProvider>
);