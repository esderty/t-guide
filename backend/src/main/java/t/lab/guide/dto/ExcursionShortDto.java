package t.lab.guide.dto;

public record ExcursionShortDto(
        Long id,
        String name,
        Integer duration,
        Double distance,
        String imageUrl
) {
}
