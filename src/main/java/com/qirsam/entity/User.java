package com.qirsam.entity;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.qirsam.util.StringUtils.SPACE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users", schema = "public")
@EqualsAndHashCode(of = "username")
@ToString(exclude = {"company", "profile", "usersChats"})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
public class User implements Comparable<User>, BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @SequenceGenerator(name = "user_gen", sequenceName = "users_id_seq", allocationSize = 1)
    private Long id;

    @Column(unique = true)
    private String username;

    @Embedded
    @AttributeOverride(name = "birthDate", column = @Column(name = "birth_date"))
    private PersonalInfo personalInfo;

    @Type(type = "jsonb")
    private String info;

    @Enumerated(EnumType.STRING)
    private Role role;
//    private Integer age;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Profile profile;

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<UsersChat> usersChats = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "receiver")
    private List<Payment> payments = new ArrayList<>();

    @Override
    public int compareTo(User o) {
        return username.compareTo(o.username);
    }

    public String fullName() {
        return getPersonalInfo().getFirstname() + SPACE + getPersonalInfo().getLastname();
    }
}
