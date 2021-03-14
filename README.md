### Java UI automation template repo

Some short understandable description

### Table of Contents

- [Prerequisites](#prerequisites)
- [How to install](#how-to-install)
- [Environment variables](#environment-variables)
- [How to run checks](#how-to-run-checks)
- [Reporting](#reporting)
- [Environments setup](#environments-setup)
- [Test Data](#test-data)
- [Actions](#actions)
- [Checks](#checks)
- [Parallelization](#parallelization)
- [How to export results to XRAY](#how-to-export-results-to-xray)
- [How to develop](#how-to-develop)
- [Known Issues](#known-issues)

### Prerequisites
- Docker & Docker-compose
- Java
- Maven
  
### How to install
```
git clone https://github.com/mikementor/java-ui-checks.git
cd  java-ui-checks
docker pull selenoid/vnc:chrome_86.0  
```
Also, don't forget to change url to your jira in `src/test/resources/allure.properties` for correct links to jira tickets in your tests
### Environment variables

- CLEAR_REPORTS_DIR - Boolean (default: true)  clear reports dir on startup
- SHOULD_START_SELENOID - Boolean (default: false) starts selenoid container on startup. 
For faster development use in different terminal 
```
cd docker
docker-compose -f selenoid.yaml up
```
and run without that flag

- `SELENOID_URL` - String (default: `http://localhost:4444/wd/hub`) URL of remote selenoid instance
- `BASE_URL`- String (default: `https://provectus.com`) base url for selenide configuration. Useful, when you want relative urls,using `open("/admin");`
TMS
- `IMPORT_TO_XRAY` - Boolean (default: false) should we import results to xray
- `XRAY_CLIENT_ID` - String (default:"") XRAY auth detail. To obtain,use this [doc](https://docs.getxray.app/display/XRAYCLOUD/Global+Settings%3A+API+Keys) 
- `XRAY_CLIENT_SECRET` - String (default:"") XRAY auth detail. To obtain,use this [doc](https://docs.getxray.app/display/XRAYCLOUD/Global+Settings%3A+API+Keys)
- `XRAY_EXECUTION_KEY` - String (default:"") JIRA issue key of test execution(e.g. `PRJ-123`). To obtain, just create JIRA ticket of type `Test Execution'(availiable if your JIRA has XRAY add-on)

### How to run checks

There're several ways to run checks

1. If you don't have  selenoid run on your machine
```
 mvn test -DSHOULD_START_SELENOID=true
```
⚠️ If you want to run checks in IDE with this approach, you'd need to set up
environment variable(`SHOULD_START_SELENOID=true`) in `Run/Edit Configurations..`

2. For development purposes it's better to just start separate selenoid in docker-compose
Do it in separate window
```
cd docker
docker-compose -f selenoid.yaml up
```
Then you can just `mvn test`. By default, `SELENOID_URL` will resolve to `http://localhost:4444/wd/hub`

It's preferred way to run. 

3. If you have remote selenoid instance, set 

`SELENOID_URL` environment variable

Example:
`mvn test -DSELENOID_URL=http://localhost:4444/wd/hub`
That's the way to run tests in CI with selenoid set up somewhere in cloud


### Reporting
Reports are in `allure-results` folder.
If you have installed allure commandline(e.g. like [here](https://docs.qameta.io/allure/#_installing_a_commandline) or [here](https://www.npmjs.com/package/allure-commandline))
You can see allure report with command
```
allure serve
```

### How to export results to XRAY
Setup environment variables
- `IMPORT_TO_XRAY` - Boolean (default: false) should we import results to xray
- `XRAY_CLIENT_ID` - String (default:"") XRAY auth detail. To obtain,use this [doc](https://docs.getxray.app/display/XRAYCLOUD/Global+Settings%3A+API+Keys) 
- `XRAY_CLIENT_SECRET` - String (default:"") XRAY auth detail. To obtain,use this [doc](https://docs.getxray.app/display/XRAYCLOUD/Global+Settings%3A+API+Keys)
- `XRAY_EXECUTION_KEY` - String (default:"") JIRA issue key of test execution(e.g. `PRJ-123`). To obtain, just create JIRA ticket of type `Test Execution'(availiable if your JIRA has XRAY add-on)

### How to develop

### Setting for different environments
⚠️ todo
### Test Data
⚠️ todo
### Actions
⚠️ todo
### Checks
⚠️ todo 

### Parallelization

### Tips
 - install `Selenium UI Testing plugin` in IDEA
 

### Known Issues