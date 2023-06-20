package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public ItemDto add(long ownerId, Item item) {
        User owner = userService.getUserById(ownerId);
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

    @Transactional
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

    @Transactional
    @Override
    public Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with id = %d not found.", itemId)));
    }

    @Override
    public ItemDto getItemDtoById(Long itemId, Long userId) {
        Item item = getItemById(itemId);
        List<Comment> comments = commentRepository.findAllByItemId(item.getId());
        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setComments(CommentMapper.toDtoList(comments));
        return itemDto;
    }

    @Transactional
    @Override
    public List<ItemDto> getAllUserItems(Long userId) {
        List<ItemDto> itemDtos = ItemMapper.toItemDtoList(itemRepository.findAllByOwnerId(userId));
        return itemDtos.stream()
                .peek(i -> i.setComments(CommentMapper.toDtoList(commentRepository.findAllByItemId(i.getId()))))
                .collect(Collectors.toList());
    }

    //todo   удалить после тога как разберешься c Page
    public List<ItemDto> getAll(Long ownerId) {
        /*Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        // затем создаём описание первой "страницы" размером 32 элемента
        Pageable page = PageRequest.of(0, 32, sortById);
        do {
            // запрашиваем у базы данных страницу с данными
            Page<Item> itemPage = itemRepository.findAll(page);
            // результат запроса получаем с помощью метода getContent()
            itemPage.getContent().forEach(item -> {
                // проверяем пользователей
            });
            // для типа Page проверяем, существует ли следующая страница
            if(itemPage.hasNext()){
                // если следующая страница существует, создаём её описание, чтобы запросить на следующей итерации цикла
                page = PageRequest.of(itemPage.getNumber() + 1, itemPage.getSize(), itemPage.getSort()); // или для простоты -- userPage.nextOrLastPageable()
            } else {
                page = null;
            }
        } while (page != null);
        */
        return ItemMapper.toItemDtoList(itemRepository.findAllByOwnerId(ownerId));
    }

    @Override
    public void delete(Long ownerId, Long itemId) {
        Item item = getItemById(itemId);
        if (!getOwnerId(itemId).equals(ownerId)) {
            throw new NotFoundException(String.format("User with id:%d is not owner Item with id:%d not found.",
                    ownerId, itemId));
        }
        itemRepository.delete(item);
    }

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

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        Item item = getItemById(itemId);
        User user = userService.getUserById(userId);
//        List<Booking> bookings = bookingRepository
//                .findAllByItemIdAndBookerIdAndStatusIsAndEndIsBefore(itemId, userId, BookingStatus.APPROVED, LocalDateTime.now());
//        log.info(bookings.toString());
//        if (!bookings.isEmpty() && bookings.get(0).getStart().isBefore(LocalDateTime.now())) {
//            Comment comment = CommentMapper.toComment(commentDto);
//            comment.setItem(item);
//            comment.setAuthor(user);
//            comment.setCreated(LocalDateTime.now());
//            return CommentMapper.toDto(commentRepository.save(comment));
//        } else {
//            throw new NotAvailableException(String.format("Booking for User with id = %d and Item with id = %d not found.", userId, itemId));
//        }
        return commentDto;
    }

}

