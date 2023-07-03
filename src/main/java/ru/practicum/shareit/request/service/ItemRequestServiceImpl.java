package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.InputItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto add(InputItemRequestDto inputItemRequestDto, Long userId) {
        User user = getUserById(userId);
        ItemRequest itemRequest = itemRequestRepository.save(
                ItemRequestMapper.fromInputItemRequestDto(inputItemRequestDto, user));
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId, int from, int size) {
        getUserById(userId);
        return itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId, getPageRequest(from, size))
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getOtherUserRequests(Long userId, int from, int size) {
        User user = getUserById(userId);
        return itemRequestRepository.findAllByRequestorIsNot(user, getPageRequest(from, size))
                .map(ItemRequestMapper::toItemRequestDto)
                .getContent();
    }

    @Override
    public ItemRequestDto getItemRequestById(Long userId, Long requestId) {
        getUserById(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Request with id %d not found", requestId)));
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id %d not found", userId)));
    }

    private PageRequest getPageRequest(int from, int size) {
        return PageRequest.of(from > 0 ? from / size : 0, size, Sort.by(Sort.Direction.DESC, "created"));
    }
}
