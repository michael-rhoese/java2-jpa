/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package de.fherfurt.jpa.domains;

import lombok.Data;

/**
 * <h2>Address</h2>
 * <p>
 * {description}
 *
 * @author Michael Rhöse
 * @version 0.0.0.0, 05/02/2021
 */
@Data
public class Address {

    // Address Table Constants
    public static final String TABLE_NAME = "ADDRESS";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_STREET = "STREET";
    public static final String COLUMN_CITY = "CITY";
    public static final String COLUMN_ZIPCODE = "ZIPCODE";

    private Long id;
    private final String street;
    private final String city;
    private final String zipcode;
}
