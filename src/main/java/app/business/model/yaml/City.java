package app.business.model.yaml;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class City {

    private int cityId;
    private String cityName;
    private int frequency;
    private int threshold;
}
