package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
class OrderServiceTest {


    @Autowired
    EntityManager em;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception {

        //given
        Member member = createMember("김진한", new Address("인천광역시", "계양구 동양동", "158225"));

        Book book = createBook("위대한 개츠비", 10000, 10);

        int orderCount = 2;


        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order order = orderRepository.findOrder(orderId);

        assertEquals("상품 주문 시 상태 값은 ORDER", OrderStatus.ORDER, order.getStatus());
        assertEquals("OrderItem의 개수 확인", 1, order.getOrderItems().size());
        assertEquals("결제금액 체크", 10000 * orderCount, order.getTotalPrice());
        assertEquals("재고 체크", 8, book.getStockQuantity());

    }


    @Test
    public void 상품주문_재고수량초과() throws Exception {

        //given
        Member member = createMember("김진한", new Address("인천광역시", "계양구 동양동", "158225"));

        Book book = createBook("위대한 개츠비", 14000, 10);

        int orderCount = 10;

        //when
        orderService.order(member.getId(), book.getId(), orderCount);

        //then
//        Assert.fail("재고수량 에러");
    }

    @Test
    public void 주문취소() throws Exception {

        //given
        Member member = createMember("김진한", new Address("인천광역시", "계양구 동양동", "158225"));

        Book book = createBook("위대한 개츠비", 14000, 10);

        int orderCount =2;

        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //when
        orderService.cancelOrder(orderId);

        //then
        Order order = orderRepository.findOrder(orderId);

        assertEquals("주문취소상태", OrderStatus.CANCEL, order.getStatus());
        assertEquals("재고 체크", 10, book.getStockQuantity());

    }


    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember(String name, Address address) {
        Member member = new Member();
        member.setName(name);
        member.setAddress(address);
        em.persist(member);
        return member;
    }
}