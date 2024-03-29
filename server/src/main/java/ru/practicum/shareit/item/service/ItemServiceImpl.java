package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final Sort sort = Sort.by(Sort.Direction.ASC, "created");
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id %d not found", userId)));
    }

    private void existsUserWithId(Long userId) {
        if (!userRepository.existsUserById(userId)) {
            throw new NotFoundException(String.format("User with id %d not found", userId));
        }
    }

    @Transactional
    @Override
    public ItemDto add(long ownerId, ItemDto itemDto) {
        User owner = getUserById(ownerId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        if (itemDto.getRequestId() != null) {
            Long requestId = itemDto.getRequestId();
            item.setRequest(itemRequestRepository.findById(requestId)
                    .orElseThrow(() -> new NotFoundException(
                            String.format("Request with id:%s is not found ", requestId))));
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemDto itemDto) {
        existsUserWithId(ownerId);
        Item item = getItemById(itemId);
        checkOwnerOfItem(ownerId, item);
        int i = 0;
        if ((itemDto.getName() != null) && (!itemDto.getName().isBlank())) {
            i++;
            item.setName(itemDto.getName());
        }
        if ((itemDto.getDescription() != null) && (!itemDto.getDescription().isBlank())) {
            i++;
            item.setDescription(itemDto.getDescription());
        }
        if ((itemDto.getAvailable() != null)) {
            i++;
            item.setAvailable(itemDto.getAvailable());
        }
        if (i > 0) {
            itemRepository.save(item);
        }
        return ItemMapper.toItemDto(item);
    }

    private void checkOwnerOfItem(Long ownerId, Item item) {
        User owner = item.getOwner();
        if ((owner == null) || (!owner.getId().equals(ownerId))) {
            throw new NotFoundException(String.format("User with id:%s is not owner Item with id: %s", ownerId,
                    item.getId()));
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with id = %d not found.", itemId)));
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDto getItemDtoById(Long itemId, Long userId) {
        Item item = getItemById(itemId);
        List<Comment> comments = commentRepository.findAllByItemId(item.getId(), sort);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        if (item.getOwner() != null && item.getOwner().getId().equals(userId)) {
            setBookings(itemDto,
                    bookingRepository.findAllByItemIdAndStatus(itemId, BookingStatus.APPROVED,
                            PageRequest.of(0, 10000, BookingRepository.SORT_BY_START_BY_DESC)));
        }
        setComments(itemDto, comments);
        return itemDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> getAllUserItems(Long userId, int from, int size) {
        existsUserWithId(userId);
        List<Item> items = itemRepository.findAllByOwnerId(userId, PageRequest.of(from / size, size,
                Sort.by(Sort.Direction.DESC, "name")));
        Pageable pageable = PageRequest.of(from / size, size, BookingRepository.SORT_BY_START_BY_DESC);
        List<Booking> bookings = bookingRepository.findAllByOwnerIdAndStatus(userId, BookingStatus.APPROVED, pageable);
        List<Comment> comments = commentRepository.findAllByItemIdIn(items.stream()
                .map(Item::getId)
                .collect(Collectors.toList()), sort);
        List<ItemDto> itemsDto = ItemMapper.toItemDtoList(items);
        itemsDto.forEach(i -> {
            setBookings(i, bookings);
            setComments(i, comments);
        });
        return itemsDto;
    }

    private void setBookings(ItemDto itemDto, List<Booking> bookings) {
        LocalDateTime now = LocalDateTime.now();
        Long itemId = itemDto.getId();
        itemDto.setLastBooking(bookings.stream()
                .filter(booking -> booking.getItem().getId().equals(itemId))
                .filter(booking -> booking.getStart().isBefore(now))
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .limit(1)
                .map(BookingMapper::toShortBookingDto)
                .findFirst().orElse(null));
        itemDto.setNextBooking(bookings.stream()
                .filter(booking -> booking.getItem().getId().equals(itemId))
                .filter(booking -> booking.getStart().isAfter(now))
                .sorted(Comparator.comparing(Booking::getStart))
                .limit(1)
                .map(BookingMapper::toShortBookingDto)
                .findFirst().orElse(null));
    }

    private void setComments(ItemDto itemDto, List<Comment> comments) {
        Long itemId = itemDto.getId();
        itemDto.setComments(comments.stream()
                .filter(comment -> comment.getItem().getId().equals(itemId))
                .map(CommentMapper::toDto)
                .collect(Collectors.toList()));
    }

    @Transactional
    @Override
    public void delete(Long ownerId, Long itemId) {
        existsUserWithId(ownerId);
        Item item = getItemById(itemId);
        checkOwnerOfItem(ownerId, item);
        itemRepository.delete(item);
    }

    @Transactional
    @Override
    public void deleteAll() {
        itemRepository.deleteAll();
    }

    @Override
    public List<ItemDto> searchItems(String query, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return ItemMapper.toItemDtoList(itemRepository.searchAvailableItems(query, pageable));
    }

    @Transactional
    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id = %d is not found.", userId)));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with id = %d is not found.", itemId)));
        bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(itemId, userId, BookingStatus.APPROVED,
                        LocalDateTime.now())
                .orElseThrow(() -> new ValidationException(String.format("User with id = %d is not be booking.", userId)));
        Comment comment = CommentMapper.toComment(commentDto);
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.toDto(commentRepository.save(comment));
    }
}

