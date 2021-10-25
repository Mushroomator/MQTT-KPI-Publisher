package de.othr.mqtt_kpi_publisher.publisher;

import de.othr.mqtt_kpi_publisher.kpi.Kpi;

import java.util.Collection;

/*
Copyright 2021 Thomas Pilz

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

/**
 * @author Thomas Pilz
 */
public interface IMqttKpiPublisher {
    /**
     * Collects KPIs (e.g. from sensors) and publishes them to a MQTT message broker under a given topic.
     * This method will be called every TASK_INTERVAL seconds.
     * @return collection of KPIs
     */
    Collection<Kpi> collectKpis();
}
