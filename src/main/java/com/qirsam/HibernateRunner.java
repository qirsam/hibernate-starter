package com.qirsam;

import com.qirsam.entity.Payment;
import com.qirsam.util.HibernateUtil;
import com.qirsam.util.TestDataImporter;
import lombok.SneakyThrows;

import javax.persistence.LockModeType;
import javax.transaction.Transactional;

public class HibernateRunner {

    @Transactional
    @SneakyThrows
    public static void main(String[] args) {
        try (var sessionFactory = HibernateUtil.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            TestDataImporter.importData(sessionFactory);
            session.beginTransaction();

            var payment = session.find(Payment.class, 1L, LockModeType.OPTIMISTIC);
            payment.setAmount(payment.getAmount() + 10);

            session.getTransaction().commit();
        }
    }
}
