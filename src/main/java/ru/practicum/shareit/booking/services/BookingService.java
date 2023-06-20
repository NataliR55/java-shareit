package ru.practicum.shareit.booking.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repositories.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Transactional
    public BookingDtoOutput create(BookingDtoInput bookingDto, Long userId) {
        User booker = userService.getUserById(userId);
        Item item = itemService.getItemById(bookingDto.getItemId());
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        if (end.isBefore(start) || end.equals(start)) {
            throw new ValidationException(String.format("Wrong booking time start = %s  end = %s", start, end));
        }
        if (itemService.getOwnerId(item.getId()).equals(booker.getId())) {
            throw new ValidationException(String.format("Booker cannot be owner of item id %s", userId));
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

    @Transactional
    public Booking findBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Booking with id: %d not found", bookingId)));
        User booker = booking.getBooker();
        Item item = booking.getItem();
        User owner = item.getOwner();
        if ((booker.getId().equals(userId)) || owner.getId().equals(userId)) {
            return booking;
        }
        throw new NotFoundException(String.format("User with id: %d is not the owner item with id: %d or is not booker.",
                userId, item.getId()));
    }

    @Transactional
    public BookingDtoOutput approveBooking(long bookingId, long userId, Boolean approve) {
        Booking booking = findBookingById(bookingId, userId);
        Long ownerId = itemService.getOwnerId(booking.getItem().getId());
        if (ownerId.equals(userId) && booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException("The booking decision has already been made.");
        }
        if (!ownerId.equals(userId)) {
            throw new ValidationException(String.format("User with id = %d is not the owner, no access to booking.", userId));
        }
        if (approve) {
            booking.setStatus(BookingStatus.APPROVED);
            bookingRepository.update(BookingStatus.APPROVED, bookingId);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
            bookingRepository.update(BookingStatus.REJECTED, bookingId);
        }
        return BookingMapper.toBookingDtoOutput(bookingRepository.save(booking));
    }
}
