package app.business.model;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class WeatherMonitoringDTO {

    private LocalDateTime time;
    private String cityName;
    private double temperature;
    private double windSpeed;

    public WeatherMonitoringDTO(String cityName, double temperature, double windSpeed) {
        this.time = LocalDateTime.now();
        this.cityName = cityName;
        this.temperature = temperature;
        this.windSpeed = windSpeed;
    }
}
