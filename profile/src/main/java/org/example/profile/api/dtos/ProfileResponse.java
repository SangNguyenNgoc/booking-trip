package org.example.profile.api.dtos;

import lombok.*;

import java.io.Serializable;

/**
 * DTO for {@link org.example.profile.api.entities.Profile}
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponse implements Serializable {
    private String fullname;
    private String email;
    private String phoneNumber;
    private String type;
}