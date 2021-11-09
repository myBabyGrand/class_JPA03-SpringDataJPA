package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom, JpaSpecificationExecutor<Member>{

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

    @Query("select m from Member m")
    List<Member> findAll2();

    @Query("select m from Member m join fetch m.team")
    List<Member> findAllMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findAllMemberEntityGraph();

//    @EntityGraph(attributePaths = {"team"})
    @EntityGraph("Member.all") //namedEntityGraph
    List<Member> findEntityGraphByUserName(@Param("userName") String userName);

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUserName(String userName);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Member findLockByUserName(String userName);

    List<UserNameOnly>findProjectionsByUserName(@Param("userName") String userName);

    List<UserNameOnlyDto>findProjectDtoByUserName(@Param("userName") String userName);

    //동적 프로젝션
    <T> List<T> findDynamicProjectionByUserName(@Param("userName") String userName, Class<T> type);

    @Query(value = "select * from member where user_name = ?", nativeQuery = true)
    Member findByNativeQuery(String userName);

    @Query(value = "select m.member_id as id, m.user_name as userName, t.team_name as teamName from member m ,team t where m.team_id = t.team_id"
             , countQuery = "select count(*) from Member ",nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable);
}
