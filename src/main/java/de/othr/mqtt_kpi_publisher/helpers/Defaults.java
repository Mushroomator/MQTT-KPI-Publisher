package de.othr.mqtt_kpi_publisher.helpers;/*
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
import java.util.regex.Pattern;

/**
 * Default values for certain configurations.
 */
public class Defaults {
    public static final long INITIAL_TASK_DELAY = 0;
    public static final long TASK_INTERVAL = 5000;
    public static final int MQTT_CONNECTION_TIMEOUT = 10;
    public static final int CHARS_CLIENT_ID = 10;
    public static final Pattern CLIENT_ID_PATTERN = Pattern.compile("[a-zA-Z0-9]");
}
