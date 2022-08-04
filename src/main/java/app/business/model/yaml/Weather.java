package app.business.model.yaml;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("weather")
public class Weather {

    private List<City> monitoring;
}
