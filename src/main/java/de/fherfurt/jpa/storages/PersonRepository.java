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
package de.fherfurt.jpa.storages;

import de.fherfurt.jpa.core.H2Controller;
import de.fherfurt.jpa.domains.Person;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * <h2>PersonRepository</h2>
 * <p>
 * {description}
 *
 * @author Michael Rhöse
 * @version 0.0.0.0, 05/02/2021
 */
@Slf4j
public class PersonRepository extends BaseRepository<Person> {

    public PersonRepository() {
        super(Person.class, H2Controller.getManager().getEntityManager());
    }

    public Optional<Person> findBy(String name) {

        final String sql = SELECT_FROM + type.getCanonicalName() + " entity WHERE entity.lastName = :lastname";
        TypedQuery<Person> query = entityManager.createQuery(sql, type);
        query.setParameter("lastname", name);
        List<Person> res = new ArrayList<>(query.getResultList());

        if (res.size() > 0) {
            return Optional.of(res.get(0));
        }

        return Optional.empty();
    }
}
