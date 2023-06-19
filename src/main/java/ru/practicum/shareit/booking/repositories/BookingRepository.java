package ru.practicum.shareit.booking.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

//TODO переделать всё!!!
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Modifying
    @Query("UPDATE Booking b " +
            "SET b.status = :status  " +
            "WHERE b.id = :bookingId")
    void update(BookingStatus status, Long bookingId);

    List<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);

    //запросы по юзеру

    List<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findAllByBookerIdOrderByStartDesc(long id);

    List<Booking> findAllByBookerIdAndStatusIsOrderByStartDesc(Long id, BookingStatus status);

    List<Booking> findAllByBookerIdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(Long id,
                                                                                 LocalDateTime end,
                                                                                 LocalDateTime start);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(Long id, LocalDateTime time);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(Long id, LocalDateTime time);

    List<Booking> findAllByBookerIdAndStartIsAfterAndStatusIsOrderByStartDesc(Long bookerId,
                                                                              LocalDateTime start,
                                                                              BookingStatus status);
   /*
    @Query("select b from Booking b " +
            "inner join Item i on b.item.id = i.id " +
            "where i.ownerId = :ownerId " +
            "order by b.start desc")
    List<Booking> findAllBookingsOwner(Long ownerId);

    @Query("select b from Booking b " +
            "inner join Item i on b.item.id = i.id " +
            "where i.ownerId = :ownerId " +
            "and :time between b.start and b.end " +
            "order by b.start desc")
    List<Booking> findAllCurrentBookingsOwner(Long ownerId, LocalDateTime time);

    @Query("select b from Booking b " +
            "inner join Item i on b.item.id = i.id " +
            "where i.owner.id = :ownerId " +
            "and b.end < :time " +
            "order by b.start desc")
    List<Booking> findAllPastBookingsOwner(Long ownerId, LocalDateTime time);

    @Query("select b from Booking b " +
            "inner join Item i on b.item.id = i.id " +
            "where i.owner.id = :ownerId " +
            "and b.start > :time " +
            "order by b.start desc")
    List<Booking> findAllFutureBookingsOwner(Long ownerId, LocalDateTime time);

    @Query("select b from Booking b " +
            "inner join Item i on b.item.id = i.id " +
            "where i.owner.id = :ownerId " +
            "and b.start > :time and b.status = :status " +
            "order by b.start desc")
    List<Booking> findAllWaitingBookingsOwner(Long ownerId, LocalDateTime time, BookingStatus status);

    @Query("select b from Booking b " +
            "inner join Item i on b.item.id = i.id " +
            "where i.owner.id = :ownerId " +
            "and b.status = :status " +
            "order by b.start desc")
    List<Booking> findAllRejectedBookingsOwner(Long ownerId, BookingStatus status);

    @Query("select b from Booking b " +
            "inner join Item i on b.item.id = i.id " +
            "where i.id = :itemId " +
            "order by b.start desc")
    List<Booking> findAllBookingsItem(Long itemId);

    List<Booking> findAllByItemIdAndBookerIdAndStatusIsAndEndIsBefore(Long itemId,
                                                                      Long bookerId,
                                                                      BookingStatus status,
                                                                      LocalDateTime time);
*/
}
