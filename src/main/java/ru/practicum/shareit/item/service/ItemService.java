package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Map;

public interface ItemService {
    ItemDto add(ItemDto item);

    ItemDto update(ItemDto item);

    ItemDto partialUpdate(long id, Map<String, String> updates);

    ItemDto get(long id);

    List<ItemDto> findAll();

    void delete(long id);

    void deleteAll();

}
