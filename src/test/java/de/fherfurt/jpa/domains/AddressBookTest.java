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

import de.fherfurt.jpa.domains.errors.PersonNotFoundException;
import de.fherfurt.jpa.storages.AddressbookRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Michael Rhöse
 */
public class AddressBookTest {

    AddressbookRepository addressbookRepository;

    Person maxMustermann;
    Person emmaWeber;
    Person manfredHerold;

    private AddressBook prepareAddressBook() {
        final AddressBook res = new AddressBook();
        maxMustermann = new Person("Max", "Mustermann", "max.mustermann@email.de");
        emmaWeber = new Person("Emma", "Weber", "emma.mueller@post.de");
        manfredHerold = new Person("Manfred", "Herold", "manfred@herold.de");

        res.addContact(maxMustermann);
        res.addContact(emmaWeber);
        res.addContact(manfredHerold);

        return res;
    }

    @Test
    void addressbook_should_store_persons_with_name_surename_email() throws PersonNotFoundException {
        // GIVEM
        final AddressBook addressBook = prepareAddressBook();

        // WHEN
        Person loadedMustermann = addressBook.getContactByLastName("Mustermann");
        Person loadedWeber = addressBook.getContactByLastName("Weber");
        Person loadedHerold = addressBook.getContactByLastName("Herold");

        // THEN
        Assertions.assertThat(loadedMustermann).isEqualTo(maxMustermann);
        Assertions.assertThat(loadedWeber).isEqualTo(emmaWeber);
        Assertions.assertThat(loadedHerold).isEqualTo(manfredHerold);
    }

    @Test
    void should_throw_person_not_found_exception_with_unknown_person() {
        // GIVEM
        final AddressBook addressBook = prepareAddressBook();

        // WHEN
        Throwable result = Assertions.catchThrowable(() -> addressBook.getContactByLastName("Gates"));

        // THEN
        Assertions.assertThat(result).isInstanceOf(PersonNotFoundException.class);
    }

    @Test
    void persons_should_be_sorted_ascending_according_to_lastname() {
        // GIVEM
        final AddressBook addressBook = prepareAddressBook();

        // WHEN
        Person result = addressBook.getPersonByIndex(0);

        // THEN
        Assertions.assertThat(addressBook.getSize())
                .overridingErrorMessage("After adding 3 persons, the address book should contain 3 persons.")
                .isEqualTo(3);

        Assertions.assertThat(result).isEqualTo(manfredHerold);
    }

    @Test
    void addressBook_should_have_overview_on_how_often_lastnames_occur_in_it() {
        // GIVEM
        final AddressBook addressBook = prepareAddressBook();

        final long frequencyBefore = addressBook.getFrequencyOfLastName("Mustermann");

        // WHEN
        addressBook.addContact(new Person("Moritz", "Mustermann", "foo@bar.com"));
        addressBook.addContact(new Person("Tobias", "Mustermann", "foo@bar.com"));
        addressBook.addContact(new Person("Matze", "Mustermann", "foo@bar.com"));

        final long result = addressBook.getFrequencyOfLastName("Mustermann");

        // THEN
        Assertions.assertThat(result).isGreaterThan(frequencyBefore).isEqualTo(4);
    }

    @Test
    void person_should_have_a_human_readable_toString_output() {
        // GIVEM
        Person felixMeyer = new Person("Felix", "Meyer", "felix@meyer.io");

        // WHEN
        final String result = felixMeyer.toString();

        // THEN
        Assertions.assertThat(result).isEqualTo("Felix Meyer (felix@meyer.io),  No address");
    }
}
