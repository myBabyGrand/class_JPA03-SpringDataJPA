package study.datajpa.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.repository.MemberJpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    public void TestEntity(){
        makeTestTeamAndMember();

        //초기화
        em.flush();
        em.clear();

        List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();
        for (Member member : members) {
            System.out.println("member : "+member.toString());
            System.out.println("member's TeamName :" +member.getTeam().getTeamName());
        }

    }

    private void makeTestTeamAndMember() {
        Team teamA = new Team("TEAM A");
        Team teamB = new Team("TEAM B");

        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("Member1", 10, teamA);
        Member member2 = new Member("Member2", 20, teamA);
        Member member3 = new Member("Member3", 30, teamB);
        Member member4 = new Member("Member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @Test
    @DisplayName("JPA Auditing : Base Entity 동작확인")
    public void JpaEvenBaseEntity(){
        //given
        Member member = new Member("Member1");
        memberJpaRepository.save(member);//@prePersist

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        member.setUserName("Member2");

        em.flush();//@preUpdate
        em.clear();

        //when
        Member findMember = memberJpaRepository.findById(member.getId()).orElseThrow(IndexOutOfBoundsException::new);

        //then
        System.out.println("findMember is CreatedAt : "+findMember.getCreatedDate()+" and updatedAt : "+findMember.getLastModifiedDate());
        assertThat(findMember.getLastModifiedDate()).isAfter(findMember.getCreatedDate());

        System.out.println("findMember is CreatedBy : "+findMember.getCreatedBy()+" and lastModifiedBy : "+findMember.getLastModifiedBy());
    }
}