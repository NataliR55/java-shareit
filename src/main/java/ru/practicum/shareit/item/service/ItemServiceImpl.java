package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    @Autowired
    public ItemServiceImpl(@Qualifier("itemRepository") ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemDto add(ItemDto item) {
        return null;
    }

    @Override
    public ItemDto update(ItemDto item) {
        return null;
    }

    @Override
    public ItemDto partialUpdate(long id, Map<String, String> updates) {
        return null;
    }

    @Override
    public ItemDto get(long id) {
        return null;
    }

    @Override
    public List<ItemDto> findAll() {
        return null;
    }

    @Override
    public void delete(long id) {

    }

    @Override
    public void deleteAll() {

    }
}
