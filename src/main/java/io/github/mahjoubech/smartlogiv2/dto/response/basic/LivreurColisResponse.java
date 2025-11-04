package io.github.mahjoubech.smartlogiv2.dto.response.basic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LivreurColisResponse {
    private String nomComplet;
    private long colisCont ;
}
