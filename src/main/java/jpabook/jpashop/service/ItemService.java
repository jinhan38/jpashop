package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    @Transactional
    public void updateItem(Long id, String name, int price, int stockQuantity) {
        Item item = itemRepository.findItem(id);
        item.setName(name);
        item.setPrice(price);
        item.setStockQuantity(stockQuantity);
        // itemRepository.findItem 으로 불러온 아이템은 영속성 entity다
        //  JPA가 저 item Entity를 관찰하고 있기 때문에 변경이 되면 자동으로 db도 변경시킨다
        // 반면 만약에 findItem으로 불러온 item 바로 수정하지 않고,
        // new Book() 처럼 새로 생성한 데이터에 저 데이터들을 담는다면 jpa가 관찰하지 않는다.
        // 이 경우를 준 영속성 entity라 한다.
        // merge(병합) : 준영속 상태의 엔티티를 영속 상태로 변경할 때 사용한다.(실무에서는 거의 안씀)
        // merge는 데이터가 없는 경우 모두 null로 쳐버리기 때문에 상당히 위험하다.
    }


    public Item findOne(Long id) {
        return itemRepository.findItem(id);
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

}
