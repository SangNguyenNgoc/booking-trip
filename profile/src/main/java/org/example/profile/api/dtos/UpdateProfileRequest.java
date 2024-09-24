package org.example.profile.api.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.example.profile.api.entities.Profile;

import java.io.Serializable;

/**
 * DTO for {@link Profile}
 */
@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest implements Serializable {
    @NotBlank(message = "fullname must not be blank")
    private String fullname;
    @Email(message = "Email invalid")
    private String email;
    @Pattern(regexp = "^0(3[2-9]|5[6|8|9]|7[0|6-9]|8[1-5]|9[0-9])[0-9]{7}$\n", message = "Phone number invalid")
    private String phoneNumber;
}