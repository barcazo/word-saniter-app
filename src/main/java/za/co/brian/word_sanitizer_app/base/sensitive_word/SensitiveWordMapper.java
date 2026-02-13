package za.co.brian.word_sanitizer_app.base.sensitive_word;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;


@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface SensitiveWordMapper {

    SensitiveWordDTO updateSensitiveWordDTO(SensitiveWord sensitiveWord,
            @MappingTarget SensitiveWordDTO sensitiveWordDTO);

    @Mapping(target = "id", ignore = true)
    SensitiveWord updateSensitiveWord(SensitiveWordDTO sensitiveWordDTO,
            @MappingTarget SensitiveWord sensitiveWord);

}
