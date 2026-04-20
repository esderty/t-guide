package t.lab.guide.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "security.cors")
@Getter @Setter
public class CorsProperties {
    private List<String> allowedOrigins;
}
