package org.example.vehicle.utils.auditing;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class AuditorEntity extends TimestampEntity {

    @CreatedBy
    @Column(name = "create_by", updatable = false, nullable = false)
    private String createBy;

    @LastModifiedBy
    @Column(name = "last_modified_by", insertable = false, nullable = true)
    private String lastModifiedBy;
}
