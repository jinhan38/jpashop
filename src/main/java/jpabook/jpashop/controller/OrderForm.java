package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.item.Item;

import java.util.List;

public class OrderForm {

    Member member ;
    Delivery delivery;
    List<Item> itemList;
}
