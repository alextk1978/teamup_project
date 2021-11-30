package ru.team.up.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Сущность Мероприятий
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "EVENT")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@ApiModel(value = "Event", description = "Сущность Мероприятий")
public class Event {
    /**
     * Первичный ключ
     */
    @ApiModelProperty(value = "id", required = true, example = "1")
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название мероприятия
     */
    @ApiModelProperty(value = "Название мероприятия", required = true, example = "JOKER-2021")
    @Column(name = "EVENT_NAME", nullable = false)
    private String eventName;

    /**
     * Описание мероприятий
     */
    @ApiModelProperty(value = "Описание мероприятия", required = true, example = "Конференция JAVA-разработчиков")
    @Column(name = "DESCRIPTION_EVENT", nullable = false)
    private String descriptionEvent;

    /**
     * Место проведения мероприятия
     */
    @ApiModelProperty(value = "Место проведения мероприятия", required = true, example = "Online")
    @Column(name = "PLACE_EVENT", nullable = false)
    private String placeEvent;

    /**
     * Время проведения мероприятия
     */
    @ApiModelProperty(value = "Время проведения мероприятия", required = true, example = "28.11.2021 20:00")
    @Column(name = "TIME_EVENT", nullable = false)
    private LocalDateTime timeEvent;

    /**
     * Время обновления мероприятия
     */
    @ApiModelProperty(value = "Время обновления мероприятия", required = true, example = "29.11.2021 21:00")
    @Column(name = "EVENT_UPDATE_DATE")
    private LocalDate eventUpdateDate;

    /**
     * Участники мероприятия
     */
    @ApiModelProperty(name = "participantsEvent", value = "Участники мероприятия",
            required = true, dataType = "User")
    @ManyToMany(mappedBy = "userEvent",
            cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    private List<User> participantsEvent;

    /**
     * Тип мероприятия
     */
    @ApiModelProperty(name = "eventType", value = "Тип мероприятия", required = true, dataType = "EventType")
    @ManyToOne(optional = false, cascade = CascadeType.MERGE)
    @JoinColumn(name = "EVENT_TYPE_ID")
    private EventType eventType;

    /**
     * Создатель мероприятия
     */
    @ApiModelProperty(name = "authorId", value = "Создатель мероприятия", required = true, dataType = "User")
    @ManyToOne(optional = false, cascade = CascadeType.MERGE)
    @JoinColumn(name = "USER_ID")
    private User authorId;

    /**
     * С какими интересами связано мероприятие
     */
    @ApiModelProperty(name = "eventInterests", value = "С какими интересами связано мероприятие", required = true,
            dataType = "Interests")
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinTable(name = "INTERESTS_EVENT",
            joinColumns = @JoinColumn(name = "EVENT_ID", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "INTERESTS_ID"))
    private Set<Interests> eventInterests;

    /**
     * Статус мероприятия (модерация, доступно и т.д.)
     */
    @ApiModelProperty(name = "status", value = "Статус мероприятия (модерация, доступно и т.д.)", required = true,
            dataType = "Status")
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "STATUS_ID")
    private Status status;
}
