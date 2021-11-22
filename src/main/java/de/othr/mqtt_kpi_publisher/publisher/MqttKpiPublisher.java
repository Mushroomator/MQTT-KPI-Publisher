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

import de.othr.mqtt_kpi_publisher.helpers.Defaults;
import de.othr.mqtt_kpi_publisher.helpers.Utils;
import de.othr.mqtt_kpi_publisher.helpers.Validator;
import de.othr.mqtt_kpi_publisher.kpi.KpiReader;
import org.eclipse.paho.mqttv5.client.*;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * MQTT Client which connects reads and publishes KPIs to a MQTT message broker
 * @author Thomas Pilz
 */
public class MqttKpiPublisher {


    /**
     * Executor service to run tasks asynchronously and periodically.
     *
     * Java Executor Service tutorial: https://www.baeldung.com/java-executor-service-tutorial
     */
    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    /**
     * SLF4J logger using Log4j 2
     */
    private static final Logger logger = LoggerFactory.getLogger(MqttKpiPublisher.class.getName());

    /**
     * Reference to singleton instance
     */
    private static MqttKpiPublisher instance;

    /**
     * Options for application
     */
    private final MqttKpiPublisherOptions mqttKpiPublisherOptions;

    private final IMqttKpiPublisher mqttKpiCollector;

    /**
     * Start MQTT KPI collector with default options.
     */
    public static void runMqttKpiCollector(IMqttKpiPublisher mqttKpiCollector){
        // start application with default arguments
        MqttKpiPublisher.runMqttKpiCollector(mqttKpiCollector, null);
    }


    /**
     * Start MQTT KPI
     * @param mqttKpiPublisher code to execute to read KPIs
     * @param options options for MQTT connection
     */
    public static void runMqttKpiCollector(IMqttKpiPublisher mqttKpiPublisher, MqttKpiPublisherOptions options) {
        if(instance == null) instance = new MqttKpiPublisher(mqttKpiPublisher, options);

    }

    /**
     * Create instance of MQTT KPI Collector, start reading KPIs periodically and transmit them to a given MQTT message broker
     * @param options options for application
     */
    private MqttKpiPublisher(IMqttKpiPublisher mqttKpiCollector, MqttKpiPublisherOptions options) {
        this.mqttKpiCollector = mqttKpiCollector;
        this.mqttKpiPublisherOptions = mergeOptions(readOptsFromEnv(), options);
        // initialize application
        logger.info("Starting MQTT KPI Collector with options {}", mqttKpiPublisherOptions.toString());
        handleSignals();
        connect2MqttMsgBroker((mqttAsyncClient, iMqttToken) -> {
            var kpiReader = new KpiReader(
                    mqttAsyncClient,
                    this.mqttKpiPublisherOptions.getMqttClientId(),
                    this.mqttKpiPublisherOptions.getMqttTopic(),
                    this.mqttKpiCollector
            );
            // run task periodically
            executorService.scheduleWithFixedDelay(kpiReader,
                    this.mqttKpiPublisherOptions.getInitialTaskDelay(),
                    this.mqttKpiPublisherOptions.getTaskInterval(),
                    TimeUnit.MILLISECONDS);
        }, null);
    }

    /**
     * Connect to MQTT Message Broker
     */
    private void connect2MqttMsgBroker(){
        connect2MqttMsgBroker(null, null);
    }

    /**
     * Connect to MQTT Message Broker
     * @param iOnMqttConFailed callback if connection could not be established
     */
    private void connect2MqttMsgBroker(IOnMqttConFailed iOnMqttConFailed){
        connect2MqttMsgBroker(null, iOnMqttConFailed);
    }

    /**
     * Connect to MQTT Message Broker
     * @param iOnMqttConSuccess callback if connection was successfully established
     */
    private void connect2MqttMsgBroker(IOnMqttConSuccess iOnMqttConSuccess){
        connect2MqttMsgBroker(iOnMqttConSuccess, null);
    }

    /**
     * Connect to MQTT Message Broker
     * @param iOnMqttConSuccess callback if connection was successfully established
     * @param iOnMqttConFailed callback if connection could not be established
     */
    private void connect2MqttMsgBroker(IOnMqttConSuccess iOnMqttConSuccess, IOnMqttConFailed iOnMqttConFailed){
        var persistence = new MemoryPersistence();
        // Set MQTT connection options
        MqttConnectionOptions options = new MqttConnectionOptionsBuilder()
                .automaticReconnect(true)
                .connectionTimeout(mqttKpiPublisherOptions.getMqttConnectionTimeout())
                .cleanStart(true)
                .build();
        try {
            // Use async MQTT client for better performance/ non-blocking operations
            var client = new MqttAsyncClient(mqttKpiPublisherOptions.getMqttMsgBrokerUrl(), mqttKpiPublisherOptions.getMqttClientId(), persistence);
            client.connect(options, null, new MqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {
                    logger.info("Successfully connected to {}.", mqttKpiPublisherOptions.getMqttMsgBrokerUrl());
                    // Run callback
                    if(iOnMqttConSuccess != null) iOnMqttConSuccess.run(client, iMqttToken);
                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                    logger.error("Failed to connect to {}.", mqttKpiPublisherOptions.getMqttMsgBrokerUrl(), throwable);
                    // Run callback
                    if(iOnMqttConFailed != null) iOnMqttConFailed.run(iMqttToken, throwable);
                }
            });
        } catch (MqttException e){
            logger.warn("Failed to connect due to exception.", e);
        }
    }


    /**
     * Read options from environment variables
     * @return all options as set by environment variables
     */
    private MqttKpiPublisherOptions readOptsFromEnv(){
        // Read mandatory options from environment variables
        var mqttMsgBrokerUrl = System.getenv("MQTT_MSG_BROKER_URL");
        var mqttClientId = System.getenv("MQTT_CLIENT_ID");
        var mqttTopic = System.getenv("MQTT_TOPIC");

        // Build minimal options
        var builder = new MqttKpiPublisherOptions.Builder(mqttClientId, mqttMsgBrokerUrl, mqttTopic);

        // Enrich minimal options with other options if provided
        var mqttConnectionTimeout = Utils.parseIntNullable(System.getenv("MQTT_CONNECTION_TIMEOUT"));
        if(mqttConnectionTimeout != null) builder.setMqttConnectionTimeout(mqttConnectionTimeout);

        var taskInterval = Utils.parseLongNullable(System.getenv("TASK_INTERVAL"));
        if(taskInterval != null) builder.setTaskInterval(taskInterval);

        var initialTaskDelay = Utils.parseLongNullable(System.getenv("INITIAL_TASK_DELAY"));
        if(initialTaskDelay != null) builder.setInitialTaskDelay(initialTaskDelay);

        // build options object
        return builder.build();
    }

    /**
     * Merge options provided by user programmatically via arguments with those read from environment variables
     * @param envOpts options read from environment variables
     * @param argOpts options provided as arguments
     * @return merged options with environment variables taking precedence over options provided by arguments
     */
    private MqttKpiPublisherOptions mergeOptions(MqttKpiPublisherOptions envOpts, MqttKpiPublisherOptions argOpts){
        // options must be provided
        if(envOpts == null && argOpts == null) throw new IllegalArgumentException("You must provide mandatory options either by using environment variables or by creating an instance of MqttKpiPublisherOptions!");
        // if only argument options are passed all required params are already set to we can use the argOpts
        if(envOpts == null) return argOpts;
        // if no arg options are passed -> create arg opts with null values so there is no NullPointerException (when accessing argOpts!), env values will take preference anyway; if no env values are supplied the application will exit anyway
        if(argOpts == null) argOpts = new MqttKpiPublisherOptions.Builder(null, null, null).build();
        // MANDATORY OPTIONS
        // MQTT Client ID
        String mqttClientId;
        if(envOpts.getMqttClientId() == null){
            if(argOpts.getMqttClientId() == null){
                logger.error("Mandatory MQTT client ID was not provided.");
                throw new IllegalArgumentException("You must provide a unique value for MQTT client ID. You can do so by explicitly creating a options object or providing a value for environment variable MQTT_CLIENT_ID!");
            }
            mqttClientId = argOpts.getMqttClientId();
        }
        else mqttClientId = envOpts.getMqttClientId();
        // validate set client-ID
        var validationResult = Validator.isClientIdValid(mqttClientId, Defaults.CHARS_CLIENT_ID, Defaults.CLIENT_ID_PATTERN);
        if(!validationResult.isValid()) throw new IllegalArgumentException(validationResult.getErrMsg());

        // MQTT Message Broker URL
        String mqttMsgBrokerUrl;
        if(envOpts.getMqttMsgBrokerUrl() == null){
            if(argOpts.getMqttMsgBrokerUrl() == null){
                logger.error("Mandatory URL to MQTT message broker was not provided.");
                throw new IllegalArgumentException("You must provide a value for the URL to the MQTT message broker. You can do so by explicitly creating a options object or providing a value for environment variable MQTT_MSG_BROKER_URL!");
            }
            mqttMsgBrokerUrl = argOpts.getMqttMsgBrokerUrl();
        }
        else mqttMsgBrokerUrl = envOpts.getMqttMsgBrokerUrl();

        // MQTT Topic
        String mqttTopic;
        if(envOpts.getMqttTopic() == null){
            if(argOpts.getMqttTopic() == null){
                logger.error("Mandatory MQTT topic was not provided.");
                throw new IllegalArgumentException("You must provide a value for MQTT topic. You can do so by explicitly creating a options object or providing a value for environment variable MQTT_TOPIC!");
            }
            mqttTopic = argOpts.getMqttTopic();
        }
        else mqttTopic = envOpts.getMqttTopic();

        var builder = new MqttKpiPublisherOptions.Builder(mqttClientId, mqttMsgBrokerUrl, mqttTopic);

        // OPTIONAL OPTIONS
        if(envOpts.getInitialTaskDelay() != null) builder.setInitialTaskDelay(envOpts.getInitialTaskDelay());
        else if (argOpts.getInitialTaskDelay() != null) builder.setInitialTaskDelay(envOpts.getInitialTaskDelay());

        if(envOpts.getTaskInterval() != null) builder.setTaskInterval(envOpts.getTaskInterval());
        else if (argOpts.getTaskInterval() != null) builder.setTaskInterval(envOpts.getTaskInterval());

        if(envOpts.getMqttConnectionTimeout() != null) builder.setMqttConnectionTimeout(envOpts.getMqttConnectionTimeout());
        else if (argOpts.getMqttConnectionTimeout() != null) builder.setMqttConnectionTimeout(envOpts.getMqttConnectionTimeout());

        return builder.build();
    }

    /**
     * Register signal handlers for this process.
     * Very important to handle signals forwarded by Docker to this process which will be running with PID 1.
     * More details on docker signal handling: https://www.kaggle.com/residentmario/best-practices-for-propagating-signals-on-docker
     */
    private void handleSignals(){
        // handle "SIGTERM"
        handleSigterm();
    }

    /**
     * Handle "SIGTERM" signal, to gracefully shut down this process.
     */
    private void handleSigterm(){
        Thread shutdownHook = new Thread(() -> {
            logger.info("JVM will be shutdown. Shutting down MQTT client with ID {}...", mqttKpiPublisherOptions.getMqttClientId());
            // shutdown executor service gracefully
            executorService.shutdown();
            try {
                // wait 10% longer than an interval takes, if the tasks are not finished by then shut the service down immediately
                if (!executorService.awaitTermination((long)(mqttKpiPublisherOptions.getTaskInterval() * 1.1), TimeUnit.MILLISECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }
            logger.info("MQTT client with ID {} was shutdown.", mqttKpiPublisherOptions.getMqttClientId());
        });
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

}
