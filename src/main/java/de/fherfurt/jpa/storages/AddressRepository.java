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
 * <h2>AddressRepository</h2>
 * <p>
 * {description}
 *
 * @author Michael Rhöse
 * @version 0.0.0.0, 05/02/2021
 */
@Slf4j
public class AddressRepository {

    private static final String INSERT_ADDRESS
            = "insert into " + Address.TABLE_NAME + " ("
            + Address.COLUMN_STREET + ", "
            + Address.COLUMN_CITY + ", "
            + Address.COLUMN_ZIPCODE + " ) VALUES ( ?, ?, ?)";

    private static final String UPDATE_ADDRESS
            = "update " + Address.TABLE_NAME + " set "
            + Address.COLUMN_STREET + " = ?, "
            + Address.COLUMN_CITY + " = ?, "
            + Address.COLUMN_ZIPCODE + " = ? where " + Address.COLUMN_ID + " = ?";

    private static final String SELECT_ALL_FROM_ADDRESS = "select * from " + Address.TABLE_NAME;

    private static final String DELETE_ALL = "delete from " + Address.TABLE_NAME;

    public Long save(Address address) {
        if (Objects.isNull(address)) {
            return -1L;
        }

        try {
            H2Controller.getManager().getConnection().setAutoCommit(false);

            boolean newAddress = Objects.isNull(address.getId());

            final PreparedStatement statement = H2Controller.getManager().getConnection().prepareStatement((newAddress) ? INSERT_ADDRESS : UPDATE_ADDRESS, new String[]{"ID"});

            statement.setString(1, address.getStreet());
            statement.setString(2, address.getCity());
            statement.setString(3, address.getZipcode());

            if (newAddress) {
                statement.executeUpdate();
                ResultSet generatedKeys = statement.getGeneratedKeys();
                generatedKeys.next();
                Long personId = generatedKeys.getLong(1);
                address.setId(personId);
                LOGGER.info("Saved Address [" + address + "] with id [" + personId + "]");
            } else {
                statement.setLong(4, address.getId());
                statement.executeUpdate();
                LOGGER.info("Updated Address [" + address + "]");
            }
            H2Controller.getManager().getConnection().setAutoCommit(true);

        } catch (SQLException throwables) {
            throw new SqlException("Error while saving Address", throwables);
        }

        return address.getId();
    }

    public List<Address> findAll() {
        final List<Address> res = new ArrayList<>();

        try {
            final Statement selectAddresssStatement = H2Controller.getManager().getConnection().createStatement();
            final ResultSet personsResultSet = selectAddresssStatement.executeQuery(SELECT_ALL_FROM_ADDRESS);
            while (personsResultSet.next()) {
                String street = personsResultSet.getString(Address.COLUMN_STREET);
                String city = personsResultSet.getString(Address.COLUMN_CITY);
                String zipcode = personsResultSet.getString(Address.COLUMN_ZIPCODE);

                final Address address = new Address(street, city, zipcode);

                address.setId(personsResultSet.getLong(Address.COLUMN_ID));

                LOGGER.info("Loaded Address from Database [" + address + "]");
                res.add(address);
            }
        } catch (SQLException e) {
            LOGGER.error("Error while reading Addressbook from Database " + e.getMessage());
            throw new SqlException("Error while reading Addressbook from Database " + e.getMessage(), e);
        }

        return res;
    }

    public Optional<Address> findBy(Long id) {

        if (Objects.isNull(id) || id == -1) {
            return Optional.empty();
        }

        try {
            final Statement selectAddresssStatement = H2Controller.getManager().getConnection().createStatement();
            final ResultSet personsResultSet = selectAddresssStatement.executeQuery(SELECT_ALL_FROM_ADDRESS + " WHERE " + Address.COLUMN_ID + " = '" + id + "'");
            personsResultSet.next();

            String street = personsResultSet.getString(Address.COLUMN_STREET);
            String city = personsResultSet.getString(Address.COLUMN_CITY);
            String zipcode = personsResultSet.getString(Address.COLUMN_ZIPCODE);

            final Address address = new Address(street, city, zipcode);

            address.setId(personsResultSet.getLong(Address.COLUMN_ID));

            LOGGER.info("Loaded Address from Database [" + address + "]");
            return Optional.of(address);
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
