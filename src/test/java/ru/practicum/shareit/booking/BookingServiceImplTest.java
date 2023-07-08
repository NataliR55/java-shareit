package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.dto.OutputBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
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
    void beforeEach() {
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
        assertEquals(actualBooking.getBooker().getId(), user2.getId());
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
        assertEquals(BookingStatus.APPROVED, outputBookingDto1.getStatus());
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

    @Test
    void getBookingByIdIsOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        OutputBookingDto outputBookingDto = bookingService.getBookingDtoById(booking.getId(), user.getId());
        assertEquals(outputBookingDto.getId(), booking.getId());
        assertNotNull(outputBookingDto.getStart());
        assertNotNull(outputBookingDto.getEnd());
        assertEquals(BookingStatus.WAITING, outputBookingDto.getStatus());
        assertEquals(outputBookingDto.getBooker().getId(), booking.getBooker().getId());
        assertEquals(outputBookingDto.getItem().getId(), item.getId());
        verify(bookingRepository).findById(anyLong());
    }

    @Test
    void getBookingByIdNotFoundBooking() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        assertThrows(NotFoundException.class, () -> bookingService.getBookingDtoById(0L, user.getId()));
        verify(bookingRepository).findById(anyLong());
    }

    @Test
    void getBookingByIdNotFoundUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        assertThrows(NotFoundException.class, () -> bookingService.getBookingDtoById(booking.getId(), 0L));
        verify(bookingRepository).findById(anyLong());
        verify(userRepository).findById(anyLong());
    }

    @Test
    void getBookingsOfBookerOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        List<Booking> bookings = List.of(booking);
        when(bookingRepository.findAllByBookerId(anyLong(), any())).thenReturn(bookings);
        when(bookingRepository.findAllByBookerIdAndStatus(anyLong(), any(), any())).thenReturn(bookings);
        when(bookingRepository.findAllByBookerIdAndStatus(anyLong(), any(), any())).thenReturn(bookings);
        when(bookingRepository.findAllByBookerIdAndEndBefore(anyLong(), any(), any())).thenReturn(bookings);
        when(bookingRepository.findAllByBookerIdAndStartAfter(anyLong(), any(), any())).thenReturn(bookings);
        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(anyLong(), any(), any())).thenReturn(bookings);
        Long bookerId = booking.getBooker().getId();
        assertEquals(1, bookingService.getBookingsOfBooker(State.ALL, bookerId, 0, 10).size());
        assertEquals(1, bookingService.getBookingsOfBooker(State.WAITING, bookerId, 0, 10).size());
        assertEquals(1, bookingService.getBookingsOfBooker(State.REJECTED, bookerId, 0, 10).size());
        assertEquals(1, bookingService.getBookingsOfBooker(State.CURRENT, bookerId, 0, 10).size());
        assertEquals(1, bookingService.getBookingsOfBooker(State.PAST, bookerId, 0, 10).size());
        assertEquals(1, bookingService.getBookingsOfBooker(State.FUTURE, bookerId, 0, 10).size());
    }

    @Test
    void getBookingsOfOwnerIsOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        List<Booking> bookings = List.of(booking);
        when(bookingRepository.findAllByOwnerIdAndStatus(anyLong(), any(), any())).thenReturn(bookings);
        when(bookingRepository.findAllByOwnerIdAndEndBefore(anyLong(), any(), any())).thenReturn(bookings);
        when(bookingRepository.findAllByOwnerIdAndStartAfter(anyLong(), any(), any())).thenReturn(bookings);
        when(bookingRepository.findAllByOwnerIdAndStartBeforeAndEndAfter(anyLong(), any(), any())).thenReturn(bookings);
        when(bookingRepository.findAllByOwnerId(anyLong(), any())).thenReturn(bookings);
        Long ownerId = booking.getItem().getOwner().getId();
        assertEquals(1, bookingService.getBookingsOfOwner(State.ALL, ownerId, 0, 10).size());
        assertEquals(1, bookingService.getBookingsOfOwner(State.WAITING, ownerId, 0, 10).size());
        assertEquals(1, bookingService.getBookingsOfOwner(State.REJECTED, ownerId, 0, 10).size());
        assertEquals(1, bookingService.getBookingsOfOwner(State.CURRENT, ownerId, 0, 10).size());
        assertEquals(1, bookingService.getBookingsOfOwner(State.PAST, ownerId, 0, 10).size());
        assertEquals(1, bookingService.getBookingsOfOwner(State.FUTURE, ownerId, 0, 10).size());
    }
}