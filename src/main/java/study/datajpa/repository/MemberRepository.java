package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByUserNameAndAgeGreaterThan(String userName, int  age);

//    @Query(name = "Member.findByUserName2")
    List<Member> findByUserName2(@Param("userName") String userName);

    @Query("select m from Member m where m.userName = :userName and m.age = :age")
    List<Member> findUser(@Param("userName") String userName, @Param("age") int age);

    @Query("select m.userName from Member m")
    List<String> findUserNameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.userName, t.teamName) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.userName in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    List<Member> findMemberListByUserName(String userName);
    Member findMemberByUserName(String userName);
    Optional<Member> findOptionalMemberByUserName(String userName);

    Page<Member> findByAge(int age, Pageable pageable);

    Slice<Member> findByUserName(String userName, Pageable pageable);

    @Query(value = "select m from Member m left join m.team t where m.age = :age"
    , countQuery = "select count(m) from Member m where m.age = :age")
    Page<Member> findByAge2(@Param("age")int age, Pageable pageable);

//    @Modifying(clearAutomatically = true) //영속성 컨텍스트를 자동으로 clear를 한다
    @Modifying
    @Query("update Member m set m.age = m.age+1 where m.age >= :age")
    int bulkAgePlus(@Param("age")int age);

}
