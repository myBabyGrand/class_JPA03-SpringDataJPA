package study.datajpa.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired MemberJpaRepository memberJpaRepository;
    @Autowired TeamJpaRepository teamJpaRepository;

    @Test
    @Rollback(value = false)
    void save() {
        Member member = new Member("TestMember");
        Member savedMember = memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.find(savedMember.getId());

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUserName()).isEqualTo(member.getUserName());
        assertThat(findMember).isEqualTo(member);

    }

    @Test
    public void basicCRUD() {

        List<Member> members= MakeTestMembers();

        Member findMember1 = memberJpaRepository.findById(members.get(0).getId()).get();
        Member findMember2 = memberJpaRepository.findById(members.get(1).getId()).get();

        //단건
        assertThat(findMember1).isEqualTo(members.get(0));
        assertThat(findMember2).isEqualTo(members.get(1));

        //List
        List<Member> all = memberJpaRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //count
        assertThat(memberJpaRepository.count()).isEqualTo(2);

        //delete
        memberJpaRepository.delete(members.get(0));
        memberJpaRepository.delete(members.get(1));

        assertThat(memberJpaRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("JPA로 메소드 이름으로 쿼리생성 테스트")
    void MethodNameQueryTest(){
        List<Member> members= MakeTestMembers();

        List<Member> findMember = memberJpaRepository.findByUserNameAndAgeLargerThan("TestMember2", 15);
        assertThat(findMember.get(0)).isEqualTo(members.get(1));

    }

    @Test
    @DisplayName("JPA로 NamedQuery 테스트")
    void NamedQueryTest(){
        List<Member> members= MakeTestMembers();

        assertThat(memberJpaRepository.findByUserName2("TestMember1").get(0)).isEqualTo(members.get(0));
    }

    private List<Member> MakeTestMembers() {
        Member member1 = new Member("TestMember1", 10);
        Member member2 = new Member("TestMember2", 20);

        List<Member> members = new ArrayList<>();
        members.add(member1);
        members.add(member2);
        Member savedMember1 = memberJpaRepository.save(member1);
        Member savedMember2 = memberJpaRepository.save(member2);

        return members;
    }

    private List<Member> MakeTestMembersWithTeam() {
        Team team1 = new Team("A team");
        Team team2 = new Team("2 team");

        teamJpaRepository.save(team1);
        teamJpaRepository.save(team2);

        Member member1 = new Member("TestMember1", 10, team1);
        Member member2 = new Member("TestMember2", 10, team1);
        Member member3 = new Member("TestMember3", 10, team1);
        Member member4 = new Member("TestMember4", 10, team2);
        Member member5 = new Member("TestMember5", 10, team2);
        Member member6 = new Member("TestMember6", 40, team2);
        Member member7 = new Member("TestMember7", 40, team2);


        List<Member> members = new ArrayList<>();
        members.add(member1);
        members.add(member2);
        members.add(member3);
        members.add(member4);
        members.add(member5);
        members.add(member6);
        members.add(member7);

        Member savedMember1 = memberJpaRepository.save(member1);
        Member savedMember2 = memberJpaRepository.save(member2);
        Member savedMember3 = memberJpaRepository.save(member3);
        Member savedMember4 = memberJpaRepository.save(member4);
        Member savedMember5 = memberJpaRepository.save(member5);
        Member savedMember6 = memberJpaRepository.save(member6);
        Member savedMember7 = memberJpaRepository.save(member7);


        return members;
    }

    @Test
    @DisplayName("JPA로 페이징 테스트")
    public void findByPageTest(){
        //given
        MakeTestMembersWithTeam();
        int age = 10;
        int offset = 0;
        int limit = 3;

        //when
        List<Member> members = memberJpaRepository.findByPage(age, offset, limit);
        long totalCount = memberJpaRepository.totalCount(age);

        //then
        assertThat(totalCount).isEqualTo(5);

        int i=5;
        for (Member member : members) {
            String sol = "TestMember"+i;
            System.out.println(member.getUserName() +" : "+ sol);
            assertThat(member.getUserName()).isEqualTo(sol);
            i--;
        }
    }

    @Test
    @DisplayName("JPA로 bulk 업데이트")
    public void bulkAgePlusTest() {
        //given
        MakeTestMembersWithTeam();

        //when
        int i = memberJpaRepository.bulkAgePlus(40);

        //then
        assertThat(i).isEqualTo(2);

    }
}

