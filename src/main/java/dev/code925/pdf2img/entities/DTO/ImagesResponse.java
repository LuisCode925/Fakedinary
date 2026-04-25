package dev.code925.pdf2img.entities.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImagesResponse extends RepresentationModel<ImagesResponse> {

    String uuid;

    Integer size;
}
