package autumn.browmanagement.service;

import autumn.browmanagement.DTO.EventDTO;
import autumn.browmanagement.Entity.Event;
import autumn.browmanagement.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    // 이벤트 조회

    public List<EventDTO> eventList(){
        List<Event> events = eventRepository.findAllByOrderByEventIdDesc();
        List<EventDTO> eventDTOS = new ArrayList<>();

        for (Event event : events){
            EventDTO eventDTO = new EventDTO();
            eventDTO.setEventId(event.getEventId());
            eventDTO.setTitle(event.getTitle());
            eventDTO.setContent(event.getContent());
            eventDTO.setEventUrl(event.getEventUrl());
            eventDTO.setImportant(event.getImportant());
            eventDTO.setEventDate(event.getEventDate());

            eventDTOS.add(eventDTO);
        }
        return eventDTOS;
    }
}
