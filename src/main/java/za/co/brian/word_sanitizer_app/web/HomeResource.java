package za.co.brian.word_sanitizer_app.web;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import za.co.brian.word_sanitizer_app.base.util.SensitiveWordResource;


@RestController
public class HomeResource {

    @GetMapping("/")
    public RepresentationModel<?> index() {
        final RepresentationModel<?> representationModel = RepresentationModel.of(null);
        representationModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(SensitiveWordResource.class).getAllSensitiveWords(null, null)).withRel("sensitiveWords"));
        return representationModel;
    }

}
