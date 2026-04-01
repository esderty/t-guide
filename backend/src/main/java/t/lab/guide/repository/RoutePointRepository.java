package t.lab.guide.repository;

import t.lab.guide.entity.RoutePoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoutePointRepository extends JpaRepository<RoutePoint, Long> {

    List<RoutePoint> findByExcursionIdOrderByOrderNumberAsc(Long excursionId);
}