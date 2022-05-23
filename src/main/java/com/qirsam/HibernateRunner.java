package com.qirsam;

import com.qirsam.converter.BirthdayConverter;
import com.qirsam.entity.Birthday;
import com.qirsam.entity.Role;
import com.qirsam.entity.User;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.SneakyThrows;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.cfg.Configuration;

import java.time.LocalDate;
import java.util.Map;

public class HibernateRunner {

    /**
     * @param args
     */
    @SneakyThrows
    public static void main(String[] args) {
        var configuration = new Configuration();
//        configuration.setPhysicalNamingStrategy(new CamelCaseToUnderscoresNamingStrategy());
        configuration.addAnnotatedClass(User.class);
        configuration.addAttributeConverter(new BirthdayConverter());
        configuration.registerTypeOverride(new JsonBinaryType());

//        configuration.registerTypeOverride(new JsonBinaryType());
        configuration.configure();

        try (var sessionFactory = configuration.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();

            var user = User.builder()
                    .username("1qirsam@gmail.com")
                    .firstname("Sergey")
                    .lastname("Lazchenko")
                    .info("""
                            {
                                "name": "Sergey",
                                "id": 25
                            }
                            """)
                    .birthDate(new Birthday(LocalDate.of(1993, 9, 21)))
                    .role(Role.ADMIN)
//                    .age(28)
                    .build();
            session.persist(user);

            session.getTransaction().commit();
        }
    }
}
