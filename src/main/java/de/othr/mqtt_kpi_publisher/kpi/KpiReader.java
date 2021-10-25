package de.othr.mqtt_kpi_publisher.kpi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.othr.mqtt_kpi_publisher.publisher.IMqttKpiPublisher;
import org.eclipse.paho.mqttv5.client.IMqttAsyncClient;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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
 * Class to read temperature off temperature sensor.
 */
public class KpiReader implements Runnable {

    private static final Random rnd = new Random();
    private static final ObjectMapper objMapper = new ObjectMapper();
    private IMqttAsyncClient client;
    private final IMqttKpiPublisher mqttKpiCollector;
    private final String clientId;
    private final String topic;
    /**
     * SLF4J logger using Log4j 2
     */
    private static final Logger logger = LoggerFactory.getLogger(KpiReader.class.getName());

    public KpiReader(IMqttAsyncClient client, String clientId, String topic, IMqttKpiPublisher mqttKpiCollector) {
        this.client = client;
        this.topic = topic;
        this.mqttKpiCollector = mqttKpiCollector;
        // ensure equipment number is non-null, otherwise exception is thrown
        this.clientId = Objects.requireNonNull(clientId);
    }

    /**
     * Reads temperature from sensor and sends it to MQTT message broker.
     */
    @Override
    public void run() {
        if (!client.isConnected()) {
            // client is not connected to MQTT message broker -> don't do anything
            return;
        }
        // read temperature from sensor
        var opt = readSensorKpi();
        MqttMessage msg;
        if(opt.isEmpty()) {
            logger.warn("No KPIs were read so no message will be sent.");
            return;
        }
        else msg = opt.get();
        // set quality of service: QoS=0 means message will be sent once and forgotten about if nobody receives it.
        // That is what we want here. It's not crucial if a single measurement is missing in thousands of measurements.
        // But there is a hugh performance boost using this asynchronous "call and forget" method.
        msg.setQos(0);
        //
        msg.setRetained(true);
        // publish message under given topic
        try {
            client.publish("/%s/%s".formatted(topic, clientId),msg);
            logger.debug("Publishing message {} to /{}/{}", msg, topic, clientId);
        } catch (MqttException e) {
            logger.warn("Message could not be published due to to an exception.", e);
        }
    }

    /**
     * Read KPIs.
     * @return message containing KPIs
     */
    private Optional<MqttMessage> readSensorKpi() {
        // generate KPI here
        Collection<Kpi> kpis = this.mqttKpiCollector.collectKpis();
        if(kpis != null){
            byte[] payload = new byte[0];
            var msg = new KpiMsg(clientId, kpis);
            try {
                payload = objMapper.writeValueAsBytes(msg);
            } catch (JsonProcessingException e) {
                logger.warn("Could not serialize message {}", msg.toString());
            }
            return Optional.of(new MqttMessage(payload));
        }
        else return Optional.empty();
    }

    public IMqttAsyncClient getClient() {
        return client;
    }

    public void setClient(IMqttAsyncClient client) {
        this.client = client;
    }

    @Override
    public String toString() {
        return "EngineTemperatureSensor{" +
                "client=" + client +
                ", TOPIC='" + topic + '\'' +
                ", EQUIPMENT_NO='" + clientId + '\'' +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KpiReader that = (KpiReader) o;
        return Objects.equals(client, that.client) && clientId.equals(that.clientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(client, clientId);
    }
}