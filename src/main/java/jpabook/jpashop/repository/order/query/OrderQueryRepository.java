package jpabook.jpashop.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> result = findOrders();
        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });
        return result;
    }


    // 오더 데이터를 먼저 조회하고
    // 그다음에 오더아이템 데이터들을 한번에 다 땡겨온다
    // V4는 오더 아이템 데이터를 호출 할 때 마다 쿼리를 날리는데 V5는 쿼리를 한번만 날린다.
    public List<OrderQueryDto> findAllByDto_optimization() {
        List<OrderQueryDto> result = findOrders();
        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(toOrderIds(result))
                .stream()
                .collect(Collectors.groupingBy(OrderItemQueryDto::getOrderId));
        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));
        return result;
    }

    private List<Long> toOrderIds(List<OrderQueryDto> result) {
        List<Long> orderIds = result.stream().map(OrderQueryDto::getOrderId).collect(Collectors.toList());
        return orderIds;
    }

    private List<OrderItemQueryDto> findOrderItemMap(List<Long> orderIds) {
        List<OrderItemQueryDto> orderItems = em.createQuery(
                        "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)  from OrderItem oi" +
                                " join oi.item i" +
                                " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();
        return orderItems;
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery("select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)  from OrderItem oi" +
                " join oi.item i" +
                " where oi.order.id = :orderId", OrderItemQueryDto.class).setParameter("orderId", orderId).getResultList();

    }

    private List<OrderQueryDto> findOrders() {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDateTime, o.status, d.address) from orders o" +
                        " join o.member m" +
                        " join o.delivery d", OrderQueryDto.class).getResultList();
    }

    public List<OrderFlatDto> findAllByDto_flat() {
        return em.createQuery("select new jpabook.jpashop.repository.order.query.OrderFlatDto(o.id, m.name, o.orderDateTime, o.status, d.address, i.name, oi.orderPrice, oi.count)" +
                " from orders o" +
                " join o.member m" +
                " join o.delivery d" +
                " join o.orderItems oi" +
                " join oi.item i", OrderFlatDto.class).getResultList();
    }
}
