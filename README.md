# Apphub

## Overview

This repository contains a complete application including the UI, service implementation, build & deploy process and the automated tests.
The app's purpose is to provide an easy-to extend frame for multiple applications, like games, office related functionalities, etc.

### Currently implemented:

* User account management
    * Create / delete accounts, modify user data
    * Locale handling (HU / EN)
* Modules management (search and display available modules, mark them as favorite)
* Admin features
    * Ban/delete users
      * Ban specific roles permanently, or for a specified time
      * Schedule user deletion
    * Role management
      * Add / revoke specific roles to/from users
      * Enable / disable roles for the whole platform
    * Error monitoring
    * Memory monitoring
    * Manage migration tasks
* Notebook
    * Store specific types of data grouped into categories.
    * Search
    * Pin favorites
    * Archive and hide items
* Calendar
    * Setting up events
    * Search
    * Setting statuses for occurrences
* Development utils
    * Base64 Encoder
    * JSON formatter
    * Log formatter
* Games
    * SkyXplore (In progress, not enabled on production)
* Community (In progress, not enabled on production)
    * Chat
    * Event feed
    * Setting up groups
    * Not enabled on production
* Training
    * HTML
    * CSS
    * Basics of Programming
    * JavaScript

## Modules Overview

* Build and Deployment scripts: Located in the root directory
* apphub-api: Endpoint definitions of the external/internal communication
* apphub-fronted: React based frontend
* apphub-integration: BE and FE test framework for integration tests
* apphub-integration-server: Statistic tool for measuring integration tests
* apphub-lib: Libraries used by the services
* apphub-proxy: Proxy application for exposing the application outside the host machine
* apphub-service: Services of the application
* infra: deployment scripts used by the deployment system, kubernetes definitions

## Services

* ### admin-panel
  * Responsible for platform and user administration.
    * error-reporting
    * migration tasks
    * memory monitoring
* ### calendar
  * Responsible for the Calendar feature
* ### community
  * Responsible for the platform's "social media"
  * It's "Groups" feature will be the base of the future feature "Share with"
  * In progress, disabled
* ### modules
  * Responsible for the landing page after registration/login.
* ### notebook
  * Responsible for the "Notebook" feature.
* ### encryption
  * Currently unused
  * Will be responsible for storing encryption keys, and the access management for encrypted entities
* ### event-gateway
  * Broadcasts incoming events to the subscribers
* ### main-gateway
  * Roots the incoming requests to the corresponding service
  * Authenticates the user
  * Validates the requests semantically
* ### scheduler
  * Fires scheduled events
* ### storage
  * Responsible for file upload, storage, and download
  * Acts as a proxy / adapter between the platform and an external FTP server
* ### web-content
  * Contains localization
  * Servers frontend
  * Deprecated, will be removed once all the features are migrated to React
* ### skyxplore-data
  * Responsible for user data storage, like games, friendships, and static game files
* ### skyxplore-game
  * Simulate games currently played
* ### skyxplore-lobby
  * Setting up new games
* ### training
  * Contains the data of the training books
* ### user
  * User session management
  * User's basic data handling (email, password, etc)
  * User role configuration
* ### utils
  * Small tools

## How to use

### System requirements:

* CPU: AMD Ryzen R5 2600
* RAM: 24 GB
* Empty Disk Space: 20GB
* maven installed
* JDK17 installed
* npm installed
* PostgreSQL installed and running

### Script usage

### For local run

How to start up:
* Start postgres database
  * Create database "apphub"
  * If you plan run integration tests, create database "integration"
* Start the WebUI
  * Open terminal to apphub-frontend directory
  * run command "npm install react-scripts"
    * Need to do only during the first startup
  * run command "npm start" from directory "apphub-frontend"
  * Close the opened tab (or change the port to 8080)

Be careful! Startup process eats the CPU until all the services are up.

In local environment, React based pages reload automatically after a change is made.

#### local_start.sh

* Stops the existing services if any
* Builds the application
* Starts all the services on local machine
* Waits until all the services are started up

Usage: ./local_start.sh [skipTests | skipBuild]

* skipBuild: skip the build process of the application, just restart the services
* skipTests: skip the unit testing part of the build process

#### local_stop.sh

Stops the locally running services.

Usage: ./local_stop.sh

#### local_run_tests.sh

Runs the integration tests against the local server.

Usage: ./local_run_tests.sh [headless:true] [disabled groups:headed-only] [server port:8080] [database port:6432] [integration server port:8072]

Parameters:

* headless:
  * true (default): Selenium tests run in the background
  * false: Selenium tests open browser windows
* disabled groups: Name of test groups should not run
  * Default: headed-only. (Some tests cannot be run in headless mode, so they are excluded)
* server port: The port of the main gateway (8080)
* database port: Port of the local postgres server (5432)
* integration server port: integration server listens to this port. Default: 8072

#### local_run_test_groups.sh

Runs the integration tests against the local server with the specified groups only.

Usage: ./local_run_test_groups.sh [enabled groups] [headless:true] [disabled groups:headed-only] [server port:8080] [database port:6432] [integration server port:8072]

Parameters:

* enabled groups: Name of the groups to run
* headless:
  * true (default): Selenium tests run in the background
  * false: Selenium tests open browser windows
* disabled groups: Name of test groups should not run
  * Default: headed-only. (Some tests cannot be run in headless mode, so they are excluded)
* server port: The port of the main gateway (8080)
* database port: Port of the local postgres server (5432)
* integration server port: integration server listens to this port. Default: 8072

### For minikube deployments
All minikube operations require administrator access.

#### minikube_up.sh

Used on "production machine"

* Starts up minikube, and minikube dashboard
* Creates if not present, and scales up namespaces
  * production
  * name of the current branch (if not develop or master)
* Starts production proxy
* Forwards the ports to the current namespace

#### minikube_down.sh

Use when you need to stop any minikube

* Scales down namespaces
  * production
  * name of the current branch
* Stops minikube

#### local_minikube_up.sh

Use when you don't need to scale up production namespace

* Starts up minikube, and minikube dashboard
* Creates if not present, and scales up namespace of the current branch
* Forwards the ports to the current namespace

#### build_and_deploy.sh

* Cleans up the hard disk space by deleting unused docker images
* Builds the application
* Creates and configures namespace
* Deploys/re-deploys the services
* Waits until all the services are up

Usage: ./build_and_deploy.sh [skipBuild | skipTests] [namespace:current git branch]

Parameters:

* skipBuild: skip the build process of the application, deploy the existing images to the develop environment
* skipTests: skip the unit testing part of the build process
* namespace: Name of the namespace to use (production/develop). Default: name of the current git branch

#### deploy_frontend.sh

* Builds a docker image from the apphub-frontend module
* Deploys the module
* Waits until all the services are up

Usage: ./deploy_frontend.sh [namespace] [directory]

Parameters:

* namespace: Name of the namespace to use. Default: name of the current git branch
* directory:
    * develop (default): The develop version of the service starts up
    * production: The production version of the service starts up

#### deploy_service.sh

* Builds the specified services
* Deploys these services
* Waits until all the services are up

Usage: ./deploy_service.sh [service name] [skipTest | skipBuild]

Parameters:

* service name: Name of the service(s) to build separated by commas. (E.g. main-gateway,event-gateway)
* skipBuild: skip the build process of the application, deploy the existing images to the develop environment
* skipTests: skip the unit testing part of the build process

#### port_forward.sh

* Exposes the application's endpoints and database

Usage: ./port_forward.sh [namespace:current git branch]

Parametes:

* namespace: Name of the namespace to use. Default: name of the current git branch

#### pp.sh

* Exposes the application running on production namespace to a random port of the host machine
* Builds module apphub-proxy
* Starts apphub-proxy to expose the application outside the host machine

Usage: ./production_proxy.sh

#### production_release.sh

* Cleans up the hard disk space by deleting unused docker images
* Builds the application
* Creates and configures develop namespace
* Deploys/re-deploys the services to develop namespace
* Waits until all the services are up
* Runs automated tests against develop namespace
* Tags docker images as release and pushes them to docker hub
* Creates and configures production namespace
* Deploys/re-deploys the services to production namespace
* Waits until all the services are up
* Runs automated tests against production namespace
* Starts production-proxy

Usage: ./production_release.sh [username] [password]

Parameters:

* username: DockerHub username
* password: DockerHub password

#### run_tests.sh

Runs automated tests

Usage: ./run_tests.sh [namespace:current git branch] [headless] [disabled groups] [server port:8070] [database port:8071] [integration server port:8072]

Parametes:

* namespace: Name of the namespace to use. Default: name of the current git branch
* headless:
    * true (default): Selenium tests run in the background
    * false: Selenium tests open browser windows
* disabled groups: Name of test groups should not run
  * Default: headed-only. (Some tests cannot be run in headless mode, so they are excluded)
* server port: Script forwards the main gateway's port to this port. Default: 8070
* database port: Script forwards the database's port to this port. Default: 8071
* integration server port: integration server listens to this port. Default: 8072

#### run_production_tests.sh

Some features are not production-ready, and disabled on production.
Runs a special set of integration test against production environment.

Usage: ./run_production_tests.sh

### Defaults:

#### Default ports:

* 9000: Exposed port by apphub-proxy
* 9001: Exposed endpoints with port_forward.sh
* 9002: Exposed database with port_forward.sh
* 8080: Port of the application run locally

#### Default ports for local:

* 3000: React app (WebUI) port
* 8080: Main gateway (Entry point)
* 8081-8100: Services
* 5432: Postgres

#### Parallelism:

Builds and test runs are parallelized to achieve faster builds and test runs, but it requires higher performance, and can lead to failures on slower systems.
To decrease the load on your machine, you can:

* Decrease the maven build thread count: reduce the treadCount for  "mvn -T <threadCount>" commands found in build & deployment scripts
* Decrease the threadCounts of automated tests.

    * For apphub-integration it can be found in the pom.xml at project.build.plugins.plugin.configuration.threadCount path.
    * Constant BROWSER_STARTUP_LIMIT sets the limit of how many browsers can be in opening phase parallel, and MAX_DRIVER_COUNT sets how many browser window can be opened maximum.

## Testing

* Since apphub-integration is not a child of the apphub project, IntelliJ will not recognize it as a module automatically. If you dont want to see compile errors, add it manually at File -> Project
  Structure -> Modules -> + -> Import module -> Select apphub-integration directory -> Import module from external model -> Maven -> Finish
* Automated tests require access to the application's endpoints and database. So tests can only be run after port_forward.sh exposed the application.
* Test framework must know the ports to use, so you have to add "-DserverPort=9001 -DdatabasePort=9002" as VM option to the TestNG template at Run -> Edit configurations -> Templates -> TestNG -> VM
  options -> Paste value here -> Apply -> Ok
* Run TestNG tests parallel in IntelliJ: Add "-parallel methods -threadcount <threadCount> -dataproviderthreadcount <dpThreadCount>" as Test Runner Param below the VM options
