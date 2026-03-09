package io.rosetta.model;

import java.util.List;

public class RosettaSchema {

    private List<RosettaEntity> entities;

    public RosettaSchema() {}

    public RosettaSchema(List<RosettaEntity> entities) {
        this.entities = entities;
    }

    public List<RosettaEntity> getEntities() { return entities; }
    public void setEntities(List<RosettaEntity> entities) { this.entities = entities; }
}
