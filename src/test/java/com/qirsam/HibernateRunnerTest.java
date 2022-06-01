package com.qirsam;

import com.qirsam.entity.*;
import com.qirsam.util.HibernateUtil;
import lombok.Cleanup;
import org.junit.jupiter.api.Test;

import javax.persistence.Column;
import javax.persistence.Table;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

class HibernateRunnerTest {

    @Test
    void checkManyToMany() {
        try (var sessionFactory = HibernateUtil.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();

            var user = session.get(User.class, 3L);

            var chat = session.get(Chat.class, 1L);

            var usersChat = UsersChat.builder()
                    .createdAt(Instant.now())
                    .createdBy(user.getUsername())
                    .build();

            usersChat.setUser(user);
            usersChat.setChat(chat);
//
            session.save(usersChat);
//
//            var chat = Chat.builder()
//                    .name("qirsam")
//                    .build();
//
//            user.addChat(chat);
//
//            session.save(chat);

            session.getTransaction().commit();
        }
    }

    @Test
    void checkOneToOne() {
        try (var sessionFactory = HibernateUtil.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();

            var user = User.builder()
                    .username("Test2@gmail.com")
                    .build();

            var profile = Profile.builder()
                    .languages("ru")
                    .street("Bogdanova 77")
                    .build();

            profile.setUser(user);

            session.save(user);
//            profile.setUser(user);

            session.getTransaction().commit();
        }
    }

    @Test
    void checkOrphanRemoval() {
        try (var sessionFactory = HibernateUtil.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();

//            Company company = session.getReference(Company.class, 3);
//            company.getUsers().removeIf(user -> user.getId().equals(5L));

            var user = session.get(User.class, 7L);
            session.remove(user);

            session.getTransaction().commit();
        }
    }

    @Test
    void checkLazyInitializations() {
        Company company = null;
        try (var sessionFactory = HibernateUtil.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();

            company = session.get(Company.class, 3);

            session.getTransaction().commit();
        }
        var users = company.getUsers();
        System.out.println(users.size());
    }

    @Test
    void addUserToNewCompany() {
        @Cleanup var sessionFactory = HibernateUtil.buildSessionFactory();
        @Cleanup var session = sessionFactory.openSession();
        session.beginTransaction();

        var company = Company.builder()
                .name("Facebook")
                .build();

        var alisa = User.builder()
                .username("Alisa@ya.ru")
                .build();

        company.addUser(alisa);

        session.save(company);

        session.getTransaction().commit();
    }

    @Test
    void deleteCompany() {
        @Cleanup var sessionFactory = HibernateUtil.buildSessionFactory();
        @Cleanup var session = sessionFactory.openSession();
        session.beginTransaction();

        var company = session.get(Company.class, 2);

        session.delete(company);

        session.getTransaction().commit();
    }

    @Test
    void OneToMany() {
        @Cleanup var sessionFactory = HibernateUtil.buildSessionFactory();
        @Cleanup var session = sessionFactory.openSession();
        session.beginTransaction();

        var company = session.get(Company.class, 3);
        System.out.println(company.getUsers());

        session.getTransaction().commit();
    }

    @Test
    void checkGetReflection() throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.getString("firstname");
        resultSet.getString("lastname");
        resultSet.getString("username");

        var clazz = User.class;
    }

    @Test
    void checkReflectionApi() {
        var user = User.builder()
                .build();

        String sql = """
                insert
                into
                %s
                (%s)
                values
                (%s)
                """;
        var tableName = Optional.ofNullable(user.getClass().getAnnotation(Table.class))
                .map(tableAnnotation -> tableAnnotation.schema() + "." + tableAnnotation.name())
                .orElse(user.getClass().getName());

        var declaredFields = user.getClass().getDeclaredFields();

        var columnNames = Arrays.stream(declaredFields)
                .map(field -> Optional.ofNullable(field.getAnnotation(Column.class))
                        .map(Column::name)
                        .orElse(field.getName()))
                .collect(Collectors.joining(", "));

        var columnValues = Arrays.stream(declaredFields)
                .map(field -> "?")
                .collect(Collectors.joining(", "));

        System.out.println(sql.formatted(tableName, columnNames, columnValues));


    }

}