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

import de.fherfurt.jpa.domains.Address;
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
        new AddressRepository().deleteAll();
    }

    @Test
    void save() {
        // GIVEN
        Person given = new Person("Hans", "Musterfrau", "test@gmx.com");

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
        Person given1 = new Person("Hans", "Musterfrau", "test@gmx.com");
        Person given2 = new Person("Frauke", "Mustermann", "test2@gmx.com");

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
        Person given1 = new Person("Hans", "Musterfrau", "test@gmx.com");
        Person given2 = new Person("Frauke", "Mustermann", "test2@gmx.com");

        repository.save(given1);
        repository.save(given2);

        // WHEN
        Optional<Person> result = repository.findBy("Mustermann");

        // WHEN
        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getFirstName()).isEqualTo("Frauke");
        Assertions.assertThat(result.get().getLastName()).isEqualTo("Mustermann");
    }

    @Test
    void findByNameWithAddress() {
        // GIVEN
        Person given1 = new Person("Hans", "Musterfrau", "test@gmx.com");
        Address address1 = new Address("Leutragraben 1", "Jena", "07745");

        given1.setAddress(address1);

        Person given2 = new Person("Frauke", "Mustermann", "test2@gmx.com");
        Address address2 = new Address("Anger 24", "Erfurt", "99084");

        given2.setAddress(address2);

        repository.save(given1);
        repository.save(given2);

        // WHEN
        Optional<Person> result = repository.findBy("Mustermann");

        // WHEN
        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getFirstName()).isEqualTo("Frauke");
        Assertions.assertThat(result.get().getLastName()).isEqualTo("Mustermann");
        Assertions.assertThat(result.get().getAddress()).isNotNull();

        Address loadedAddress = result.get().getAddress();
        Assertions.assertThat(loadedAddress.getStreet()).isEqualTo("Anger 24");
        Assertions.assertThat(loadedAddress.getCity()).isEqualTo("Erfurt");
    }
}
