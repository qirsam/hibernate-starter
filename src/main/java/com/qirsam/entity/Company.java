package com.qirsam.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "name")
@ToString(exclude = "users")
@Builder
public class Company {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

//    @Builder.Default
//    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
////    @OrderBy("username DESC, personalInfo.lastname ASC ")
////    @org.hibernate.annotations.OrderBy(clause = "username DESC, lastname ASC")
////    @ToString.Exclude
////    @EqualsAndHashCode.Exclude
////    @OrderColumn(name = "id")
////    @SortNatural
//    private Set<User> users = new TreeSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private Map<String, User> users = new HashMap<>();

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "company_locale")
    private List<LocaleInfo> locales = new ArrayList<>();

    public void addUser(User user) {
//        users.add(user);
        users.put(user.getUsername(), user);
        user.setCompany(this);
    }
}
