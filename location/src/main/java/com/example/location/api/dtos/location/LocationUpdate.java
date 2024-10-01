package com.example.location.api.dtos.location;

import com.example.location.api.entities.Location;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link Location}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationUpdate implements Serializable {
    @NotNull(message = "Id không đuược để trống.")
    private String id;
    @NotBlank(message = "Tên địa điểm không được để trống.")
    private String name;
    @NotBlank(message = "Địa chỉ không được để trống.")
    private String address;
    @Pattern(message = "Số điện thoại không hợp lệ.", regexp = "^(0[1-9][0-9]{8})$|^(\\\\+84[1-9][0-9]{8})$")
    @NotBlank(message = "Số điện thoại không được để trống.")
    private String hotline;
    @NotBlank(message = "Mô địa điểm không được để trống")
    private String description;
    @NotBlank(message = "Thành phố không được để trống")
    private String regionSlug;
    private Boolean active;

}