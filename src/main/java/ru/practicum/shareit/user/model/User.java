package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Builder
@Entity
@Table(name = "users", schema = "public")
public class User {
    @Id
    private Long id;
    @NotBlank
    @Pattern(regexp = "\\S+")
    @Column(name = "name", nullable = false)
    private String name;
    @Email(message = "Field: Email must have the format EMAIL!")
    @NotBlank(message = "Field: Email must be filled!")
    @Column(name = "email", length = 128, nullable = false, unique = true)
    private String email;

    //@Enumerated(EnumType.STRING)
    //private UserState state;
    //@Column(name = "registration_date")
    //private Instant registrationDate = Instant.now();

}
