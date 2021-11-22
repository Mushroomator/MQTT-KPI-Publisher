package de.othr.mqtt_kpi_publisher.publisher;
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
import org.eclipse.paho.mqttv5.client.IMqttAsyncClient;
import org.eclipse.paho.mqttv5.client.IMqttToken;

/**
 * Callback for when MQTT connection was successfully established
 * @author Thomas Pilz
 */
public interface IOnMqttConSuccess {
    /**
     * Callback for when MQTT connection was successfully established
     * @param mqttClient MQTT asynchronous client
     * @param mqttToken Token for tracking the completion of an asynchronous task (returned by all non-blocking methods)
     */
    public void run(IMqttAsyncClient mqttClient, IMqttToken mqttToken);
}
