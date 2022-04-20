package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;


@RunWith(SpringRunner.class) //junit을 실행할 때 스프링과 같이 엮어서 실행한다는 annotation
@SpringBootTest // 스프링 부트를 띄운상태에서 테스트를 실행하게 해준다. 없으면 Autowired 실패한다.
@Transactional // 테스트가 끝나면 롤백을 시켜준다
public class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Test
//    @Rollback(value = false)
    public void 회원가입() throws Exception {
        //given
        Member member = new Member();
        member.setName("jinhan");

        //when
        Long saveId = memberService.join(member);

        //then
//        em.flush()를 하면 디비에 쿼리를 날린다. 그래서 조심히 써야 한다.
//        지금은 class level에서 Transactional annotation이 롤백을 해준다.
        em.flush();
        Assert.assertEquals(member, memberRepository.findOne(saveId));

    }


    // IllegalStateException 에러를 예상한다는 것이다.
    // IllegalStateException 에러가 뜨면 성공한다.
    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예외() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("kim");
        Member member2 = new Member();
        member2.setName("kim");

        //when
        memberService.join(member1);
        memberService.join(member2);
//        try {
//            memberService.join(member2);
//        } catch (IllegalStateException e) {
//            return;
//        }


        //then
//        fail("예외가 발생한다.");

    }


}