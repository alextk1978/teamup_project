package ru.team.up.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;

/**
 * Таблица статуса мероприятия
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "STATUS")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class Status {

    /**
     * Уникальный идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Статус мероприятия: проверено, закончено, на проверке и т.д.
     */
    @Column(name = "STATUS")
    public String status;
}