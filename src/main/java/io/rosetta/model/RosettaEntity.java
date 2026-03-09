package io.rosetta.model;

import java.util.List;

public class RosettaEntity {

    private String name;
    private String table;
    private String inscription;
    private List<RosettaField> fields;

    public RosettaEntity() {}

    public RosettaEntity(String name, String table, String inscription, List<RosettaField> fields) {
        this.name = name;
        this.table = table;
        this.inscription = inscription;
        this.fields = fields;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTable() { return table; }
    public void setTable(String table) { this.table = table; }

    public String getInscription() { return inscription; }
    public void setInscription(String inscription) { this.inscription = inscription; }

    public List<RosettaField> getFields() { return fields; }
    public void setFields(List<RosettaField> fields) { this.fields = fields; }
}
