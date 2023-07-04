package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    default ItemRequest getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("ItemRequest with id: %d is not found", id)));
    }

    List<ItemRequest> findAllByRequesterIdOrderByCreatedDesc(long id, Pageable pageable);

    @Query(value = "select r from ItemRequest as r join fetch r.requester as u " +
            " where u.id != :userId order by r.created desc")
    Page<ItemRequest> findAllByRequesterIdIsNotEqualId(@Param("userId") Long userId, Pageable pageable);

    Page<ItemRequest> findAllByRequesterIdNot(Long userId, Pageable pageable);

}
