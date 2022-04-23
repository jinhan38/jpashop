package jpabook.jpashop.repository;


import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOrder(Long id) {
        return em.find(Order.class, id);
    }


    // 에러 있어서 작동 안됨
    public List<Order> findAll(OrderSearch orderSearch) {
        return em.createQuery("select o from orders o join o.member m" +
                        " where o.status = :status " +
                        " and m.name like :name", Order.class)
                .setParameter("status", orderSearch.getOrderStatus())
                .setParameter("name", orderSearch.getMemberName())
                .setMaxResults(100) // 최대 100건
                .getResultList();
    }

    public List<Order> findAllByString(OrderSearch orderSearch) {

        String jpql = "select o from orders o join o.member m";
        boolean isFirstCondition = true;

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000);

        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }

        return query.getResultList();
    }


    // fetch join
    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery("select o from orders o" +
                " join fetch o.member m" +
                " join fetch o.delivery d", Order.class).getResultList();
    }


    //join fetch 는 페이징 처리에 사용 불가하다
    // 1대 n의 엔티티 안에 또 1대 n이 있을 경우 distinct를 사용해야 한다.
    // distinct를 사용하지 않으면 1 : n : n 만큼 로우가 늘어나서 return 데이터가 상당히 늘어난다
    public List<Order> findAllWithItem() {
        return em.createQuery("select distinct o from orders o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d" +
                        " join fetch o.orderItems oi" +
                        " join fetch oi.item i", Order.class)
                .getResultList();
    }
}
