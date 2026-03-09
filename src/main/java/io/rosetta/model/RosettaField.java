package io.rosetta.model;

public class RosettaField {

    private String name;
    private String column;
    private String type;
    private boolean primaryKey;
    private boolean nullable;
    private String glyph;
    private String relation;

    public RosettaField() {}

    public RosettaField(String name, String column, String type, boolean primaryKey,
                        boolean nullable, String glyph, String relation) {
        this.name = name;
        this.column = column;
        this.type = type;
        this.primaryKey = primaryKey;
        this.nullable = nullable;
        this.glyph = glyph;
        this.relation = relation;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getColumn() { return column; }
    public void setColumn(String column) { this.column = column; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isPrimaryKey() { return primaryKey; }
    public void setPrimaryKey(boolean primaryKey) { this.primaryKey = primaryKey; }

    public boolean isNullable() { return nullable; }
    public void setNullable(boolean nullable) { this.nullable = nullable; }

    public String getGlyph() { return glyph; }
    public void setGlyph(String glyph) { this.glyph = glyph; }

    public String getRelation() { return relation; }
    public void setRelation(String relation) { this.relation = relation; }
}
