package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//jpa의 내장 타입리아는 것이다.
@Getter
@Setter
@Entity(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    // 1대 n 관계에서 주인은 n이다
    // Member 테이블과 Order 테이블의 주인은 Order가 된다.
    // 지연로딩인 경우 하이버네티스가 new Proxymember()를 생성한다
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // CascadeType.All 로 설정하면 현재 Entity인 Order를 persist할 때
    // OrderItem 리스트도 같이 persist를 날려준다
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    // List<OrderItem> 과 마찬가지로 CascadeType.ALL 를 설정했기 때문에
    // Order를 persist 할 때 Delivery도 같이 persist 해준다
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDateTime;

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문상태[ORDER, CANCEL]


    //==연관관계 메서드==//
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }


    public void setDeliver(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //== 생성 메서드 ==//
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDeliver(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDateTime(LocalDateTime.now());

        return order;
    }

    public void cancel() {

        //배송 완료된 경우라면
        if (delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송 완료된 상품은 취소가 불가능합니다.");
        }
        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }


    //== 조회 로직 ==//
    public int getTotalPrice() {
        return orderItems.stream().mapToInt(OrderItem::getTotalPrice).sum();
    }
}
