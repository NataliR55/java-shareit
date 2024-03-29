package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto add(long ownerId, ItemDto itemDto);

    ItemDto update(Long ownerId, Long itemId, ItemDto itemDto);

    Item getItemById(Long itemId);

    ItemDto getItemDtoById(Long itemId, Long userId);

    List<ItemDto> getAllUserItems(Long userId, int from, int size);

    List<ItemDto> searchItems(String query, int from, int size);

    void delete(Long ownerId, Long itemId);

    void deleteAll();

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}
