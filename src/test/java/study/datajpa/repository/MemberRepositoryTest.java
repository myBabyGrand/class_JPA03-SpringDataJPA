package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

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
        Member member2 = new Member("TestMember2", 10, team1);
        Member member3 = new Member("TestMember3", 10, team1);
        Member member4 = new Member("TestMember4", 10, team2);
        Member member5 = new Member("TestMember5", 10, team2);
        Member member6 = new Member("TestMember1", 30, team2);
        Member member7 = new Member("TestMember1", 40, team2);


        List<Member> members = new ArrayList<>();
        members.add(member1);
        members.add(member2);
        members.add(member3);
        members.add(member4);
        members.add(member5);
        members.add(member6);
        members.add(member7);

        Member savedMember1 = memberRepository.save(member1);
        Member savedMember2 = memberRepository.save(member2);
        Member savedMember3 = memberRepository.save(member3);
        Member savedMember4 = memberRepository.save(member4);
        Member savedMember5 = memberRepository.save(member5);
        Member savedMember6 = memberRepository.save(member6);
        Member savedMember7 = memberRepository.save(member7);

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
        assertThat(memberDto.size()).isEqualTo(7);
        for (MemberDto dto : memberDto) {
            assertThat(dto).hasFieldOrProperty("id");
            assertThat(dto).hasFieldOrProperty("userName");
            assertThat(dto).hasFieldOrProperty("teamName");
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

    @Test
    @DisplayName("Spring Data JPA로 Paging 테스트")
    public void findByAgeTest(){
        //given
        MakeTestMembersWithTeam();
        int age = 10;
        int offset = 0;
        int limit = 3;

        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "userName"));

        //when
        Page<Member> memberPage = memberRepository.findByAge(age, pageRequest);

        //Dto로 변환하기
        Page<MemberDto> memberDtos = memberPage.map(m -> new MemberDto(m.getId(), m.getUserName(), null));//조회결과 team은 없으니깐 null로 넣음

        //then
        assertThat(memberPage.getTotalElements()).isEqualTo(5);
        assertThat(memberPage.getNumber()).isEqualTo(0);
        assertThat(memberPage.getTotalPages()).isEqualTo(2);
        assertThat(memberPage.isFirst()).isTrue();
        assertThat(memberPage.hasNext()).isTrue();

        int i=5;
        for (Member member : memberPage.getContent()) {
            String sol = "TestMember"+i;
            System.out.println(member.getUserName() +" : "+ sol);
            assertThat(member.getUserName()).isEqualTo(sol);
            i--;
        }
    }

    @Test
    @DisplayName("Spring Data JPA로 Paging 테스트-Slice")
    public void findByAgeSliceTest(){
        //given
        MakeTestMembersWithTeam();
        String userName = "TestMember1";
        int offset = 0;
        int limit = 3;
        Member member8 = new Member(userName, 50);
        Member member9 = new Member(userName, 60);
        memberRepository.save(member8);
        memberRepository.save(member9);

        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "age"));

        //when
        Slice<Member> memberPage = memberRepository.findByUserName(userName, pageRequest);

        //then
//        assertThat(memberPage.getTotalElements()).isEqualTo(5);
        assertThat(memberPage.getNumber()).isEqualTo(0);
//        assertThat(memberPage.getTotalPages()).isEqualTo(2);
        assertThat(memberPage.isFirst()).isTrue();
        assertThat(memberPage.hasNext()).isTrue();
        assertThat(memberPage.getContent().size()).isEqualTo(3);

        int sol = 60;
        for (Member member : memberPage.getContent()) {
            System.out.println(member.getAge() +" : "+ sol);
            assertThat(member.getAge()).isEqualTo(sol);
            sol -= 10;
        }
    }

    @Test
    @DisplayName("Spring Data JPA로 Paging 테스트 - count 쿼리 별도로 뺴기")
    public void findByAgeTest2(){
        //given
        MakeTestMembersWithTeam();
        int age = 10;
        int offset = 0;
        int limit = 3;

        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "userName"));

        //when
        Page<Member> memberPage = memberRepository.findByAge2(age, pageRequest);

        //then
        assertThat(memberPage.getTotalElements()).isEqualTo(5);
        assertThat(memberPage.getNumber()).isEqualTo(0);
        assertThat(memberPage.getTotalPages()).isEqualTo(2);
        assertThat(memberPage.isFirst()).isTrue();
        assertThat(memberPage.hasNext()).isTrue();

        int i=5;
        for (Member member : memberPage.getContent()) {
            String sol = "TestMember"+i;
            System.out.println(member.getUserName() +" : "+ sol);
            assertThat(member.getUserName()).isEqualTo(sol);
            i--;
        }
    }

    @Test
    @DisplayName("Spring Data JPA로 bulk 업데이트 테스트")
    public void bulkAgePlusTest() {
        //given
        MakeTestMembersWithTeam();

        //when
        int i = memberRepository.bulkAgePlus(30);

        //then
        assertThat(i).isEqualTo(2);

        //when
        List<Member> byUserNameAndAgeGreaterThan = memberRepository.findByUserNameAndAgeGreaterThan("TestMember1", 30);
        byUserNameAndAgeGreaterThan.sort(new Comparator<Member>() {
            @Override
            public int compare(Member o1, Member o2) {
                return o1.getAge()- o2.getAge();//asc
            }
        });

        //then
        //영속성 컨텍스트에 반영이 안된다.
        int age = 30;
        for (Member member : byUserNameAndAgeGreaterThan) {
            assertThat(member.getAge()).isEqualTo(age);//
            age +=10;
        }

        //when
        em.flush();
        em.clear();
        List<Member> byUserNameAndAgeGreaterThan2 = memberRepository.findByUserNameAndAgeGreaterThan("TestMember1", 30);
        byUserNameAndAgeGreaterThan2.sort(new Comparator<Member>() {
            @Override
            public int compare(Member o1, Member o2) {
                return o1.getAge()- o2.getAge();//asc
            }
        });

        //then
        //영속성 컨텍스트를 갱신했다
        age = 31;
        for (Member member : byUserNameAndAgeGreaterThan2) {
            assertThat(member.getAge()).isEqualTo(age);
            age +=10;
        }
    }

    @Test
    @DisplayName("Spring Data fetch join 테스트")
    public void findMemberLazy() {
        //given
        MakeTestMembersWithTeam();
        em.flush();
        em.clear();

        //when
        System.out.println("memberRepository.findAll2");
        List<Member> members = memberRepository.findAll2();

        //then
        for (Member member : members) {
            System.out.println(member);
            System.out.println(member.getTeam().getClass());//proxy
            System.out.println(member.getTeam().getTeamName());
        }

        em.flush();
        em.clear();

        //when
        System.out.println("memberRepository.findAllMemberFetchJoin");
        List<Member> allMemberFetchJoin = memberRepository.findAllMemberFetchJoin();

        //then
        for (Member member : allMemberFetchJoin) {
            System.out.println(member);
            System.out.println(member.getTeam().getClass());//team
            System.out.println(member.getTeam().getTeamName());
        }

        em.flush();
        em.clear();

        System.out.println("memberRepository.findAllMemberEntityGraph");
        List<Member> allMemberEntityGraph = memberRepository.findAllMemberEntityGraph();
        for (Member member : allMemberEntityGraph) {
            System.out.println(member);
            System.out.println(member.getTeam().getClass());//team
            System.out.println(member.getTeam().getTeamName());
        }

        em.flush();
        em.clear();

        System.out.println("memberRepository.findEntityGraphByUserName");
        List<Member> testMember1 = memberRepository.findEntityGraphByUserName("TestMember1");
        for (Member member : testMember1) {
            System.out.println(member);
            System.out.println(member.getTeam().getClass());//team
            System.out.println(member.getTeam().getTeamName());
        }

    }

    @Test
    @DisplayName("Spring Data hint 테스트")
    public void findReadOnlyByUserNameTest(){
        Member member1 = new Member("Member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        Member readOnlyByUserName = memberRepository.findReadOnlyByUserName(member1.getUserName());
        readOnlyByUserName.setUserName("Member2");//read only : dirty-check 하지 않음. 스냅샷없음
//        memberRepository.save(readOnlyByUserName);//update 하지 않음

        em.flush();//update 하지 않음
        em.clear();

        Optional<Member> byId = memberRepository.findById(member1.getId());
        System.out.println(byId.orElseThrow(NullPointerException::new).getUserName());
        assertThat(byId.orElseThrow(NullPointerException::new).getUserName()).isNotEqualTo("Member2");
    }

    @Test
    @DisplayName("Spring Data Lock 테스트")
    public void findLockByUserNameTest(){
        Member member1 = new Member("Member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        Member lockByUserName = memberRepository.findLockByUserName(member1.getUserName());//select for  update

    }

}
