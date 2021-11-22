package de.othr.mqtt_kpi_publisher.helpers;
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

import de.othr.mqtt_kpi_publisher.custom_return_vals.ValidityChecker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

    /**
     * Check if ClientId is valid.
     * A client ID must be <em>strLength</em> characters long and must follow the <em>pattern</em>.
     * @param clientId client ID to be checked
     * @param strLength desired length of client ID
     * @param pattern pattern (RegEx) the String must follow
     * @return true if valid, false otherwise
     */
    public static ValidityChecker isClientIdValid(String clientId, int strLength, Pattern pattern){
        if(clientId.length() != strLength) return ValidityChecker.invalid("Client ID must be exactly %d characters long".formatted(strLength));
        Matcher m = pattern.matcher(clientId);
        if(m.matches()) return ValidityChecker.valid();
        else return ValidityChecker.invalid("Client ID must conform to the pattern %s".formatted(pattern.toString()));
    }
}
