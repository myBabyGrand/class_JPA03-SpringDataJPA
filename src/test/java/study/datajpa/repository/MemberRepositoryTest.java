package study.datajpa.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;

    @Test
    @Rollback(value = false)
    @DisplayName("Member를 통해서 SpringData JPA가 잘 동작하는지 확인해보자")
    public void testMember(){
        Member member = new Member("TestMember2");
        Member savedMember = memberRepository.save(member);
        Member findMember = memberRepository.findById(member.getId()).get();

        assertThat(findMember.getId()).isEqualTo(savedMember.getId());
        assertThat(findMember.getUserName()).isEqualTo(savedMember.getUserName());
        assertThat(findMember).isEqualTo(savedMember);

    }

    private List<Member> MakeTestMembers() {
        Member member1 = new Member("TestMember1", 10);
        Member member2 = new Member("TestMember2", 20);

        List<Member> members = new ArrayList<>();
        members.add(member1);
        members.add(member2);
        Member savedMember1 = memberRepository.save(member1);
        Member savedMember2 = memberRepository.save(member2);

        return members;
    }

    @Test
    public void basicCRUD() {

        List<Member> members = MakeTestMembers();

        Member findMember1 = memberRepository.findById(members.get(0).getId()).get();
        Member findMember2 = memberRepository.findById(members.get(1).getId()).get();

        //단건
        assertThat(findMember1).isEqualTo(members.get(0));
        assertThat(findMember2).isEqualTo(members.get(1));

        //List
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //count
        assertThat(memberRepository.count()).isEqualTo(2);

        //delete
        memberRepository.delete(members.get(0));
        memberRepository.delete(members.get(1));

        assertThat(memberRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("Spring Data JPA로 메소드 이름으로 쿼리생성 테스트")
    void MethodNameQueryTest(){
        List<Member> members = MakeTestMembers();

        List<Member> findMembers = memberRepository.findByUserNameAndAgeGreaterThan("TestMember2", 15);
        assertThat(findMembers.get(0)).isEqualTo(members.get(1));
    }

    @Test
    @DisplayName("Spring Data JPA로 NamedQuery 테스트")
    void NamedQueryTest(){
        List<Member> members= MakeTestMembers();
        assertThat(memberRepository.findByUserName2("TestMember1").get(0)).isEqualTo(members.get(0));
    }

    @Test
    @DisplayName("Spring Data JPA로 Repository에 정의된 Query 테스트")
    void RepositoryDefinedQueryTest(){
        List<Member> members= MakeTestMembers();
        assertThat(memberRepository.findUser("TestMember1", 10).get(0)).isEqualTo(members.get(0));
    }
}
