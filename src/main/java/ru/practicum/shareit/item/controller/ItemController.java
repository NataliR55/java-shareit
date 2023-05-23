package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;
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
        return itemService.add(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchUpdate(@RequestHeader(userIdInHeader) long ownerId, @PathVariable long itemId,
                               @RequestBody @NotNull Map<String, String> updates) {
        return itemService.update(ownerId, itemId, updates);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable long itemId) {
        return itemService.get(itemId);
    }

    //DOTO --список должен возвращать название и описание вещи владельца
    @GetMapping
    public List<ItemDto> getAll(@RequestHeader(userIdInHeader) long ownerId) {
        return itemService.getAll(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam(name = "text") String text) {
        return itemService.searchItems(text);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader(userIdInHeader) long ownerId, @PathVariable long itemId) {
        itemService.delete(ownerId, itemId);
    }

}
