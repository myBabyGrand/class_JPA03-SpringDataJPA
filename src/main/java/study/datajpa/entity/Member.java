package study.datajpa.entity;


import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of= {"id", "userName", "age"})
@Getter
@Setter
@NamedQuery(
        name = "Member.findByUserName2",
        query = "select m from Member m where m.userName = :userName"
)
@NamedEntityGraph(name = "Member.all", attributeNodes = @NamedAttributeNode("team"))
public class Member extends BaseEntity{
//        extends JpaBaseEntity{

    @Id
    @GeneratedValue
    @Column(name= "MEMBER_ID")
    private Long id;

    private String userName;

    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID")
    private Team team;

    public Member(String userName) {
        this.userName = userName;
    }

    public Member(String userName, int age) {
        this.userName = userName;
        this.age = age;
    }

    public Member(String userName, int age, Team team) {
        this.userName = userName;
        this.age = age;
        if(team != null){
            changeTeam(team);
        }
    }

    public void changeTeam(Team team){
        this.team  = team;
        team.getMembers().add(this);
    }
}
