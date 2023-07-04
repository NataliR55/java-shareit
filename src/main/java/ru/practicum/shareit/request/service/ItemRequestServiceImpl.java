package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.InputItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public ItemRequestDto add(InputItemRequestDto inputItemRequestDto, Long userId) {
        User user = getUserById(userId);
        ItemRequest itemRequest = itemRequestRepository.save(ItemRequest.builder()
                .description(inputItemRequestDto.getDescription())
                .created(LocalDateTime.now())
                .requester(user)
                .build());
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId, int from, int size) {
        getUserById(userId);
        return itemRequestsToDto(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId
                , pageRequest(from, size)));
    }

    @Override
    public List<ItemRequestDto> getOtherUserRequests(Long userId, int from, int size) {
        getUserById(userId);
        return itemRequestsToDto(itemRequestRepository.findAllByRequesterIdIsNotEqualId(userId, pageRequest(from, size))
                .getContent());
    }

    @Override
    public ItemRequestDto getItemRequestById(Long userId, Long requestId) {
        getUserById(userId);
        ItemRequest itemRequest = itemRequestRepository.getById(requestId);
        return ItemRequestMapper.toItemRequestDto(itemRequest, itemRepository.findAllByRequestId(requestId));
    }

    private List<ItemRequestDto> itemRequestsToDto(List<ItemRequest> itemRequests) {
        List<Long> itemRequestsIds = itemRequests
                .stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());
        Map<Long, List<Item>> itemsByRequest = itemRepository.findAllByRequestIdIn(itemRequestsIds)
                .stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));
        return itemRequests
                .stream()
                .map(r -> ItemRequestMapper.toItemRequestDto(r, itemsByRequest.getOrDefault(r.getId(), List.of())))
                .collect(Collectors.toList());
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id %d not found", userId)));
    }

    private PageRequest pageRequest(int from, int size) {
        return PageRequest.of(from > 0 ? from / size : 0, size, Sort.by(Sort.Direction.DESC, "created"));
    }
}
