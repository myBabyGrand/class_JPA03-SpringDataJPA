package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;

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

    private List<Member> MakeTestMembersWithTeam() {
        Team team1 = new Team("A team");
        Team team2 = new Team("2 team");

        teamRepository.save(team1);
        teamRepository.save(team2);

        Member member1 = new Member("TestMember1", 10, team1);
        Member member2 = new Member("TestMember2", 20, team1);
        Member member3 = new Member("TestMember3", 30, team1);
        Member member4 = new Member("TestMember4", 40, team2);

        List<Member> members = new ArrayList<>();
        members.add(member1);
        members.add(member2);
        members.add(member3);
        members.add(member4);
        Member savedMember1 = memberRepository.save(member1);
        Member savedMember2 = memberRepository.save(member2);
        Member savedMember3 = memberRepository.save(member3);
        Member savedMember4 = memberRepository.save(member4);

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
    void findUserTest(){
        List<Member> members= MakeTestMembers();
        assertThat(memberRepository.findUser("TestMember1", 10).get(0)).isEqualTo(members.get(0));
    }

    @Test
    @DisplayName("Spring Data JPA로 Repository에 정의된 Query 테스트-값타입")
    void findUserNameListTest(){
        List<Member> members= MakeTestMembers();
        List<String> userNames = memberRepository.findUserNameList();
        assertThat(userNames.size()).isEqualTo(2);
        for (String userName : userNames) {
            System.out.println(userName);
        }
    }

    @Test
    @DisplayName("Spring Data JPA로 Dto를 이용한 Query 테스트")
    void findMemberDtoTest(){
        MakeTestMembersWithTeam();
        List<MemberDto> memberDto = memberRepository.findMemberDto();
        assertThat(memberDto.size()).isEqualTo(4);
        for (MemberDto dto : memberDto) {
            System.out.println(dto.toString());
        }
    }

    @Test
    @DisplayName("Spring Data JPA로 Binding Param을 이용한 Query 테스트")
    void findByNamesTest(){
        MakeTestMembers();
        List<Member> findMembers = memberRepository.findByNames(Arrays.asList("TestMember1", "TestMember2"));
        assertThat(findMembers.size()).isEqualTo(2);
        for (Member findMember : findMembers) {
            System.out.println(findMember);
        }
    }

    @Test
    @DisplayName("Spring Data JPA로 다양한 반환타입을 이용한 Query 테스트")
    void findVariousReturnTypeOfMemberByUserNameTest(){
        List<Member> members = MakeTestMembers();


        //주의 : 조회결과 없으면 null이 아니라 empty collection을 반환한다
        List<Member> memberListByUserNameEmpty = memberRepository.findMemberListByUserName("XXXX");
        assertThat(memberListByUserNameEmpty.size()).isEqualTo(0);

        //Entity는 결과가 없다면 null
        Member memberByUserNameEmpty = memberRepository.findMemberByUserName("XXXX");
        assertThat(memberByUserNameEmpty).isNull();

        String userName = members.get(0).getUserName() ;

        List<Member> memberListByUserName = memberRepository.findMemberListByUserName(userName);
        assertThat(memberListByUserName.get(0).getUserName()).isEqualTo(userName);

        Member memberByUserName = memberRepository.findMemberByUserName(userName);
        assertThat(memberByUserName.getUserName()).isEqualTo(userName);

        Optional<Member> optionalMemberByUserName = memberRepository.findOptionalMemberByUserName(userName);
        assertThat(optionalMemberByUserName.get().getUserName()).isEqualTo(userName);

        //반환이 List로 될 경우 IncorrectResultSizeDataAccessException 발생
        Member member1 = new Member("TestMember1", 10);
        Member savedMember1 = memberRepository.save(member1);
        try{
            Optional<Member> optionalMemberByUserNameDup = memberRepository.findOptionalMemberByUserName(userName);
        }catch (Exception e){
            System.out.println(e.getClass());
            assertThat(e).isInstanceOf(org.springframework.dao.IncorrectResultSizeDataAccessException.class);
        }


    }
}
