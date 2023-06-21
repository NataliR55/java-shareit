package ru.practicum.shareit.booking.services;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {

    BookingDtoOutput create(BookingDtoInput bookingDto, Long userId);

    BookingDtoOutput approveBooking(Long bookingId, Long userId, Boolean approve);

    Booking getBookingById(Long bookingId, Long userId);

    BookingDtoOutput getBookingDtoById(Long bookingId, Long userId);

    List<BookingDtoOutput> getBookingsOfBooker(State state, Long bookerId);

    List<BookingDtoOutput> getBookingsOfOwner(State state, Long ownerId);

}
