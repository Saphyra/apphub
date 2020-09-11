# Apphub

## Overview
This repository contains a complete application including the service implementation, build & deploy process and the automated tests.
The app's purpose is to provide an easy-to extend frame for multiple applications, like games, office related functionalities, etc.

### Currently implemented:
* User account management (create / delete accounts, modify user data)
* Modules management (list available modules, mark them as favorite)
* Admin panel (Modify user's roles, to allow/block functionalities available for their account)
* Notebook (Store notes, links, checklists grouped into categories)
* Locale handling (Supported locales: EN, HU)

### Upcoming features:
* Extend Notebook module with tables and checklist-tables
* Extend Notebook module with Search functionality
* Extend Notebook module with archiving functionality
* Add Language management to Index page
* New module "Balance", a module what can help tracking financial transactions
* New module "Breakdown", a module where you can create tasks and subtasks, allowing to break complex problems to smaller tasks, and track them one-by-one
* Migrate existing application "Training": https://github.com/Saphyra/training
* Migrate/finish existing games (SkyCastle, SkyXplore versions)

## Modules Overview
* Build and Deployment scripts: Located in the root directory
* apphub-api: Endpoint definitions of the external/internal communication
* apphub-lib: Libraries used by the service
* apphub-proxy: Proxy application for exposing the service outside the host machine
* apphub-service: Includes the microservices of the application
* infra: deployment scripts used by the deployment system

## How to use

### System requirements:
* CPU: AMD Ryzen R5 2600
* RAM: 16 GB
* Minikube running with at least 6GB RAM, and the most possible CPU cores available.
  (Note: with 6GB of RAM only one namespace (develop/production) is available at once.
  To use both namespace in parallel the recommended RAM amount is 16 GB.)
* System Administrator access
* Kubectl installed

The system is configured to work with AMD Ryzen R7 2700X and 32GB RAM. The configurations can be changed to work with less resources.

### Script usage

#### build_and_deploy.sh

* Cleans up the hard disk space by deleting unused docker images
* Builds the application
* Creates and configures develop namespace
* Deploys/re-deploys the services
* Waits until all the services are up

Usage: ./build_and_deploy.sh [skipBuild | skipTests]
Parameters:
* skipBuild: skip the build process of the application, deploy the existing images to the develop environment
* skipTests: skips the unit/integration testing part of the build process


#### clean_up_space.sh
* Cleans up the hard disk space by deleting unused docker images

Usage: ./clean_up_space.sh

#### deploy.sh

* Creates and configures namespace
* Deploys/re-deploy the services

Usage: ./deploy.sh [namespace:develop]

Parametes:
* namespace: Name of the namespace to use (production/develop). Default: develop

#### develop_deployment.sh

* Cleans up the hard disk space by deleting unused docker images
* Builds the application
* Creates and configures develop namespace
* Deploys/re-deploys the services to develop namespace
* Waits until all the services are up
* Runs automated tests

Usage: ./develop_deployment.sh [skipBuild | skipTrap]
Parametes:
* skipBuild: skip the build process of the application, deploy the existing images to the develop environment
* skipTrap: created background processes for port-forwarding are not stopped after the script is finished

#### port_forward.sh

* Exposes the application's endpoints and database

Usage: ./port_forward.sh [namespace:develop]
Parametes:
* namespace: Name of the namespace to use (production/develop). Default: develop

#### production_proxy.sh

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
* Runs automated tests against develop namespcae
* Tags docker images as release and pushes them to docker hub
* Creates and configures production namespace
* Deploys/re-deploys the services to production namespace
* Waits until all the services are up
* Runs automated tests against production namespcae
* Starts production-proxy

Usage: ./production_release.sh [username] [password]
Parameters:
* username: DockerHub username
* password: DockerHub password

#### run_tests.sh

* Runs automated tests

Usage: ./run_tests.sh [namespace:develop]
Parametes:
* namespace: Namespace to run the tests agains, develop as default.

### Defaults:
#### Default ports:
* 9000: Exposed port by apphub-proxy
* 9001: Exposed endpoints with port_forward.sh
* 9002: Exposed database with port_forward.sh

#### Parallelism:
Builds and test runs are parallelised to achieve faster builds and test runs, but it causes higher performance requirement, and can lead to failures on slower systems.
To decrease the load on your machine, you can:

* Decrease the maven build thread count: reduce the treadCount for  "mvn -T <threadCount>" commands found in build&deployment scripts
* Decrease the threadCounts of automated tests. 
  
  For apphub-integration-backend it can be found in the pom.xml at project.build.plugins.plugin.configuration.threadCount path.
  
  For apphub-integration-frontend it can be found in class com.github.saphyra.apphub.integration.frontend.WebDriverFactory.
  Constant BROWSER_STARTUP_LIMIT sets the limit of how many browsers can be in opening phase parallel, and MAX_DRIVER_COUNT sets how many browser window can be opened maximum.
  
## Testing
* Since apphub-integration is not a child of the apphub project, IntelliJ will not recognize it as a module automatically. If you dont want to see compile errors, add it manually at File -> Project Structure -> Modules -> + -> Import module -> Select apphub-integration directory -> Import module from external model -> Maven -> Finish
* Automated tests require access to the application's endpoints and database. So tests can only be run after port_forward.sh exposed the application.
* Test framework must know the ports to use, so you have to add "-DserverPort=9001 -DdatabasePort=9002" as VM option to the TestNG template at Run -> Edit configurations -> Templates -> TestNG -> VM options -> Paste value here -> Apply -> Ok
* Run TestNG tests parallel in IntelliJ: Add "-parallel methods -threadcount <threadCount> -dataproviderthreadcount <dpThreadCount>" as Test Runner Param below the VM options
