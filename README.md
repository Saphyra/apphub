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
    * Ban users (permanent / for a specific time)
    * Role management (Allow / block specific roles (features) for all / specific users)
    * Error monitoring
    * Memory monitoring
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
* Training
    * HTML
    * CSS
    * Basics of Programming
    * JavaScript

## Modules Overview

* Build and Deployment scripts: Located in the root directory
* apphub-api: Endpoint definitions of the external/internal communication
* aphub-fronted: React based frontend
* apphub-lib: Libraries used by the services
* apphub-proxy: Proxy application for exposing the application outside the host machine
* apphub-service: Services of the application
* infra: deployment scripts used by the deployment system, kubernetes definitions

## How to use

### System requirements:

* CPU: AMD Ryzen R5 2600
* RAM: 24 GB
* Empty Disk Space: 2GB
* maven installed
* JDK17 installed
* npm installed
* PostgreSQL installed and running

The system is configured to work with AMD Ryzen R9 5900X and 64GB RAM. The configurations can be changed to work with less resources.

### Script usage

### For minikube deployments

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

* Runs automated tests

Usage: ./run_tests.sh [namespace:current git branch] [headless] [disabled groups]

Parametes:

* namespace: Name of the namespace to use. Default: name of the current git branch
* headless:
    * true (default): Selenium tests run in the background
    * false: Selenium tests open browser windows
* disabled groups: Name of test groups should not run

### For local run

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

Usage: ./local_run_tests.sh [headless] [disabled groups] [server port] [database port]

Parameters:

* headless:
    * true (default): Selenium tests run in the background
    * false: Selenium tests open browser windows
* disabled groups: Name of test groups should not run
* server port: The port of the main gateway (8080)
* database port: Port of the local postgres server (5432)

### Defaults:

#### Default ports:

* 9000: Exposed port by apphub-proxy
* 9001: Exposed endpoints with port_forward.sh
* 9002: Exposed database with port_forward.sh
* 8080: Port of the application run locally

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
