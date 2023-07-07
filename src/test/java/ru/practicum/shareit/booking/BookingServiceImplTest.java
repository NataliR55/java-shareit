package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.dto.OutputBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.InternalServerError;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @InjectMocks
    BookingServiceImpl bookingService;

    InputBookingDto inputBookingDto;
    Booking booking;
    User user, user2, user3;
    Item item;

    @BeforeEach
    void BeforeEach() {
        LocalDateTime start = LocalDateTime.now().plusMinutes(1);
        LocalDateTime end = LocalDateTime.now().plusMinutes(30);
        user = User.builder().id(1L).name("user1").email("user1@mail.ru").build();
        user2 = User.builder().id(2L).name("user2").email("user2@mail.ru").build();
        user3 = User.builder().id(3L).name("user3").email("user3@mail.ru").build();
        item = Item.builder().id(1L).name("item1").description("itemDescription1").available(true)
                .owner(user).request(null).build();
        booking = Booking.builder().id(1L).item(item).booker(user2).status(BookingStatus.WAITING)
                .start(start).end(end).build();
        inputBookingDto = new InputBookingDto(item.getId(), start, end);
    }

    @Test
    void createIsOk() {
        item.setOwner(User.builder().id(2L).build());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(booking);
        OutputBookingDto actualBooking = bookingService.create(inputBookingDto, user.getId());
        assertEquals(booking.getId(), actualBooking.getId());
        assertNotNull(actualBooking.getStart());
        assertNotNull(actualBooking.getEnd());
        assertEquals(BookingStatus.WAITING, actualBooking.getStatus());
        assertEquals(actualBooking.getBooker().getId(), user.getId());
        verify(bookingRepository).save(any());
    }

    @Test
    void createUserEmpty() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingService.create(inputBookingDto, user.getId()));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createItemEmpty() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingService.create(inputBookingDto, user.getId()));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createAvailableFalse() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        item.setOwner(User.builder().id(2L).build());
        item.setAvailable(false);
        assertThrows(ValidationException.class, () -> bookingService.create(inputBookingDto, user.getId()));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void approveBookingIsOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        OutputBookingDto outputBookingDto1 = bookingService.approveBooking(booking.getId(), user.getId(), true);
        assertEquals(outputBookingDto1.getStatus(), BookingStatus.APPROVED);
        verify(userRepository).findById(anyLong());
        verify(bookingRepository).findById(anyLong());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void approveBookingNotOwner() {
        assertThrows(NotFoundException.class, () -> bookingService.approveBooking(booking.getId(), user3.getId(), true));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void approveBookingAlreadyApproved() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        booking.setStatus(BookingStatus.APPROVED);
        assertThrows(ValidationException.class, () -> bookingService.approveBooking(booking.getId(), user.getId(), true));
        verify(userRepository).findById(anyLong());
        verify(bookingRepository).findById(anyLong());
        verify(bookingRepository, never()).save(any());
    }
/*
        @Test
        void findByIdOk () {
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
            when(bookingRepository.get(anyLong())).thenReturn(booking);

            BookingDto foundBookingDto = bookingService.findById(1L, booking.getId());

            assertEquals(foundBookingDto.getId(), 1L);
            assertNotNull(foundBookingDto.getStart());
            assertNotNull(foundBookingDto.getEnd());
            assertEquals(foundBookingDto.getStatus(), Status.WAITING);
            assertEquals(foundBookingDto.getBooker().getId(), 1L);
            assertEquals(foundBookingDto.getItem().getId(), item.getId());
        }

        @Test
        void findByUserIdNotFound () {
            assertThrows(NotFoundException.class, () -> bookingService.findById(3L, booking.getId()));
        }

        @Test
        void findByBookingIdNotFound () {
            lenient().when(bookingRepository.get(anyLong())).thenThrow(NotFoundException.class);
            assertThrows(NotFoundException.class, () -> bookingService.findById(user.getId(), 3L));
        }

        @Test
        void findAllByBookerIsOk () {
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
            List<Booking> bookings = new ArrayList<>(List.of(booking));
            when(bookingRepository.findAllByBookerId(anyLong(), any())).thenReturn(bookings);
            when(bookingRepository.findAllByBookerIdAndStatusEquals(anyLong(), any(), any())).thenReturn(bookings);
            when(bookingRepository.findAllByBookerCurrent(anyLong(), any(), any())).thenReturn(bookings);
            when(bookingRepository.findAllByBookerIdAndEndBefore(anyLong(), any(), any())).thenReturn(bookings);
            when(bookingRepository.findAllFutureForBooker(anyLong(), any(), any())).thenReturn(bookings);

            assertEquals(bookingService.findAllForBooker(booking.getBooker().getId(), "ALL", 0, 20).size(), 1);
            assertEquals(bookingService.findAllForBooker(booking.getBooker().getId(), "WAITING", 0, 20).size(), 1);
            assertEquals(bookingService.findAllForBooker(booking.getBooker().getId(), "REJECTED", 0, 20).size(), 1);
            assertEquals(bookingService.findAllForBooker(booking.getBooker().getId(), "CURRENT", 0, 20).size(), 1);
            assertEquals(bookingService.findAllForBooker(booking.getBooker().getId(), "PAST", 0, 20).size(), 1);
            assertEquals(bookingService.findAllForBooker(booking.getBooker().getId(), "FUTURE", 0, 20).size(), 1);
        }

        @Test
        void findAllByBookerBadState () {
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
            assertThrows(InvalidStateException.class, () -> bookingService.findAllForBooker(booking.getBooker().getId(), "BadState", 0, 20));
        }

        @Test
        void findAllByOwnerIsOk () {
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
            when(itemRepository.findFirstByOwnerId(anyLong())).thenReturn(Optional.of(item));
            List<Booking> bookings = new ArrayList<>(List.of(booking));
            when(bookingRepository.findAllByOwner(anyLong(), any())).thenReturn(bookings);
            when(bookingRepository.findAllByOwnerAndStatus(anyLong(), any(), any())).thenReturn(bookings);
            when(bookingRepository.findAllByOwnerCurrent(anyLong(), any(), any())).thenReturn(bookings);
            when(bookingRepository.findAllByOwnerAndEndBefore(anyLong(), any(), any())).thenReturn(bookings);
            when(bookingRepository.findAllFutureForOwner(anyLong(), any(), any())).thenReturn(bookings);

            assertEquals(bookingService.findAllForOwner(booking.getItem().getOwner().getId(), "ALL", 0, 20).size(), 1);
            assertEquals(bookingService.findAllForOwner(booking.getItem().getOwner().getId(), "WAITING", 0, 20).size(), 1);
            assertEquals(bookingService.findAllForOwner(booking.getItem().getOwner().getId(), "REJECTED", 0, 20).size(), 1);
            assertEquals(bookingService.findAllForOwner(booking.getItem().getOwner().getId(), "CURRENT", 0, 20).size(), 1);
            assertEquals(bookingService.findAllForOwner(booking.getItem().getOwner().getId(), "PAST", 0, 20).size(), 1);
            assertEquals(bookingService.findAllForOwner(booking.getItem().getOwner().getId(), "FUTURE", 0, 20).size(), 1);
        }

        @Test
        void findAllByOwnerBadState () {
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
            when(itemRepository.findFirstByOwnerId(anyLong())).thenReturn(Optional.of(item));
            assertThrows(InvalidStateException.class, () -> bookingService.findAllForOwner(booking.getItem().getOwner().getId(), "BadState", 0, 20));
        }

    */
}