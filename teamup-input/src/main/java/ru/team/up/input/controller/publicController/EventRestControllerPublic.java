package ru.team.up.input.controller.publicController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.team.up.core.entity.Event;
import ru.team.up.core.entity.EventType;
import ru.team.up.input.exception.EventCheckException;
import ru.team.up.input.exception.EventCreateRequestException;
import ru.team.up.input.payload.request.EventRequest;
import ru.team.up.input.payload.request.JoinRequest;
import ru.team.up.input.payload.request.UserRequest;
import ru.team.up.input.service.EventServiceRest;
import ru.team.up.input.wordmatcher.WordMatcher;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * REST-контроллер для мероприятий
 *
 * @author Pavel Kondrashov
 */

@Tag(name = "Event Public Controller", description = "REST-контроллер для мероприятий")
@Slf4j
@RestController
@RequestMapping(value = "api/public/event")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class EventRestControllerPublic {
    private final EventServiceRest eventServiceRest;
    private final WordMatcher wordMatcher;


    /**
     * Метод получения списка всех мероприятий
     *
     * @return Список мероприятий и статус ответа
     */
    @Operation(summary = "Получение списка мероприятий", method = "GET", responses = {
            @ApiResponse(responseCode = "200", description = "ОК. Список мероприятий получен."),
            @ApiResponse(responseCode = "204", description = "NO CONTENT. Мероприятия не найдены."),
            @ApiResponse(responseCode = "404", description = "NOT FOUND. Страница не найдена.")
    })
    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        log.debug("Получен запрос на список мероприятий");
        List<Event> events = eventServiceRest.getAllEvents();

        if (events.isEmpty()) {
            log.error("Список мероприятий пуст");
            return new ResponseEntity("Список мероприятий пуст", HttpStatus.NO_CONTENT);
        }

        log.debug("Список мероприятий получен");
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    /**
     * Метод получения мероприятия по идентификатору
     *
     * @param eventId Идентификатор мероприятия
     * @return Ответ запроса и статус проверки
     */
    @Operation(summary = "Получение мероприятия по идентификатору", method = "GET", responses = {
            @ApiResponse(responseCode = "200", description = "ОК. Мероприятие найдено."),
            @ApiResponse(responseCode = "204", description = "NO CONTENT. Мероприятие не найдено."),
            @ApiResponse(responseCode = "404", description = "NOT FOUND")
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<Event> findEventById(@Parameter(name = "ID", example = "1", description = "ID мероприятия")
                                               @PathVariable("id") Long eventId) {
        log.debug("Получен запрос на поиск мероприятия по id: {}", eventId);
        Optional<Event> eventOptional = Optional.ofNullable(eventServiceRest.getEventById(eventId));

        return eventOptional
                .map(event -> {
                    log.debug("Мероприятие с id: {} найдено", eventId);
                    return new ResponseEntity<>(event, HttpStatus.OK);
                })
                .orElseGet(() -> {
                    log.error("Мероприятие с id: {} не найдено", eventId);
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                });
    }

    /**
     * Метод получения мероприятий по названию
     *
     * @param eventName Название мероприятия
     * @return Ответ запроса и статус проверки
     */
    @Operation(summary = "Получение мероприятий по названию", method = "GET", responses = {
            @ApiResponse(responseCode = "200", description = "ОК. Мероприятие найдено."),
            @ApiResponse(responseCode = "204", description = "NO CONTENT. Мероприятие не найдено."),
            @ApiResponse(responseCode = "404", description = "NOT FOUND")
    })
    @GetMapping(value = "/name/{eventName}")
    public ResponseEntity<List<Event>> findEventsByName(@Parameter(name = "eventName", example = "JOKER 2021", description = "Название мероприятия")
                                                        @PathVariable("eventName") String eventName) {
        log.debug("Получен запрос на поиск мероприятий по названию {}", eventName);
        List<Event> events = eventServiceRest.getEventByName(eventName);

        if (events.isEmpty()) {
            log.error("Мероприятия не найдены");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        log.debug("Мероприятие с названием {} найдено", eventName);
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    /**
     * Метод получения мероприятий по автору
     *
     * @param author Автор мероприятия
     * @return Ответ запроса и статус проверки
     */
    @Operation(summary = "Получение мероприятий по автору", method = "GET", responses = {
            @ApiResponse(responseCode = "200", description = "ОК. Мероприятие найдено."),
            @ApiResponse(responseCode = "204", description = "NO CONTENT. Мероприятие не найдено."),
            @ApiResponse(responseCode = "404", description = "NOT FOUND")
    })
    @GetMapping(value = "/author")
    public ResponseEntity<List<Event>> findEventsByAuthor(@Parameter(name = "author", description = "Сущность User, автор мероприятия")
                                                          @RequestBody UserRequest author) {
        log.debug("Получен запрос на поиск мероприятий по автору {}", author);
        List<Event> events = eventServiceRest.getAllEventsByAuthor(author.getUser());

        if (events.isEmpty()) {
            log.error("Мероприятия по указанному автору {} не найдены", author);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        log.debug("Мероприятия от автора {} найдены", author);
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    /**
     * Метод поиска мероприятия по типу
     *
     * @param eventType Тип мероприятия
     * @return Ответ запроса и статус проверки
     */
    @Operation(summary = "Получение мероприятия по типу", method = "GET", responses = {
            @ApiResponse(responseCode = "200", description = "ОК. Мероприятие найдено."),
            @ApiResponse(responseCode = "204", description = "NO CONTENT. Мероприятие не найдено."),
            @ApiResponse(responseCode = "404", description = "NOT FOUND")
    })
    @GetMapping(value = "/type")
    public ResponseEntity<List<Event>> findEventsByType(@Parameter(name = "eventType", description = "Сущность EventType, тип мероприятия")
                                                        @RequestBody EventType eventType) {
        log.debug("Получен запрос на поиск мероприятий по типу: {}", eventType);
        List<Event> events = eventServiceRest.getAllEventsByEventType(eventType);

        log.debug("Мероприятия с типом: {} найдены", eventType);
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    /**
     * Метод создания мероприятия
     *
     * @param event Данные мероприятия
     * @return Ответ запроса и статус проверки
     */
    @Operation(summary = "Создание мероприятия", method = "POST", responses = {
            @ApiResponse(responseCode = "201", description = "ОК. Мероприятие создано."),
            @ApiResponse(responseCode = "404", description = "NOT FOUND")
    })
    @PostMapping(value = "/")
    public ResponseEntity<Event> createEvent(@Parameter(name = "eventRequest", description = "Сущность EventRequest")
                                             @RequestBody EventRequest event) {
        log.debug("Получен запрос на создание мероприятия:\n {}", event);

        checkEvent(event);

        log.debug("Мероприятие создано");
        Event upcomingEvent = eventServiceRest.saveEvent(event.getEvent());

        return new ResponseEntity<>(upcomingEvent, HttpStatus.CREATED);
    }

    /**
     * Метод обновления мероприятия
     *
     * @param event   Данные мероприятия
     * @param eventId Идентификатор мероприятия
     * @return Ответ запроса и статус проверки
     */
    @Operation(summary = "Обновление мероприятия", method = "PUT", responses = {
            @ApiResponse(responseCode = "200", description = "ОК. Мероприятие обновлено."),
            @ApiResponse(responseCode = "204", description = "NO CONTENT. Мероприятие не найдено."),
            @ApiResponse(responseCode = "404", description = "NOT FOUND")
    })
    @PutMapping(value = "/{id}")
    public ResponseEntity<Event> updateEvent(@RequestBody EventRequest event,
                                             @Parameter(name = "ID", example = "1", description = "ID мероприятия")
                                             @PathVariable("id") Long eventId) {
        log.debug("Получен запрос на обновление мероприятия {}", event);

        checkEvent(event);

        log.debug("Мероприятие {} обновлено", event);
        Event newEvent = eventServiceRest.updateEvent(eventId, event.getEvent());

        return new ResponseEntity<>(newEvent, HttpStatus.OK);
    }

    /**
     * Метод удаления мероприятия по идентификатору
     *
     * @param eventId Идентификатор мероприятия
     * @return Ответ запроса и статус проверки
     */
    @Operation(summary = "Удаление мероприятия по идентификатору", method = "DELETE", responses = {
            @ApiResponse(responseCode = "200", description = "ОК. Мероприятие удалено."),
            @ApiResponse(responseCode = "204", description = "NO CONTENT. Мероприятие не найдено."),
            @ApiResponse(responseCode = "404", description = "NOT FOUND")
    })
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Event> deleteEvent(@Parameter(name = "ID", example = "1", description = "ID мероприятия")
                                             @PathVariable("id") Long eventId) {
        log.debug("Получен запрос на удаление мероприятия с id: {}", eventId);
        Event event = eventServiceRest.getEventById(eventId);

        if (event == null) {
            log.error("Мероприятие с id: {} не найдено", eventId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        eventServiceRest.deleteEvent(eventId);

        log.debug("Мероприятие с id: {} успешно удалено", eventId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Метод добавления участника мероприятия
     *
     * @param joinRequest Данные запроса для добавления участника
     * @return Ответ запроса и статус проверки
     */
    @Operation(summary = "Добавление участника мероприятия", method = "POST", responses = {
            @ApiResponse(responseCode = "200", description = "ОК. Участник добавлен."),
            @ApiResponse(responseCode = "204", description = "NO CONTENT. Мероприятие не найдено."),
            @ApiResponse(responseCode = "404", description = "NOT FOUND")
    })
    @PostMapping(value = "/join")
    public ResponseEntity<Event> addEventParticipant(@Parameter(name = "joinRequest", description = "Сущность JoinRequest")
                                                     @RequestBody JoinRequest joinRequest) {
        log.debug("Получен запрос на добавление участника мероприятия");
        Event event = eventServiceRest.addParticipant(joinRequest.getEventId(), joinRequest.getUserId());

        log.debug("Участник успешно добавлен");
        return new ResponseEntity<>(event, HttpStatus.OK);
    }

    /**
     * Метод удаления участника мероприятия
     *
     * @param joinRequest Данные запроса для удаления участника
     * @return Ответ запроса и статус проверки
     */
    @Operation(summary = "Удаление участника мероприятия", method = "PATCH", responses = {
            @ApiResponse(responseCode = "200", description = "ОК. Участник удален."),
            @ApiResponse(responseCode = "204", description = "NO CONTENT. Мероприятие не найдено."),
            @ApiResponse(responseCode = "404", description = "NOT FOUND")
    })
    @PatchMapping("/unjoin")
    public ResponseEntity<Event> deleteEventParticipant(@Parameter(name = "joinRequest", description = "Сущность JoinRequest")
                                                        @RequestBody JoinRequest joinRequest) {
        log.debug("Получен запрос на удаление участника мероприятия");
        Event event = eventServiceRest.deleteParticipant(joinRequest.getEventId(), joinRequest.getUserId());

        log.debug("Участник успешно удален");
        return new ResponseEntity<>(event, HttpStatus.OK);
    }

    /**
     * Метод проверки мероприятия
     *
     * @param event Данные мероприятия
     */
    private void checkEvent(EventRequest event) {
        if (wordMatcher.detectBadWords(event.getEvent().getEventName()) ||
                wordMatcher.detectBadWords(event.getEvent().getDescriptionEvent())) {
            log.error("Имя или описание мероприятия содержит запрещенные слова:\n {}", event);
            throw new EventCreateRequestException("Имя или описание мероприятия содержит запрещенные слова");
        }

        if (ChronoUnit.YEARS.between(event.getEvent().getTimeEvent(), LocalDateTime.now()) >= 1) {
            log.error("Дата создания мероприятия более 1 года:\n {}", event);
            throw new EventCreateRequestException("Дата создания мероприятия более 1 года");
        }

        if (wordMatcher.detectUnnecessaryWords(event.getEvent().getEventName()) ||
                wordMatcher.detectUnnecessaryWords(event.getEvent().getDescriptionEvent())) {
            log.debug("Мероприятие отправлено на проверку:\n {}", event);
            throw new EventCheckException("Мероприятие отправлено на проверку");
        }
    }
}
