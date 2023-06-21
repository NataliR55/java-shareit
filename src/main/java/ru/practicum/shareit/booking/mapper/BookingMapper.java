package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {
    public static BookingDtoOutput toBookingDtoOutput(Booking booking) {
        return BookingDtoOutput.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toItemDto(booking.getItem()))
                .booker(UserMapper.toUserDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public static List<BookingDtoOutput> toBookingDtoOutputs(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toBookingDtoOutput)
                .collect(Collectors.toList());
    }
}
