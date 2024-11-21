package org.example.profile.api.entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.profile.utils.auditing.TimestampEntity;
import org.hibernate.annotations.Type;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "profiles")
@Entity
public class Profile extends TimestampEntity {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "full_name", nullable = false)
    private String fullname;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone_number", nullable = true)
    private String phoneNumber;

    @Column(name = "type", nullable = false)
    private String type;

    @PrePersist
    public void generateUUID() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }
}
