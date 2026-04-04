package t.lab.guide.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import t.lab.guide.dto.ExcursionFullDto;
import t.lab.guide.dto.ExcursionShortDto;
import t.lab.guide.entity.Excursion;
import t.lab.guide.mapper.ExcursionMapper;
import t.lab.guide.repository.ExcursionRepository;
import t.lab.guide.repository.RoutePointRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExcursionService {

    private final ExcursionRepository excursionRepository;
    private final RoutePointRepository routePointRepository;  // ← ИСПРАВЛЕНО!

    public List<ExcursionShortDto> getAllExcursions() {
        return excursionRepository.findAll().stream()
                .map(ExcursionMapper::toExcursionShortDto)
                .collect(Collectors.toList());
    }

    public ExcursionFullDto getExcursionById(Long id) {
        Excursion excursion = excursionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Экскурсия с id " + id + " не найдена"));

        excursion.setRoutePoints(
                routePointRepository.findByExcursionIdOrderByOrderNumberAsc(id)
        );

        return ExcursionMapper.toExcursionFullDto(excursion);
    }
}