package app.business.service;

import app.business.model.yaml.City;
import app.business.model.yaml.Weather;
import app.business.enums.JsonKey;
import app.business.model.WeatherMonitoringDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class MonitoringService implements CommandLineRunner {


    private final ThreadPoolTaskScheduler  threadPoolTaskScheduler;
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final Weather weather;

    public MonitoringService(ThreadPoolTaskScheduler threadPoolTaskScheduler,
                             RestTemplate restTemplate,
                             ObjectMapper mapper,
                             Weather weather) {
        this.threadPoolTaskScheduler = threadPoolTaskScheduler;
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.weather = weather;
    }


    @Override
    public void run(String... args) throws Exception {
        List<City> cityList = weather.getMonitoring();
        for(City city : cityList){
            String cityName = city.getCityName();
            AtomicInteger currentTemperature = new AtomicInteger(-999);
            threadPoolTaskScheduler.scheduleWithFixedDelay(() -> {
                log.info("{} WeatherMonitoring start", cityName);
                ResponseEntity<String> response = restTemplate.exchange(
                        String.format("https://api.openweathermap.org/data/2.5/weather?q=%s&appid=e735b6b632e6c008be941b8dbdb346d4&units=metric", cityName),
                        HttpMethod.GET,
                        null,
                        String.class);
                log.info("{} WeatherMonitoring response is {}", cityName, response);
                JsonNode jsonNode = null;
                try {
                    jsonNode = mapper.readTree(response.getBody());
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                int newTemperature = jsonNode.get(JsonKey.MAIN.getKey()).get(JsonKey.TEMP.getKey()).asInt();
                int wind = jsonNode.get(JsonKey.WIND.getKey()).get(JsonKey.SPEED.getKey()).asInt();
                log.info("{} WeatherMonitoring newTemperature is {}", cityName, newTemperature);
                log.info("{} WeatherMonitoring RESULT : {}", cityName, new WeatherMonitoringDTO(cityName, newTemperature, wind));

                int currentTemp = currentTemperature.get();
                double deviation = Math.abs(((double) (currentTemp - newTemperature)/currentTemp)*100);
                String deviationFormat = String.format("%.2f", deviation);
                log.info("{} TemperatureDeviation deviation is {}", cityName, deviationFormat);
                if(deviation > city.getThreshold()){
                    log.info("warning {} TemperatureDeviation: the new currentTemperature is critical: difference is more {} percent",cityName,  deviationFormat);
                }else {
                    log.info("{} TemperatureDeviation the new currentTemperature is not critical", cityName);
                }
                currentTemperature.set(newTemperature);
                log.info("{} WeatherMonitoring finish", cityName);
            }, city.getFrequency());
        }
    }
}
