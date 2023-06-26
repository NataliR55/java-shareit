package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final String userIdInHeader = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto create(@RequestHeader(userIdInHeader) long ownerId, @Valid @RequestBody @NotNull ItemDto itemDto) {
        return itemService.add(ownerId, ItemMapper.toItem(itemDto));
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(userIdInHeader) long ownerId, @PathVariable long itemId,
                          @RequestBody @NotNull Map<String, String> updates) {
        return itemService.update(ownerId, itemId, updates);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader(userIdInHeader) long userId, @PathVariable long itemId) {
        return itemService.getItemDtoById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader(userIdInHeader) long ownerId) {
        return itemService.getAllUserItems(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader(userIdInHeader) long userId, @RequestParam(name = "text") String text) {
        return itemService.searchItems(userId, text);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader(userIdInHeader) long ownerId, @PathVariable long itemId) {
        itemService.delete(ownerId, itemId);
    }

    //добавление нового комментария на арендованную вещь(по itemId) может только арендатор уже бравший эту вещь
    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(userIdInHeader) long userId, @PathVariable long itemId,
                           @Valid @RequestBody CommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }
}
