package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @NotEmpty
    private String name;

    @Embedded
    private Address address;

    // Member 테이블과 Order 테이블을 맵핑시킨 것이다.
    // 이때 주인은 n인 Order가 된다.
    // 그리고 Order 테이블의 member와 맵핑 시킨다.
    //@JsonIgnore를 붙이면 entity를 리턴할 때 해당 객체는 내보내지 않는다.
    // 양방향 연관관계가 있으면 둘중 하나는 JsonIgnore를 해줘야 한다
    @JsonIgnore
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();
}
