![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)

# MQTT KPI Publisher
Simple Java mini-framework simplifying development of simple MQTT Java clients which just collect and publish to a MQTT message broker.
Very simple implementation used to quickly setup many in essence very similar clients. The application was written using Open JDK 16.

> This repository is part of the [MQTT KPI Collection Project](https://github.com/Mushroomator/MQTT-KPI-Collection-Project).

## Table of Contents
- [MQTT KPI Publisher](#mqtt-kpi-publisher)
  - [Table of Contents](#table-of-contents)
  - [Getting started](#getting-started)
  - [Releases](#releases)
  - [Usage](#usage)
  - [Environment variables](#environment-variables)
  - [License](#license)

## Getting started
MQTT KPI Publisher is currently not available on Maven Central or any other repository, so you need to download the desired version of the .jar from the [release page](https://github.com/Mushroomator/MQTT-KPI-Publisher/releases). You may choose to do this manually or (e.g. in a Dockerfile) use the following command:
```bash
MQTT_KPI_PUBLISHER_RELEASE_TAG=b40485a7
wget https://github.com/Mushroomator/MQTT-KPI-Publisher/releases/download/$MQTT_KPI_PUBLISHER_RELEASE_TAG/MqttKpiPublisher-0.1-jar-with-dependencies.jar
```
> Please note: The release tag will always be the first 8 characters of the commit hash the MQTT KPI Publisher is based on.

Then install the .jar into your [local Maven repository](https://www.baeldung.com/maven-local-repository):
```bash
mvn install:install-file \
-Dfile=/path/to/MqttKpiPublisher-0.1-jar-with-dependencies.jar \
-DgroupId=de.othr \
-DartifactId=MqttKpiPublisher \
-Dversion=0.1 \
-Dpackaging=jar \
-DgeneratePom=true
```
Now you may add MQTT KPI Publisher to your `pom.xml` as with any other dependency that is available on Maven Central:
```xml
<dependency>
    <groupId>de.othr</groupId>
    <artifactId>MqttKpiPublisher</artifactId>
    <version>0.1</version>
</dependency>
```
You can now compile and package your application with the MQTT KPI Publisher using `maven clean package`.

## Releases
Each push to the main branch will trigger a tag and a corresponding release to be created. Take a look at the [Releases](https://github.com/Mushroomator/MQTT-KPI-Publisher/releases) to get the desired version. 
> Please note: the version as part of the Maven coordinates does not change as of now and therefore will stay at `0.1`.

## Usage
To use the framework you essentially just need to implement the [IMqttKpiPublisher interface](src/main/java/de/othr/mqtt_kpi_publisher/publisher/IMqttKpiPublisher.java) and provide the 3 mandatory options `MQTT message broker URL`, `MQTT client-ID` and `MQTT topic`. For more information on those and other options as well as different way to set those options please see [section "Environment variables"](#environment-variables).
```java
// Use builder to build make sure you get a valid options object
var options = new MqttKpiPublisherOptions.Builder("A unique ID", "tcp://iot.eclipse.org:1883", "/test/topic").build();
// Start collecting and publishing KPIs periodically
MqttKpiPublisher.runMqttKpiCollector(() -> {
    // get your KPIs here
    var temp = new TempSensor().readTemp();
    // return collection of KPIs
    return List.of(new Kpi("temperature", Unit.DEGREE_CELCIUS, temp));
});
```

## Environment variables
There are a few environment variables available to set mandatory parameters/ options for the MQTT KPI Publisher. All options may also be set in code using [MqttKpiPublisherOptions.Builder()](src/main/java/de/othr/mqtt_kpi_publisher/publisher/MqttKpiPublisherOptions.java). You may use both possibilities but be aware that environment variables will always take precedence over parameters set in code. 

| Name                    | Description                                                                                                                                                                       | Mandatory? | Default value |
| ----------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------- | ------------- |
| MQTT_MSG_BROKER_URL     | URL to the MQTT message broker. Must be in format `[protocol]://[hostname]:[port]` for example `tcp://iot.eclipse.org:1883`.                                                      | Yes        | -             |
| MQTT_CLIENT_ID          | Unique ID for the MQTT client.                                                                                                                                                    | Yes        | -             |
| MQTT_TOPIC              | Topic all the messages will be published to.                                                                                                                                      | Yes        | -             |
| MQTT_CONNECTION_TIMEOUT | Timeout for the connection to the MQTT Broker in seconds (s). Automatic re-connection attempts will be made if the connection is lost.                                            | No         | 10            |
| TASK_INTERVAL           | Interval between two calls to [collectKpis()](src/main/java/de/othr/mqtt_kpi_publisher/publisher/IMqttKpiPublisher.java) in milliseconds (ms)                                     | No         | 5000          |
| INITIAL_TASK_DELAY      | Initial delay for the first call to [collectKpis()](src/main/java/de/othr/mqtt_kpi_publisher/publisher/IMqttKpiPublisher.java) in milliseconds (ms) on startup of the application | No         | 0             |

## License
Copyright 2021 Thomas Pilz

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  [https://www.apache.org/licenses/LICENSE-2.0](https://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.