package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members")
    public Page<Member> pageList(Pageable pageable){
        return memberRepository.findAll(pageable);
    }

    @GetMapping("/members2")
    public Page<Member> pageList2(@PageableDefault(size = 5) Pageable pageable){
        return memberRepository.findAll(pageable);
    }

    @GetMapping("/membersDto")
    public Page<MemberDto>  pageListDto(Pageable pageable){
//        return memberRepository.findAll(pageable)
//                 .map(m -> new MemberDto(m.getId(), m.getUserName(), "No team"));
        return memberRepository.findAll(pageable)
                .map(MemberDto::new);
    }

//    @PostConstruct
    public void init(){
        List<Member> memberList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            memberList.add(new Member("user"+i, i+10));
        }
        memberRepository.saveAll(memberList);
    }
}
