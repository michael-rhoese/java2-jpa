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

import de.fherfurt.jpa.domains.Person;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Michael Rhöse
 */
class PersonRepositoryTest {

    PersonRepository repository;

    @BeforeEach
    public void beforeEach() {
        repository = new PersonRepository();
    }

    @AfterEach
    public void afterEach() {
        repository.deleteAll();
    }

    @Test
    void save() {
        // GIVEN
        Person given = new Person("Hans", "Musterfrau", "test@gmx.com", null);

        // WHEN
        Long result = repository.save(given);

        // THEN
        Assertions.assertThat(result)
                .isNotNull()
                .isGreaterThan(0);
    }

    @Test
    void findAll() {
        // GIVEN
        Person given1 = new Person("Hans", "Musterfrau", "test@gmx.com", null);
        Person given2 = new Person("Frauke", "Mustermann", "test2@gmx.com", null);

        List<Long> idsOfPersisted = new ArrayList<>();
        idsOfPersisted.add(repository.save(given1));
        idsOfPersisted.add(repository.save(given2));

        // WHEN
        List<Person> result = repository.findAll();

        // WHEN
        Assertions.assertThat(result).isNotNull().isNotEmpty().allMatch(Objects::nonNull);
        Assertions.assertThat(idsOfPersisted).isNotNull().isNotEmpty().allMatch(Objects::nonNull);
    }

    @Test
    void findByName() {
        // GIVEN
        Person given1 = new Person("Hans", "Musterfrau", "test@gmx.com", null);
        Person given2 = new Person("Frauke", "Mustermann", "test2@gmx.com", null);

        repository.save(given1);
        repository.save(given2);

        // WHEN
        Optional<Person> result = repository.findBy("Mustermann");

        // WHEN
        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getFirstName()).isEqualTo("Frauke");
        Assertions.assertThat(result.get().getLastName()).isEqualTo("Mustermann");
    }
}
