package com.qirsam.dao;

import com.qirsam.entity.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.Session;

import javax.persistence.CollectionTable;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDao {

    private static final UserDao INSTANCE = new UserDao();

    public List<User> findAll(Session session) {
//        return session.createQuery("select u from User u", User.class)
//                .list();
        var cb = session.getCriteriaBuilder();

        var criteria = cb.createQuery(User.class);
        var user = criteria.from(User.class);

        criteria.select(user);

        return session.createQuery(criteria)
                .list();
    }

    public List<User> findAllByFirstName(Session session, String firstname) {
//        return session.createQuery("select u from User u " +
//                                   "where u.personalInfo.firstname = :firstname", User.class)
//                .setParameter("firstname", firstname)
//                .list();
        var cb = session.getCriteriaBuilder();

        var criteria = cb.createQuery(User.class);
        var user = criteria.from(User.class);

        criteria.select(user).where(
                cb.equal(user.get(User_.personalInfo).get(PersonalInfo_.firstname), firstname));

        return session.createQuery(criteria)
                .list();
    }

    public List<User> findLimitedUsersOrderedByBirthday(Session session, int limit) {
//        return session.createQuery("select u from User u order by u.personalInfo.birthDate", User.class)
//                .setMaxResults(limit)
//                .list();
        var cb = session.getCriteriaBuilder();

        var criteria = cb.createQuery(User.class);
        var user = criteria.from(User.class);

        criteria.select(user).orderBy(cb.asc(user.get(User_.personalInfo).get(PersonalInfo_.birthDate)));

        return session.createQuery(criteria)
                .setMaxResults(limit)
                .list();

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

        var cb = session.getCriteriaBuilder();

        var criteria = cb.createQuery(User.class);
        var company = criteria.from(Company.class);
        var users = company.join(Company_.users);

        criteria.select(users).where(
                cb.equal(company.get(Company_.name), companyName)
        );

        return session.createQuery(criteria)
                .list();

    }

    public List<Payment> findAllPaymentsByCompanyName(Session session, String companyName) {
        return session.createQuery("select p from Payment p " +
                                   "join p.receiver u " +
                                   "join u.company c " +
                                   "where c.name = :companyName " +
                                   "order by u.personalInfo.firstname, p.amount", Payment.class)
                .setParameter("companyName", companyName)
                .list();
    }

    public Double findAveragePaymentAmountByFirstAndLastNames(Session session, String firstname, String lastname) {
        return session.createQuery("select avg(p.amount) from Payment p " +
                                   "join p.receiver u " +
                                   "where u.personalInfo.firstname= :firstname " +
                                   "and u.personalInfo.lastname = :lastname", Double.class)
                .setParameter("firstname", firstname)
                .setParameter("lastname", lastname)
                .uniqueResult();
    }


    public List<Object[]> findCompanyNamesWithAvgUserPaymentsOrderedByCompanyName(Session session) {
        return session.createQuery("select c.name, avg(p.amount) from Company c " +
                                   "join c.users u " +
                                   "join u.payments p " +
                                   "group by c.name " +
                                   "order by c.name", Object[].class)
                .list();
    }

    public List<Object[]> isItPossible(Session session) {
        return session.createQuery("select u, avg(p.amount) from User u " +
                                   "join u.payments p " +
                                   "group by u " +
                                   "having avg (p.amount) > (select avg(p.amount) from Payment p) " +
                                   "order by u.personalInfo.firstname", Object[].class)
                .list();
    }


    public static UserDao getInstance() {
        return INSTANCE;
    }
}
