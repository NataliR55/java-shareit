package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated

public class ItemRequestController {
    private static final String USER_ID_IN_HEADER = "X-Sharer-User-Id";
    private final ItemRequestClient itemRequestClient;


    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(USER_ID_IN_HEADER) @Positive Long userId,
                                                    @RequestBody @Valid @NotNull ItemRequestDto request) {
        return itemRequestClient.createItemRequest(userId, request);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader(USER_ID_IN_HEADER) @Positive Long userId,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                  @RequestParam(defaultValue = "10") @Positive int size) {
        return itemRequestClient.getUserRequests(userId, from, size);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getOtherUserRequests(@RequestHeader(USER_ID_IN_HEADER) @Positive Long userId,
                                                        @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                        @RequestParam(defaultValue = "10") @Positive Integer size) {
        return itemRequestClient.getOtherUserRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(USER_ID_IN_HEADER) @Positive Long userId,
                                                 @PathVariable @Positive Long requestId) {
        return itemRequestClient.getItemRequestById(userId, requestId);
    }
}