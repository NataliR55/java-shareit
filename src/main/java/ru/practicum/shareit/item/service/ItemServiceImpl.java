package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repositories.BookingRepository;
import ru.practicum.shareit.exception.InternalServerError;
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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id %d not found", userId)));
    }

    @Transactional
    @Override
    public ItemDto add(long ownerId, Item item) {
        User owner = getUserById(ownerId);
        item.setOwner(owner);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public ItemDto update(Long ownerId, Long itemId, Map<String, String> updates) {
        Item item = getItemById(itemId);
        if (!getOwnerId(itemId).equals(ownerId)) {
            throw new NotFoundException(String.format("User with id:%s is not owner Item with id: %s", ownerId, itemId));
        }
        if (updates.containsKey("name")) {
            String value = updates.get("name");
            checkString(value, "Name");
            log.info("Change name item {} owner {}", itemId, ownerId);
            item.setName(value);
        }
        if (updates.containsKey("description")) {
            String value = updates.get("description");
            checkString(value, "Name");
            item.setDescription(value);
        }
        if (updates.containsKey("available")) {
            item.setAvailable(Boolean.valueOf(updates.get("available")));
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional(readOnly = true)
    @Override
    public Long getOwnerId(Long itemId) {
        Item item = getItemById(itemId);
        User owner = item.getOwner();
        if (owner == null) throw new InternalServerError("Item with id = %d not have owner!");
        return owner.getId();
    }

    private void checkString(String value, String name) {
        if (value == null || value.isBlank()) {
            log.info("{} item is empty!", name);
            throw new ValidationException(String.format("%s item is empty!", name));
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
        List<Comment> comments = commentRepository.findAllByItemId(item.getId(),
                Sort.by(Sort.Direction.ASC, "created"));
        ItemDto itemDto = ItemMapper.toItemDto(item);
        if (item.getOwner() != null && item.getOwner().getId().equals(userId)) {
            setBookings(itemDto, bookingRepository.findAllByItemIdAndStatus(itemId, BookingStatus.APPROVED));
        }
        setComments(itemDto, comments);
        return itemDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> getAllUserItems(Long userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        List<Booking> bookings = bookingRepository.findAllByOwnerIdAndStatus(userId, BookingStatus.APPROVED);
        List<Comment> comments = commentRepository.findAllByItemIdIn(items.stream()
                .map(Item::getId)
                .collect(Collectors.toList()), Sort.by(Sort.Direction.ASC, "created"));
        List<ItemDto> itemDtos = ItemMapper.toItemDtoList(items);
        itemDtos.forEach(i -> {
            setBookings(i, bookings);
            setComments(i, comments);
        });
        return itemDtos;
    }

    private void setBookings(ItemDto itemDto, List<Booking> bookings) {
        LocalDateTime now = LocalDateTime.now();
        Long itemId = itemDto.getId();
        itemDto.setLastBooking(bookings.stream()
                .filter(booking -> booking.getItem().getId().equals(itemId))
                .filter(booking -> booking.getStart().compareTo(now) <= 0)
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .limit(1)
                .map(BookingMapper::toBookingDtoShort)
                .findFirst().orElse(null));
        itemDto.setNextBooking(bookings.stream()
                .filter(booking -> booking.getItem().getId().equals(itemId))
                .filter(booking -> booking.getStart().compareTo(now) > 0)
                .sorted(Comparator.comparing(Booking::getStart))
                .limit(1)
                .map(BookingMapper::toBookingDtoShort)
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
        Item item = getItemById(itemId);
        if (!getOwnerId(itemId).equals(ownerId)) {
            throw new NotFoundException(String.format("User with id:%d is not owner Item with id:%d not found.",
                    ownerId, itemId));
        }
        itemRepository.delete(item);
    }

    @Transactional
    @Override
    public void deleteAll() {
        itemRepository.deleteAll();
    }

    @Override
    public List<ItemDto> searchItems(Long userId, String query) {
        if (query == null || query.isBlank()) {
            return Collections.emptyList();
        }
        return ItemMapper.toItemDtoList(itemRepository.searchAvailableItems(query));
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


/* TODO  переделать запрос по comments to Item  - здесь интеросно работа с Map и исключение множества запросов

    public Collection<PostWithCommentsDto> getComments() {
        // выгружаем посты (один запрос)
        Map<Long, Post> postMap = postService.findAllPostsWithAuthors()
                .stream()
                .collect(Collectors.toMap(Post::getId, Function.identity()));
        // выгружаем комментарии (ещё один запрос)
        Map<Long, List<Comment>> commentMap = commentService.getByPostId(postMap.keySet())
                .stream()
                .collect(Collectors.groupingBy(Comment::getPostId));
        // готовим окончательный результат из полученных данных (нет обращений к БД)
        return postMap.values()
                .stream()
                .map(post -> makePostWithCommentsDto(
                        post,
                        commentMap.getOrDefault(post.getId(), Collections.emptyList())
                ))
                .collect(Collectors.toList());
    }

    private PostWithCommentsDto makePostWithCommentsDto(Post post, List<Comment> comments) {
        // конвертируем комментарии в DTO
        List<CommentDto> commentDtos = comments
                .stream()
                .map(comment -> CommentDto.of(comment.getId(), comment.getText(), comment.getPostId()))
                .collect(Collectors.toList());
        // формируем окончательное представление для данного поста
        User author = post.getAuthor();

        return PostWithCommentsDto.of(
                post.getId(), post.getTitle(), post.getText(),
                UserDto.of(author.getId(), author.getName(), author.getEmail()),
                commentDtos
        );
    }




 */
}

