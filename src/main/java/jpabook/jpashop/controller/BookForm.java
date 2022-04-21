package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class BookForm {

    private Long id;

    @NotEmpty(message = "이름을 입력해주세요")
    private String name;

    @NotEmpty(message = "저자를 입력해주세요")
    private String author;

    @NotEmpty(message = "isbn을 입력해주세요")
    private String isbn;

    @NotEmpty(message = "가격을 입력해주세요")
    private int price;

    @NotEmpty(message = "재고를 입력해주세요")
    private int stockQuantity;
}
