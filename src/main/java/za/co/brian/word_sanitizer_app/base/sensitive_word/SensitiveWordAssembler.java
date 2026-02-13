package za.co.brian.word_sanitizer_app.base.sensitive_word;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;
import za.co.brian.word_sanitizer_app.base.model.SimpleValue;
import za.co.brian.word_sanitizer_app.base.util.SensitiveWordResource;


@Component
public class SensitiveWordAssembler implements SimpleRepresentationModelAssembler<SensitiveWordDTO> {

    @Override
    public void addLinks(final EntityModel<SensitiveWordDTO> entityModel) {
        entityModel.add(linkTo(methodOn(SensitiveWordResource.class).getSensitiveWord(entityModel.getContent().getId())).withSelfRel());
        entityModel.add(linkTo(methodOn(SensitiveWordResource.class).getAllSensitiveWords(null, null)).withRel(IanaLinkRelations.COLLECTION));
    }

    @Override
    public void addLinks(final CollectionModel<EntityModel<SensitiveWordDTO>> collectionModel) {
        collectionModel.add(linkTo(methodOn(SensitiveWordResource.class).getAllSensitiveWords(null, null)).withSelfRel());
    }

    public EntityModel<SimpleValue<Long>> toSimpleModel(final Long id) {
        final EntityModel<SimpleValue<Long>> simpleModel = SimpleValue.entityModelOf(id);
        simpleModel.add(linkTo(methodOn(SensitiveWordResource.class).getSensitiveWord(id)).withSelfRel());
        return simpleModel;
    }

}
