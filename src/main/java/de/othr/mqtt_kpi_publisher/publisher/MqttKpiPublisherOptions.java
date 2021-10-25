package de.othr.mqtt_kpi_publisher.publisher;

import de.othr.mqtt_kpi_publisher.Defaults;

import java.util.Objects;

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
 * Options for the MQTT KPI Publisher
 * @author Thomas Pilz
 */
public class MqttKpiPublisherOptions {
    private final String mqttClientId;
    private final String mqttMsgBrokerUrl;
    private final String mqttTopic;
    private final Long initialTaskDelay;
    private final Long taskInterval;
    private final Integer mqttConnectionTimeout;

    private MqttKpiPublisherOptions(Builder builder) {
        this.mqttClientId = builder.mqttClientId;
        this.mqttMsgBrokerUrl = builder.mqttMsgBrokerUrl;
        this.mqttTopic = builder.mqttTopic;
        this.initialTaskDelay = builder.initialTaskDelay;
        this.taskInterval = builder.taskInterval;
        this.mqttConnectionTimeout = builder.mqttConnectionTimeout;
    }

    /**
     * Get MQTT client ID
     * @return MQTT client ID
     */
    public String getMqttClientId() {
        return mqttClientId;
    }

    /**
     * Get URL to MQTT message broker
     * @return URL to MQTT message broker
     */
    public String getMqttMsgBrokerUrl() {
        return mqttMsgBrokerUrl;
    }

    /**
     * Get MQTT topic
     * @return MQTT topic
     */
    public String getMqttTopic() {
        return mqttTopic;
    }

    /**
     * Get initial delay before starting first task in milliseconds
     * @return initial delay in ms
     */
    public Long getInitialTaskDelay() {
        return initialTaskDelay;
    }

    /**
     * Get interval between running two tasks in milliseconds
     * @return interval in ms
     */
    public Long getTaskInterval() {
        return taskInterval;
    }

    /**
     * Get timeout for MQTT connection to MQTT message broker in seconds
     * @return timeout in s
     */
    public Integer getMqttConnectionTimeout() {
        return mqttConnectionTimeout;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MqttKpiPublisherOptions that = (MqttKpiPublisherOptions) o;
        return Objects.equals(mqttClientId, that.mqttClientId) && Objects.equals(mqttMsgBrokerUrl, that.mqttMsgBrokerUrl) && Objects.equals(mqttTopic, that.mqttTopic) && Objects.equals(initialTaskDelay, that.initialTaskDelay) && Objects.equals(taskInterval, that.taskInterval) && Objects.equals(mqttConnectionTimeout, that.mqttConnectionTimeout);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mqttClientId, mqttMsgBrokerUrl, mqttTopic, initialTaskDelay, taskInterval, mqttConnectionTimeout);
    }

    @Override
    public String toString() {
        return "MqttKpiPublisherOptions{" +
                "mqttClientId='" + mqttClientId + '\'' +
                ", mqttMsgBrokerUrl='" + mqttMsgBrokerUrl + '\'' +
                ", mqttTopic='" + mqttTopic + '\'' +
                ", initialTaskDelay=" + initialTaskDelay +
                ", taskInterval=" + taskInterval +
                ", mqttConnectionTimeout=" + mqttConnectionTimeout +
                '}';
    }

    /**
     * Builder for MQTT Publisher options
     * @author Thomas Pilz
     */
    public static class Builder {
        private final String mqttClientId;
        private final String mqttMsgBrokerUrl;
        private final String mqttTopic;
        private Long initialTaskDelay = Defaults.INITIAL_TASK_DELAY;
        private Long taskInterval = Defaults.TASK_INTERVAL;
        private Integer mqttConnectionTimeout = Defaults.MQTT_CONNECTION_TIMEOUT;

        /**
         * Create builder required to build a MqttKpiPublisherOptions object.
         * After running this constructor a minimal MqttKpiPublisherOptions is constructed by immediately calling build().
         * @param clientId unique equipment number for object being monitored (e.g. engine)
         * @param mqttMsgBrokerUrl URL (with Port!) to MQTT message broker
         */
        public Builder(String clientId, String mqttMsgBrokerUrl, String mqttTopic){
            this.mqttClientId = clientId;
            this.mqttMsgBrokerUrl = mqttMsgBrokerUrl;
            this.mqttTopic = mqttTopic;
        }

        /**
         * Initial delay before starting first task in milliseconds
         * @param initialTaskDelay initial delay in ms
         * @return Builder instance
         */
        public Builder setInitialTaskDelay(Long initialTaskDelay) {
            this.initialTaskDelay = initialTaskDelay;
            return this;
        }

        /**
         * Set interval between running two tasks in milliseconds
         * @param taskInterval interval in ms
         * @return Builder instance
         */
        public Builder setTaskInterval(Long taskInterval) {
            this.taskInterval = taskInterval;
            return this;
        }

        /**
         * Timeout for MQTT connection to MQTT message broker in seconds
         * @param mqttConnectionTimeout timeout in s
         * @return Builder instance
         */
        public Builder setMqttConnectionTimeout(Integer mqttConnectionTimeout) {
            this.mqttConnectionTimeout = mqttConnectionTimeout;
            return this;
        }

        /**
         * Build MqttKpiPublisherOptions with parameters set as you please.
         * @return instance of MqttKpiPublisherOptions
         */
        public MqttKpiPublisherOptions build(){
            return new MqttKpiPublisherOptions(this);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Builder builder = (Builder) o;
            return Objects.equals(mqttClientId, builder.mqttClientId) && Objects.equals(mqttMsgBrokerUrl, builder.mqttMsgBrokerUrl) && Objects.equals(mqttTopic, builder.mqttTopic) && Objects.equals(initialTaskDelay, builder.initialTaskDelay) && Objects.equals(taskInterval, builder.taskInterval) && Objects.equals(mqttConnectionTimeout, builder.mqttConnectionTimeout);
        }

        @Override
        public int hashCode() {
            return Objects.hash(mqttClientId, mqttMsgBrokerUrl, mqttTopic, initialTaskDelay, taskInterval, mqttConnectionTimeout);
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "mqttClientId='" + mqttClientId + '\'' +
                    ", mqttMsgBrokerUrl='" + mqttMsgBrokerUrl + '\'' +
                    ", mqttTopic='" + mqttTopic + '\'' +
                    ", initialTaskDelay=" + initialTaskDelay +
                    ", taskInterval=" + taskInterval +
                    ", mqttConnectionTimeout=" + mqttConnectionTimeout +
                    '}';
        }
    }
}
