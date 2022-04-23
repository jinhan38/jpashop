package jpabook.jpashop.api;


import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


/**
 * xToOne(ManyToOne, OneToOne) 에서 성능 최적화를 어떻게 할 것인지 처리
 * Order
 * Order -> Member : ManyToOne
 * Order -> Delivery : OneToOne
 * 양방향이 걸린 애들은 JsonIgnore를 잘 활용해야 한다
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            // 여기서 getMember까지는 아직 Proxy이지만
            // getName으로 실제로 끌고오면 Lazy 강제 초기화 돼서 멤버 쿼리를 날려서 JPA가 데이터를 가져온다
            order.getMember().getName();
            order.getDelivery().getAddress();
        }
        return all;
    }

    // 지연로딩으로 호출할 때 영속성 컨텍스트를 참조해서
    // 동일한 데이터가 있으면 쿼리를 새로 날리지 않고, 기존의 것을 그대로 사용한다.
    // 예를 들어 Member를 조회하기위해서 stream이나 for문을 돌 때
    // 동일한 member일 경우 쿼리를 한번만 날린다.
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> orderV2() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        return all.stream().map(o -> new SimpleOrderDto(o)).collect(Collectors.toList());
    }

    // fetch join은 쿼리를 한번만 날리기 때문에 더 속도가 빠르다
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> orderV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
//        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        return orders.stream().map(SimpleOrderDto::new).collect(Collectors.toList());
    }

    // fetch join은 쿼리를 한번만 날리기 때문에 더 속도가 빠르다
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> orderV4() {
        return orderSimpleQueryRepository.findOrderDtos();
    }

    @Data
    static class SimpleOrderDto {
        public SimpleOrderDto(Order order) {
            this.orderId = order.getId();
            this.name = order.getMember().getName();
            this.orderDate = order.getOrderDateTime();
            this.orderStatus = order.getStatus();
            this.address = order.getDelivery().getAddress();
        }

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
    }

}
