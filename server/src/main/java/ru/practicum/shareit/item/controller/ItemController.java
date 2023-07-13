package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private static final String USER_ID_IN_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader(USER_ID_IN_HEADER) long ownerId, @RequestBody ItemDto itemDto) {

        return itemService.add(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_ID_IN_HEADER) Long ownerId, @PathVariable Long itemId,
                          @RequestBody ItemDto itemDto) {
        return itemService.update(ownerId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader(USER_ID_IN_HEADER) long userId, @PathVariable long itemId) {
        return itemService.getItemDtoById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAllUserItems(@RequestHeader(USER_ID_IN_HEADER) long ownerId,
                                         @RequestParam(defaultValue = "0") int from,
                                         @RequestParam(defaultValue = "10") int size) {

        return itemService.getAllUserItems(ownerId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader(USER_ID_IN_HEADER) long userId,
                                     @RequestParam(name = "text") String text,
                                     @RequestParam(defaultValue = "0") int from,
                                     @RequestParam(defaultValue = "10") int size) {
        if ((text == null) || (text.isBlank())) {
            return List.of();
        }
        return itemService.searchItems(text, from, size);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader(USER_ID_IN_HEADER) long ownerId, @PathVariable long itemId) {
        itemService.delete(ownerId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(USER_ID_IN_HEADER) long userId, @PathVariable long itemId,
                                 @RequestBody CommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }
}
