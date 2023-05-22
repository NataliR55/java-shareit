package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@Qualifier("itemRepository")
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private long lastId = 0L;

    @Override
    public Item add(Item item) {
        return null;
    }

    @Override
    public Item update(Item item) {
        return null;
    }

    @Override
    public List<Item> findAll() {
        return null;
    }

    @Override
    public Item get(long id) {
        return null;
    }

    @Override
    public void delete(long userId) {

    }

    @Override
    public void deleteAll() {

    }
}
