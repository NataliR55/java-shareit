package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Builder
@Entity
@Table(name = "users", schema = "public")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Pattern(regexp = "\\S+")
    @Column(name = "name", length = 128, nullable = false)
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
