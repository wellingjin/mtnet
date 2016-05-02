/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.bluetooth.le;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    public static String TEST_0="00002a00-0000-1000-8000-00805f9b34fb";//read
    public static String TEST_1="00002a01-0000-1000-8000-00805f9b34fb";//read
    public static String TEST_2="00002a02-0000-1000-8000-00805f9b34fb";//read|write
    public static String TEST_3="00002a03-0000-1000-8000-00805f9b34fb";//write
    public static String TEST_4="00002a04-0000-1000-8000-00805f9b34fb";//read
    
    public static String TEST_5="00002a05-0000-1000-8000-00805f9b34fb";//indicate
    public static String TEST_5_1="00002902-0000-1000-8000-00805f9b34fb";
    
    public static String TEST_6="0000fff1-0000-1000-8000-00805f9b34fb";//read|write
    public static String TEST_6_1="00002901-0000-1000-8000-00805f9b34fb";
    public static String TEST_7="0000fff2-0000-1000-8000-00805f9b34fb";//notify
    public static String TEST_7_1="00002902-0000-1000-8000-00805f9b34fb";
    public static String TEST_7_2="00002901-0000-1000-8000-00805f9b34fb";
    public static String TEST_8="0000fff3-0000-1000-8000-00805f9b34fb";//read|write
    public static String TEST_8_1="00002901-0000-1000-8000-00805f9b34fb";


    static {
        // Sample Services.
        attributes.put("00001800-0000-1000-8000-00805f9b34fb", "Service01");
        attributes.put("00001801-0000-1000-8000-00805f9b34fb", "Service02");        
        attributes.put("0000fff0-0000-1000-8000-00805f9b34fb", "Service03");
        // Sample Characteristics.
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}















