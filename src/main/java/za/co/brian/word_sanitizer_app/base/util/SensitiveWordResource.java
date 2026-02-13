package za.co.brian.word_sanitizer_app.base.util;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.data.web.SortDefault;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import za.co.brian.word_sanitizer_app.base.model.SimpleValue;
import za.co.brian.word_sanitizer_app.base.security.UserRoles;
import za.co.brian.word_sanitizer_app.base.sensitive_word.SensitiveWordAssembler;
import za.co.brian.word_sanitizer_app.base.sensitive_word.SensitiveWordDTO;

import java.net.URI;

@RestController
@RequestMapping(
        value = "/api/internal/v1/sensitive-words",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@SecurityRequirement(name = "bearer-jwt")
@Tag(
        name = "Sensitive Words (internal)",
        description = "CRUD operations for managing sensitive words"
)
public class SensitiveWordResource {

    private final SensitiveWordService sensitiveWordService;
    private final SensitiveWordAssembler sensitiveWordAssembler;
    private final PagedResourcesAssembler<SensitiveWordDTO> pagedResourcesAssembler;


    public SensitiveWordResource(SensitiveWordService sensitiveWordService,
                                 final SensitiveWordAssembler sensitiveWordAssembler,
                                 final PagedResourcesAssembler<SensitiveWordDTO> pagedResourcesAssembler) {
        this.sensitiveWordService = sensitiveWordService;
        this.sensitiveWordAssembler = sensitiveWordAssembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }


    @Operation(
            summary = "Get paginated list of sensitive words",
            description = "Returns a paginated, optionally filtered list of sensitive words.",
            parameters = {
                    @Parameter(
                            name = "filter",
                            description = "Case-insensitive search on word",
                            in = ParameterIn.QUERY,
                            schema = @Schema(implementation = String.class)
                    ),
                    @Parameter(
                            name = "page",
                            in = ParameterIn.QUERY,
                            schema = @Schema(implementation = Integer.class)
                    ),
                    @Parameter(
                            name = "size",
                            in = ParameterIn.QUERY,
                            schema = @Schema(implementation = Integer.class)
                    ),
                    @Parameter(
                            name = "sort",
                            in = ParameterIn.QUERY,
                            schema = @Schema(implementation = String.class)
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sensitive words fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping
    @PreAuthorize("@devProfileSecurityConfig.isDev() ? true : hasAuthority('" + UserRoles.ADMIN + "')")
    public ResponseEntity<PagedModel<EntityModel<SensitiveWordDTO>>> getAllSensitiveWords(
            @RequestParam(name = "filter", required = false) final String filter,
            @Parameter(hidden = true)
            @SortDefault(sort = "id")
            @PageableDefault(size = 20) final Pageable pageable) {

        final Page<SensitiveWordDTO> sensitiveWordDTOs = sensitiveWordService.findAll(filter, pageable);
        return ResponseEntity.ok(
                pagedResourcesAssembler.toModel(sensitiveWordDTOs, sensitiveWordAssembler)
        );
    }

    @Operation(summary = "Get a sensitive word by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sensitive word fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Sensitive word not found")
    })
    @GetMapping("/{id}")
    @PreAuthorize("@devProfileSecurityConfig.isDev() ? true : hasAnyAuthority('" + UserRoles.ADMIN + "', '" + UserRoles.VIEWER + "')")
    public ResponseEntity<EntityModel<SensitiveWordDTO>> getSensitiveWord(
            @PathVariable(name = "id") final Long id) {

        final SensitiveWordDTO sensitiveWordDTO = sensitiveWordService.get(id);
        return ResponseEntity.ok(sensitiveWordAssembler.toModel(sensitiveWordDTO));
    }

    @Operation(summary = "Create a new sensitive word")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Sensitive word created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "409", description = "Word already exists")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("@devProfileSecurityConfig.isDev() ? true : hasAuthority('" + UserRoles.ADMIN + "')")
    public ResponseEntity<EntityModel<SimpleValue<Long>>> createSensitiveWord(
            @RequestBody @Valid final SensitiveWordDTO sensitiveWordDTO,
            UriComponentsBuilder uriBuilder) {

        final Long createdId = sensitiveWordService.create(sensitiveWordDTO);

        final URI location = uriBuilder
                .path("/api/internal/v1/sensitive-words/{id}")
                .buildAndExpand(createdId)
                .toUri();

        return ResponseEntity
                .created(location)
                .body(sensitiveWordAssembler.toSimpleModel(createdId));
    }

    @Operation(summary = "Update an existing sensitive word")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sensitive word updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Sensitive word not found")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("@devProfileSecurityConfig.isDev() ? true : hasAuthority('" + UserRoles.ADMIN + "')")
    public ResponseEntity<EntityModel<SimpleValue<Long>>> updateSensitiveWord(
            @PathVariable(name = "id") final Long id,
            @RequestBody @Valid final SensitiveWordDTO sensitiveWordDTO) {

        sensitiveWordService.update(id, sensitiveWordDTO);
        return ResponseEntity.ok(sensitiveWordAssembler.toSimpleModel(id));
    }

    @Operation(summary = "Delete a sensitive word by id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Sensitive word deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Sensitive word not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("@devProfileSecurityConfig.isDev() ? true : hasAuthority('" + UserRoles.ADMIN + "')")
    public ResponseEntity<Void> deleteSensitiveWord(@PathVariable(name = "id") final Long id) {
        sensitiveWordService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
