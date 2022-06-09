package com.qirsam.dao;

import com.qirsam.dto.CompanyDto;
import com.qirsam.entity.*;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.Session;
import com.querydsl.core.*;

import javax.persistence.CollectionTable;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.qirsam.entity.QCompany.company;
import static com.qirsam.entity.QPayment.payment;
import static com.qirsam.entity.QUser.user;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDao {

    private static final UserDao INSTANCE = new UserDao();

    public List<User> findAll(Session session) {
//        return session.createQuery("select u from User u", User.class)
//                .list();
        return new JPAQuery<User>(session)
                .select(user)
                .from(user)
                .fetch();
    }

    public List<User> findAllByFirstName(Session session, String firstname) {
//        return session.createQuery("select u from User u " +
//                                   "where u.personalInfo.firstname = :firstname", User.class)
//                .setParameter("firstname", firstname)
//                .list();
        return new JPAQuery<User>(session)
                .select(user)
                .from(user)
                .where(user.personalInfo.firstname.eq(firstname))
                .fetch();
    }

    public List<User> findLimitedUsersOrderedByBirthday(Session session, int limit) {
//        return session.createQuery("select u from User u order by u.personalInfo.birthDate", User.class)
//                .setMaxResults(limit)
//                .list();
        return new JPAQuery<User>(session)
                .select(user)
                .from(user)
                .orderBy(user.personalInfo.birthDate.asc())
                .limit(limit)
                .fetch();
    }

    public List<User> findAllByCompanyName(Session session, String companyName) {
//        return session.createQuery("select u from User u " +
//                                   "where u.company.name  = :companyName", User.class)
//                .setParameter("companyName", companyName)
//                .list();

//        return session.createQuery("select u from Company c " +
//                                     "join c.users u " +
//                                   "where c.name  = :companyName", User.class)
//                .setParameter("companyName", companyName)
//                .list();
        return new JPAQuery<User>(session)
                .select(user)
                .from(company)
                .join(company.users, user)
                .where(company.name.eq(companyName))
                .fetch();
    }

    public List<Payment> findAllPaymentsByCompanyName(Session session, String companyName) {
//        return session.createQuery("select p from Payment p " +
//                                   "join p.receiver u " +
//                                   "join u.company c " +
//                                   "where c.name = :companyName " +
//                                   "order by u.personalInfo.firstname, p.amount", Payment.class)
//                .setParameter("companyName", companyName)
//                .list();
        return new JPAQuery<Payment>(session)
                .select(payment)
                .from(payment)
                .join(payment.receiver, user)
                .join(user.company, company)
                .where(company.name.eq(companyName))
                .orderBy(user.personalInfo.firstname.asc(), payment.amount.asc())
                .fetch();
    }

    public Double findAveragePaymentAmountByFirstAndLastNames(Session session, String firstname, String lastname) {
//        return session.createQuery("select avg(p.amount) from Payment p " +
//                                   "join p.receiver u " +
//                                   "where u.personalInfo.firstname= :firstname " +
//                                   "and u.personalInfo.lastname = :lastname", Double.class)
//                .setParameter("firstname", firstname)
//                .setParameter("lastname", lastname)
//                .uniqueResult();
        return new JPAQuery<Double>(session)
                .select(payment.amount.avg())
                .from(payment)
                .join(payment.receiver, user)
                .where(user.personalInfo.firstname.eq(firstname)
                        .and(user.personalInfo.lastname.eq(lastname)))
                .fetchOne();
    }


    public List<Tuple> findCompanyNamesWithAvgUserPaymentsOrderedByCompanyName(Session session) {
//        return session.createQuery("select c.name, avg(p.amount) from Company c " +
//                                   "join c.users u " +
//                                   "join u.payments p " +
//                                   "group by c.name " +
//                                   "order by c.name", Object[].class)
//                .list();
        return new JPAQuery<Tuple>(session)
                .select(company.name, payment.amount.avg())
                .from(company)
                .join(company.users, user)
                .join(user.payments, payment)
                .groupBy(company.name)
                .orderBy(company.name.asc())
                .fetch();
    }

    public List<Tuple> isItPossible(Session session) {
//        return session.createQuery("select u, avg(p.amount) from User u " +
//                                   "join u.payments p " +
//                                   "group by u " +
//                                   "having avg (p.amount) > (select avg(p.amount) from Payment p) " +
//                                   "order by u.personalInfo.firstname", Object[].class)
//                .list();
        return new JPAQuery<Tuple>(session)
                .select(user, payment.amount.avg())
                .from(user)
                .join(user.payments, payment)
                .groupBy(user.id)
                .having(payment.amount.avg().gt(new JPAQuery<Double>(session)
                        .select(payment.amount.avg())
                        .from(payment)
                ))
                .orderBy(user.personalInfo.firstname.asc())
                .fetch();
    }


    public static UserDao getInstance() {
        return INSTANCE;
    }
}
