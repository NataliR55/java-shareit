package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingPostRequestDto;
import ru.practicum.shareit.exception.StateException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingClient bookingClient;
    private static final String USER_ID_IN_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(USER_ID_IN_HEADER) @Positive long userId,
                                                @RequestBody @Valid BookingPostRequestDto requestDto) {
        return bookingClient.createBooking(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader(USER_ID_IN_HEADER) @Positive long ownerId,
                                                 @PathVariable @Positive long bookingId,
                                                 @RequestParam(value = "approved", required = false) boolean approved) {
        return bookingClient.approveBooking(bookingId, ownerId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(USER_ID_IN_HEADER) @Positive long userId,
                                             @PathVariable @Positive long bookingId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsOfBooker(@RequestHeader(USER_ID_IN_HEADER) long bookerId,
                                                      @RequestParam(name = "state", defaultValue = "ALL")
                                                      String stateParam,
                                                      @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero
                                                      int from,
                                                      @RequestParam(name = "size", defaultValue = "10") @Positive
                                                      int size) {
        State state = State.from(stateParam)
                .orElseThrow(() -> new StateException("Unknown state: " + stateParam));
        return bookingClient.getBookingsOfBooker(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsOfOwner(@RequestHeader(USER_ID_IN_HEADER) long ownerId,
                                                     @RequestParam(name = "state", defaultValue = "ALL")
                                                     String stateParam,
                                                     @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                     int from,
                                                     @Positive @RequestParam(name = "size", defaultValue = "10")
                                                     int size) {
        State state = State.from(stateParam)
                .orElseThrow(() -> new StateException("Unknown state: " + stateParam));
        return bookingClient.getBookingsOfOwner(ownerId, state, from, size);
    }
}