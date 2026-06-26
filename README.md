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
* Training
    * HTML
    * CSS
    * Basics of Programming
    * JavaScript

## Modules Overview

* Build and Deployment scripts: Located in the root directory
* apphub-api: Endpoint definitions of the external/internal communication
* apphub-ci: A tool to manage the servers and tests
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
* ### monitoring
  * Storing and handling metrics sent by services
* ### authorization
  * Handling tokens of the users

## How to use

### Recommended system requirements:

* CPU: AMD Ryzen R5 2600 / Intel i5 8500
* RAM: 24 GB
* Empty Disk Space: 20GB
* maven installed
* JDK25 installed
* npm installed
* PostgreSQL installed and running
* PgAdmin (or any PostgreSQL-compatible database manager) installed
* Local DynamoDB downloaded

# Usage

## Databases used

* AWS DynamoDB: storage of refresh tokens
* AWS S3: file storage (can be replaced by FTP)

### PostgreSQL

It is recommended/required to create them before first start of the application

* apphub: Used by the services run on the host machine (Local Run)
* apphub_ci: Used by the CI app
* apphub_integration: Used by integration tests
* apphub_preprod: Used by the preproduction server
* apphub_production: Used by the production server

## Start the CI app

* You can start the CI application by running "ci_start.sh"
* Application WebUI is accessible on port 8079

## Run the server directly on host

### Start

* Start the WebUI
  * Open terminal to apphub-frontend directory
  * run command "npm install react-scripts"
    * Need to do only during the first startup
  * run command "npm start" from directory "apphub-frontend"
  * Close the opened tab (or change the port to 8080)
* Select option "Local Run" in the CI app
* Select option "Start" in the CI app

CI tool stops all the running services, rebuild them, and if build is successful, start them.
Lots of command lines opens. (One for each service.)

In local environment, React based pages reload automatically after a change is made in the WebUI code.

### Start specific services

If you don't want to (re)start the whole application, you can use "Start services" option to start only the desired ones.

After selecting this option, CI tool will ask you to enter the name of the services you would like to (re)start.
You can enter names of multiple services, separated by commas ','

### Running tests

You can run the integration tests by selecting "Run Tests" option.

This will start the integration server, and run all the integration tests.

If you want to run only specific types of tests, you can use "Run test groups" option.
You need to enter the test groups you want to run as a comma ',' separated list.

### Stopping the application

You can stop the BackEnd services by selecting "Stop" option.

### Configurations

You can modify the settings of Local Run by selecting "Edit configuration" option.

Configurations you can edit:
* Deploy mode: How the application should start
  * Default: All the services will be rebuilt, unit tested, and started.
  * Skip Tests: All the services will be rebuilt, unit tests will be skipped, then services will start.
  * Skip Build: Services will not be rebuilt, only (re)started.
* Build Thread Count: How many services to build in parallel in specific Deploy Modes
* Integration Test Thread Count: How many integration tests are allowed to run parallel.
* Pre-create WebDrivers before running integration tests: defines how many WebDrivers should be created before starting the tests, so WebDriver creation does not affect the execution. 
  * Works only when running all the tests
  * Additional WebDrivers might be created if the default mode (headed or headless) is not compatible, or no available driver in the cache
* Service startup count limit: How many services to start parallel
* Enable / Disable services: You can turn off starting up some optional services if you want to save up resources
* Integration test retry count: How many times a failed test should be retried before marking it as failed

## Run application on Minikube

You can run the application on minikube. To do this, you need to install minikube.

Before you deploy the application, make sure the VM has enough CPU cores (Preferably threads of your CPU - 1), and RAM (Minimum: 14 GB)!

You can start the Virtual Machine by selecting "Start VM" option. It also starts the dashboard, it should open in your default browser.

### Deploy application to Minikube

"Deploy to VM" option deploys the application to the Virtual Machine. It works very similar to the Local Run script, with a few differences:
* During the Maven build, it creates Docker images from the services. Running Minikube is required for this step.
* It also creates Docker image from the WebUI
* Instead of starting the services on the host machine, it deploys the Docker images to Minikube
  * The namespace used in Minikube is the name of the current git branch
* Forwards the port of the main-gateway running in Minikube to the host machine's 9001 port
* Forwards the port of the PostgreSQL database running in Minikube to the host machine's 9002 port

### Cleaning up

You can use "Delete namespace" option to undeploy the application from Minikube. It is highly recommended to do after finishing work on the current branch, because unused namespaces still use the resources of the VM.

If you want to keep the namespace for further use, you can use "Scale down" option to free up resources.

### Configurations

You can edit configurations related to Minikube deployment by selecting "Edit configurations" option.
Here you can edit the "Build Thread Count", "Deploy Mode", and "Integration Test Thread Count", like for Local Run.

Keep in mind, these values are different from the ones corresponding to Local Run.

There might be cases when the CI application does not find the bash.exe file by default. This case you can specify the absolute path of the executable in the "Set bash.exe location" option.

## (Pre)Production server

Production and Preproduction server also run on Minikube, but it has differences compared to dev deployments:
* Pods have more memory available
* Uses the host's database server instead of a database server deployed to the namespace
* Menu offers additional options making the application live
  * "Start VM" option does everything the one in the "Minikube" menu does, but it also scales up the production namespace, and starts the "production_proxy" application.
  * "Production release" deploys the built images to DockerHUB (after providing correct credentials)
  * "Run Tests" runs a filtered set of tests, it skips the features not enabled on production
  * "Start Production Proxy" makes the production server available on port 9000

## Settings

* Browser startup limit: how many Selenium WebDrivers can be created parallel.
* GUI enabled: if checked, a small window opens when starting services locally displaying the status of the services, and how much time they needed to start.
* AWS configuration: configure S3 and DynamoDB properties separately for each environment.
* Authorization certificate: create / recreate RSA certificates used for token signing.

## Testing

* Since apphub-integration is not a child of the apphub project, IntelliJ will not recognize it as a module automatically. If you don't want to see compile errors, add it manually at File -> Project
  Structure -> Modules -> + -> Import module -> Select apphub-integration directory -> Import module from external model -> Maven -> Finish
* Automated tests require access to the application's endpoints and database.
* Test framework must know the ports to use, so you have to add "-DserverPort=9001 -DdatabasePort=9002" as VM option to the TestNG template at Run -> Edit configurations -> Templates -> TestNG -> VM
  options -> Paste value here -> Apply -> Ok
* Run TestNG tests parallel in IntelliJ: Add "-parallel methods -threadcount <threadCount> -dataproviderthreadcount <dpThreadCount>" as Test Runner Param below the VM options
