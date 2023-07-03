package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.InputItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.stream.Collectors;

public class ItemRequestMapper {
    public static ItemRequest fromInputItemRequestDto(InputItemRequestDto dto, User owner) {
        return ItemRequest.builder()
                .description(dto.getDescription())
                .created(LocalDateTime.now())
                .requestor(owner)
                .build();
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .requestor(UserMapper.toUserDto(itemRequest.getRequestor()))
                .items(itemRequest.getItems() != null ? itemRequest.getItems()
                        .stream()
                        .map(ItemMapper::toItemDto)
                        .collect(Collectors.toList())
                        : Collections.emptyList())
                .build();



    }
}
