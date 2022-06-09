package com.qirsam;

import com.qirsam.entity.Company;
import com.qirsam.entity.PersonalInfo;
import com.qirsam.entity.User;
import com.qirsam.util.HibernateUtil;
import lombok.SneakyThrows;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateRunner {

    @SneakyThrows
    public static void main(String[] args) {
        var company = Company.builder()
                .name("Yandex")
                .build();
//        var user = User.builder()
//                .username("qirsam@gmail.com")
//                .personalInfo(PersonalInfo.builder()
//                        .lastname("Lazchenko")
//                        .firstname("Sergey")
//                        .build())
//                .company(company)
//                .build();
                User user = null;

        try (var sessionFactory = HibernateUtil.buildSessionFactory()) {
            var sessionOne = sessionFactory.openSession();
            try (sessionOne) {
                var transaction = sessionOne.beginTransaction();

//                var user1 = sessionOne.get(User.class, 1L);
//                var company1 = user1.getCompany();
//                company1.getId();
                sessionOne.save(company);
                sessionOne.save(user);

                sessionOne.getTransaction().commit();
            }
        }
    }
}
