package org.example.statistics.api.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "bills")
public class Bill {
    private String id;
    private String tripId;
    private Long totalPrice;
    private Integer totalTicket;
    private LocalDateTime createDate;
}
