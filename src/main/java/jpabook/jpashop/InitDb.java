package jpabook.jpashop;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;


@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;


    // 서버 빌드 시점에 호출하기 위함
    // spring bean이 다 올라가고 나면 spring bean이 호출해준다
    @PostConstruct
    public void init(){
        initService.dbInit1();
        initService.dbInit2();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {
        private final EntityManager em;

        public void dbInit1() {
            // 멤버 생성
            Member member = new Member();
            member.setName("jinhan");
            member.setAddress(new Address("인천", "계양구 양지로", "324"));
            em.persist(member);

            //Item 생성
            Book book = new Book();
            book.setName("자유론");
            book.setPrice(15000);
            book.setStockQuantity(10);
            em.persist(book);

            Book book2 = new Book();
            book2.setName("명상록");
            book2.setPrice(25000);
            book2.setStockQuantity(30);
            em.persist(book2);


            // order 생성
            OrderItem orderItem1 = OrderItem.createOrderItem(book, 15000, 1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 25000, 2);

            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);

        }

        public void dbInit2() {
            // 멤버 생성
            Member member = new Member();
            member.setName("김진한");
            member.setAddress(new Address("대한민국", "인천", "666"));
            em.persist(member);

            //Item 생성
            Book book = new Book();
            book.setName("자유론2");
            book.setPrice(15000);
            book.setStockQuantity(10);
            em.persist(book);

            Book book2 = new Book();
            book2.setName("명상록2");
            book2.setPrice(25000);
            book2.setStockQuantity(30);
            em.persist(book2);


            // order 생성
            OrderItem orderItem1 = OrderItem.createOrderItem(book, 15000, 1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 25000, 2);

            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);

        }
    }

}
