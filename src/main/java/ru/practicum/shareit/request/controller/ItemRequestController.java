package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.InputItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;

/*
    Эндпоинты:

    POST /requests — добавить новый запрос вещи. Основная часть запроса — текст запроса,
                    где пользователь описывает, какая именно вещь ему нужна.
    GET /requests — получить список своих запросов вместе с данными об ответах на них.
                    Для каждого запроса должны указываться описание, дата и время создания и список ответов в формате:
                    id вещи, название, id владельца. Так в дальнейшем, используя указанные id вещей,
                    можно будет получить подробную информацию о каждой вещи.
                     Запросы должны возвращаться в отсортированном порядке от более новых к более старым.
    GET /requests/all?from={from}&size={size} — получить список запросов, созданных другими пользователями.
                    С помощью этого эндпоинта пользователи смогут просматривать существующие запросы,
                    на которые они могли бы ответить. Запросы сортируются по дате создания: от более новых
                    к более старым. Результаты должны возвращаться постранично. Для этого нужно передать два параметра:
                    from — индекс первого элемента, начиная с 0, и size — количество элементов для отображения.
    GET /requests/{requestId} — получить данные об одном конкретном запросе вместе с данными об ответах на него в том же
                    формате, что и в эндпоинте GET /requests. Посмотреть данные об отдельном запросе может любой пользователь.
 */
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private static final String USER_ID_IN_HEADER = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader(USER_ID_IN_HEADER) Long userId,
                                 @RequestBody @Valid InputItemRequestDto inputItemRequestDto) {
        return itemRequestService.add(inputItemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader(USER_ID_IN_HEADER) Long userId,
                                                @RequestParam(defaultValue = "0") @Min(0) int from,
                                                @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        return itemRequestService.getUserRequests(userId, from, size);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getOtherUsersRequests(@RequestHeader(USER_ID_IN_HEADER) Long userId,
                                                      @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                      @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer size) {
        return itemRequestService.getOtherUserRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(USER_ID_IN_HEADER) Long userId,
                                         @PathVariable Long requestId) {
        return itemRequestService.getItemRequestById(userId, requestId);
    }

}
