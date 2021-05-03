package de.fherfurt.jpa.core;

import de.fherfurt.jpa.core.errors.SqlException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.h2.jdbcx.JdbcDataSource;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
/**
 * <h2>H2Controller</h2>
 * <p>
 *
 * @author Michael Rhöse
 * @version 0.0.0.0 04/25/2021
 */
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class H2Controller {

    private static H2Controller instance;

    private final EntityManagerFactory factory;

    public static H2Controller getManager() {
        if (Objects.isNull(instance)) {
            instance = new H2Controller(Persistence.createEntityManagerFactory("de.fherfurt.jpa"));
        }

        return instance;
    }

    public EntityManager getEntityManager(){
        return factory.createEntityManager();
    }
}
