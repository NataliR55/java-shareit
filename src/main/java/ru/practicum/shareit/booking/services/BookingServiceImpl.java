package ru.practicum.shareit.booking.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.repositories.BookingRepository;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.InternalServerError;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    @Transactional
    public BookingDtoOutput create(BookingDtoInput bookingDto, Long userId) {
        User booker = userService.getUserById(userId);
        Item item = itemService.getItemById(bookingDto.getItemId());
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        if (end.isBefore(start) || end.equals(start)) {
            throw new ValidationException(String.format("Wrong booking time start = %s and end = %s", start, end));
        }
        if (itemService.getOwnerId(item.getId()).equals(userId)) {
            throw new AccessException(String.format("Booker cannot be owner of item id: %d", userId));
        }
        if (!item.getAvailable()) {
            throw new ValidationException(String.format("Item with id: %d is not available!", userId));
        }
        Booking booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
        return BookingMapper.toBookingDtoOutput(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDtoOutput approveBooking(Long bookingId, Long userId, Boolean approve) {
        Booking booking = getBookingById(bookingId, userId);
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException(String.format("Booking with id: %d already have status %s",
                    bookingId, BookingStatus.APPROVED));
        }
        checkAccessToBooking(booking, userId, false);
        BookingStatus bookingStatus = approve ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(bookingStatus);
        bookingRepository.updateStatus(bookingStatus, bookingId);
        return BookingMapper.toBookingDtoOutput(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    @Override
    public Booking getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Booking with id: %d not found", bookingId)));
        checkAccessToBooking(booking, userId, true);
        return booking;
    }

    private void checkAccessToBooking(Booking booking, Long userId, boolean accessForBooker) {
        User booker = booking.getBooker();
        if (booker == null) {
            throw new InternalServerError(String.format("For booking with id: %s Bouker is not installed!", booker.getId()));
        }
        Long bookerId = booker.getId();
        Item item = booking.getItem();
        if (item == null) {
            throw new InternalServerError(String.format("For booking with id: %s Item is not installed!", booker.getId()));
        }
        User owner = item.getOwner();
        if (owner == null) {
            throw new InternalServerError(String.format("For booking with id: %s Owner is not installed!", booker.getId()));
        }
        Long ownerId = owner.getId();
        if (ownerId.equals(userId)) {
            return;
        }
        if (accessForBooker && bookerId.equals(userId)) {
            return;
        }
        throw new AccessException(String.format("Access to User id:%s for booking id:%s is denied",
                userId, booking.getId()));
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDtoOutput getBookingDtoById(Long bookingId, Long userId) {
        Booking booking = getBookingById(bookingId, userId);
        return BookingMapper.toBookingDtoOutput(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDtoOutput> getBookingsOfBooker(State state, Long bookerId) {
        userService.getUserById(bookerId);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        List<Booking> bookings;
        switch (state) {
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatus(bookerId, BookingStatus.WAITING, sort);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatus(bookerId, BookingStatus.REJECTED, sort);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBefore(bookerId, LocalDateTime.now(), sort);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfter(bookerId, LocalDateTime.now(), sort);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(bookerId, LocalDateTime.now());
                break;
            default:
                bookings = bookingRepository.findAllByBookerId(bookerId, sort);
        }
        return BookingMapper.toBookingDtoOutputs(bookings);
    }


    @Transactional(readOnly = true)
    @Override
    public List<BookingDtoOutput> getBookingsOfOwner(State state, Long ownerId) {
        userService.getUserById(ownerId);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        List<Booking> bookings;
        switch (state) {
            case WAITING:
                bookings = bookingRepository.findAllByOwnerIdAndStatus(ownerId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByOwnerIdAndStatus(ownerId, BookingStatus.REJECTED);
                break;
            case PAST:
                bookings = bookingRepository.findAllByOwnerIdAndEndBefore(ownerId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByOwnerIdAndStartAfter(ownerId, LocalDateTime.now());
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByOwnerIdAndStartBeforeAndEndAfter(ownerId, LocalDateTime.now());
                break;
            default:
                bookings = bookingRepository.findAllByOwnerId(ownerId, sort);
        }
        return BookingMapper.toBookingDtoOutputs(bookings);
    }

    public void setInItemLastAndNextBooking(Long itemId) {
//TODO
        //последний заказ Item:  последний заказ у которого start <= текущей даты или null
        //следующий заказ Item: первый заказ у которого start >= текущего времени или null
        LocalDateTime now = LocalDateTime.now();
        bookingRepository.findFirstByItemIdAndStatus(itemId, BookingStatus.APPROVED,
                Sort.by(Sort.Direction.DESC, "start"));

    }

}
