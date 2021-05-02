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
import de.fherfurt.jpa.core.errors.SqlException;
import de.fherfurt.jpa.domains.Address;
import de.fherfurt.jpa.domains.Person;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
public class PersonRepository {

    private static final String INSERT_PERSON
            = "insert into " + Person.TABLE_NAME + " ("
            + Person.COLUMN_LASTNAME + ", "
            + Person.COLUMN_FIRSTNAME + ", "
            + Person.COLUMN_MAIL + ", "
            + Person.COLUMN_ADDRESS + " ) VALUES ( ?, ?, ?, ?)";

    private static final String UPDATE_PERSON
            = "update " + Person.TABLE_NAME + " set "
            + Person.COLUMN_LASTNAME + " = ?, "
            + Person.COLUMN_FIRSTNAME + " = ?, "
            + Person.COLUMN_MAIL + " = ?, "
            + Person.COLUMN_ADDRESS + " = ? where " + Person.COLUMN_ID + " = ?";

    private static final String SELECT_ALL_FROM_PERSON = "select * from " + Person.TABLE_NAME;

    private static final String DELETE_ALL = "delete from " + Person.TABLE_NAME;

    private final AddressRepository addressRepository = new AddressRepository();

    public Long save(Person person) {
        try {
            H2Controller.getManager().getConnection().setAutoCommit(false);

            boolean newPerson = Objects.isNull(person.getId());

            final PreparedStatement statement = H2Controller.getManager().getConnection().prepareStatement((newPerson) ? INSERT_PERSON : UPDATE_PERSON, new String[]{"ID"});

            Long addressId = addressRepository.save(person.getAddress());
            statement.setString(1, person.getLastName());
            statement.setString(2, person.getFirstName());
            statement.setString(3, person.getEMail());
            statement.setLong(4, addressId);

            if (newPerson) {
                statement.executeUpdate();
                ResultSet generatedKeys = statement.getGeneratedKeys();
                generatedKeys.next();
                Long personId = generatedKeys.getLong(1);
                person.setId(personId);
                LOGGER.info("Saved Person [" + person + "] with id [" + personId + "]");
            } else {
                statement.setLong(5, person.getId());
                statement.executeUpdate();
                LOGGER.info("Updated Person [" + person + "]");
            }
            H2Controller.getManager().getConnection().setAutoCommit(true);

        } catch (SQLException throwables) {
            throw new SqlException("Error while saving Person", throwables);
        }

        return person.getId();
    }

    public List<Person> findAll() {
        final List<Person> res = new ArrayList<>();

        try {
            final Statement selectPersonsStatement = H2Controller.getManager().getConnection().createStatement();
            final ResultSet personsResultSet = selectPersonsStatement.executeQuery(SELECT_ALL_FROM_PERSON);
            while (personsResultSet.next()) {
                String firstname = personsResultSet.getString(Person.COLUMN_FIRSTNAME);
                String lastname = personsResultSet.getString(Person.COLUMN_LASTNAME);
                String email = personsResultSet.getString(Person.COLUMN_MAIL);

                final Person person = new Person(firstname, lastname, email);

                person.setId(personsResultSet.getLong(Person.COLUMN_ID));

                Optional<Address> loadedAddress = addressRepository.findBy(personsResultSet.getLong(Person.COLUMN_ADDRESS));
                if (loadedAddress.isPresent()) {
                    person.setAddress(loadedAddress.get());
                }

                LOGGER.info("Loaded Person from Database [" + person + "]");
                res.add(person);
            }
        } catch (SQLException e) {
            LOGGER.error("Error while reading Addressbook from Database " + e.getMessage());
            throw new SqlException("Error while reading Addressbook from Database " + e.getMessage(), e);
        }

        return res;
    }

    public Optional<Person> findBy(String name) {
        try {
            final Statement selectPersonsStatement = H2Controller.getManager().getConnection().createStatement();
            final ResultSet personsResultSet = selectPersonsStatement.executeQuery(SELECT_ALL_FROM_PERSON + " WHERE " + Person.COLUMN_LASTNAME + " = '" + name + "'");
            personsResultSet.next();

            String firstname = personsResultSet.getString(Person.COLUMN_FIRSTNAME);
            String lastname = personsResultSet.getString(Person.COLUMN_LASTNAME);
            String email = personsResultSet.getString(Person.COLUMN_MAIL);

            final Person person = new Person(firstname, lastname, email);

            person.setId(personsResultSet.getLong(Person.COLUMN_ID));

            Optional<Address> loadedAddress = addressRepository.findBy(personsResultSet.getLong(Person.COLUMN_ADDRESS));
            if (loadedAddress.isPresent()) {
                person.setAddress(loadedAddress.get());
            }

            LOGGER.info("Loaded Person from Database [" + person + "]");
            return Optional.of(person);
        } catch (SQLException e) {
            LOGGER.error("Error while reading Addressbook from Database " + e.getMessage());
            throw new SqlException("Error while reading Addressbook from Database " + e.getMessage(), e);
        }
    }

    public int deleteAll() {
        try {
            final Statement selectPersonsStatement = H2Controller.getManager().getConnection().createStatement();
            return selectPersonsStatement.executeUpdate(DELETE_ALL);
        } catch (SQLException e) {
            LOGGER.error("Error while deleting data from Addressbook " + e.getMessage());
            throw new SqlException("Error while deleting data from Addressbook " + e.getMessage(), e);
        }
    }
}
