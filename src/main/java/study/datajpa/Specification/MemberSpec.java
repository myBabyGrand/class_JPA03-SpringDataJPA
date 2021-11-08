package study.datajpa.Specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.criteria.*;

public class MemberSpec {

    public static Specification<Member> teamName(final String teamName){
        return (Specification<Member>)(root, query, criteriaBuilder) -> {
            if(StringUtils.isEmpty(teamName)){
                return null;
            }
            Join<Member, Team> t = root.join("team", JoinType.INNER);
            return criteriaBuilder.equal(t.get("teamName"), teamName);
        };
    }

    public static Specification<Member> userName(final String userName){
        return (Specification<Member>)(root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("userName"), userName);
    }
}
