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
class AddressRepositoryTest {

    AddressRepository repository;

    @BeforeEach
    public void beforeEach() {
        repository = new AddressRepository();
    }

    @AfterEach
    public void afterEach() {
        repository.deleteAll();
    }

    @Test
    void save() {
        // GIVEN
        Address given = new Address("Leutragraben 1", "Jena", "07745");

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
        Address given1 = new Address("Leutragraben 1", "Jena", "07745");
        Address given2 = new Address("Anger 24 ", "Erfurt", "99084");

        List<Long> idsOfPersisted = new ArrayList<>();
        idsOfPersisted.add(repository.save(given1));
        idsOfPersisted.add(repository.save(given2));

        // WHEN
        List<Address> result = repository.findAll();

        // WHEN
        Assertions.assertThat(result).isNotNull().isNotEmpty().allMatch(Objects::nonNull);
        Assertions.assertThat(idsOfPersisted).isNotNull().isNotEmpty().allMatch(Objects::nonNull);
    }

    @Test
    void findByName() {
        // GIVEN
        Address given1 = new Address("Leutragraben 1", "Jena", "07745");
        Address given2 = new Address("Anger 24 ", "Erfurt", "99084");

        repository.save(given1);
        repository.save(given2);

        // WHEN
        Optional<Address> result = repository.findBy(given1.getId());

        // WHEN
        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getStreet()).isEqualTo("Leutragraben 1");
        Assertions.assertThat(result.get().getCity()).isEqualTo("Jena");
    }
}
