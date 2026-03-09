package io.rosetta.controller;

import io.rosetta.model.RosettaEntity;
import io.rosetta.model.RosettaSchema;
import io.rosetta.service.RosettaSchemaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${rosetta.base-path:/rosetta}")
public class RosettaController {

    private final RosettaSchemaService rosettaSchemaService;

    public RosettaController(RosettaSchemaService rosettaSchemaService) {
        this.rosettaSchemaService = rosettaSchemaService;
    }

    @GetMapping("/entities")
    public RosettaSchema getEntities() {
        return rosettaSchemaService.getSchema();
    }

    @GetMapping("/entities/{name}")
    public ResponseEntity<RosettaEntity> getEntity(@PathVariable String name) {
        return rosettaSchemaService.getEntity(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
