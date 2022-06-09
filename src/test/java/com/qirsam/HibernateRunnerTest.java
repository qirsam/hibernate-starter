package com.qirsam;

import com.qirsam.entity.*;
import com.qirsam.util.HibernateTestUtil;
import com.qirsam.util.HibernateUtil;
import lombok.Cleanup;
import org.junit.jupiter.api.Test;

import javax.persistence.Column;
import javax.persistence.Table;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

class HibernateRunnerTest {

    @Test
    void checkHQL() {
        try (var sessionFactory = HibernateTestUtil.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();

            var result = session.createQuery(
                            "select u from User u join u.company c " +
                            "where u.personalInfo.firstname = :firstname and c.name = :companyName", User.class)
//                    "select u from User u where u.personalInfo.firstname = ?1", User.class)
                    .setParameter("firstname", "Sergey")
                    .setParameter("companyName", "Google")
//                    .setParameter(1, "Sergey")
                    .list();

            session.getTransaction().commit();
        }
    }

//    @Test
//    void checkH2() {
//        try (var sessionFactory = HibernateTestUtil.buildSessionFactory();
//             var session = sessionFactory.openSession()) {
//            session.beginTransaction();
//
//            var google = Company.builder()
//                    .name("Google")
//                    .build();
//            session.save(google);
//
//            var programmer = Programmer.builder()
//                    .username("qirsam@gmail.com")
//                    .language(Language.JAVA)
//                    .company(google)
//                    .build();
//            session.save(programmer);
//
//            var manager = Manager.builder()
//                    .username("alya@gmai.com")
//                    .projectName("Design")
//                    .company(google)
//                    .build();
//            session.save(manager);
//
//            session.flush();
//            session.clear();
//
//            var programmer1 = session.get(Programmer.class, 1L);
//            var manager1 = session.get(User.class, 2L);
//
//            session.getTransaction().commit();
//        }
//    }

    @Test
    void testInfo() {
        try (var sessionFactory = HibernateUtil.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();

//            var company = session.get(Company.class, 3);
//            company.getLocales().add(LocaleInfo.of("ru", "Описание на русском"));
//            company.getLocales().add(LocaleInfo.of("en", "Описание на English"));

            var company = session.get(Company.class, 3);
            company.getUsers().forEach((k, v) -> System.out.println(v));

            session.getTransaction().commit();
        }
    }

    @Test
    void checkManyToMany() {
        try (var sessionFactory = HibernateUtil.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();

            var user = session.get(User.class, 3L);

            var chat = session.get(Chat.class, 1L);

            var usersChat = UsersChat.builder()
//                    .createdAt(Instant.now())
//                    .createdBy(user.getUsername())
                    .build();

            usersChat.setUser(user);
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

//            var user = User.builder()
//                    .username("Test2@gmail.com")
//                    .build();
            User user = null;

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

       User alisa = null;
//        var alisa = User.builder()
//                .username("Alisa@ya.ru")
//                .build();

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
        User user = null;

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