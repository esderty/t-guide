package t.lab.guide.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import t.lab.guide.dto.ExcursionFullDto;
import t.lab.guide.dto.ExcursionShortDto;
import t.lab.guide.service.ExcursionService;

import java.util.List;

@RestController
@RequestMapping("/api/excursions")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ExcursionController {
    private final ExcursionService excursionService;

    @GetMapping

    public ResponseEntity<List<ExcursionShortDto>> getAllExcursions(){
        List<ExcursionShortDto> excursions = excursionService.getAllExcursions();
        return ResponseEntity.ok(excursions);
    }


    @GetMapping("/{id}")

    public  ResponseEntity<ExcursionFullDto> getExcursionById(@PathVariable Long id)
    {
        return ResponseEntity.ok(excursionService.getExcursionById(id));
    }
}