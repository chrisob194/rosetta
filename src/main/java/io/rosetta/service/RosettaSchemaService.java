package io.rosetta.service;

import io.rosetta.annotation.Glyph;
import io.rosetta.annotation.Inscription;
import io.rosetta.model.RosettaEntity;
import io.rosetta.model.RosettaField;
import io.rosetta.model.RosettaSchema;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EntityType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RosettaSchemaService {

    private final jakarta.persistence.EntityManagerFactory emf;

    public RosettaSchemaService(jakarta.persistence.EntityManagerFactory emf) {
        this.emf = emf;
    }

    public RosettaSchema getSchema() {
        List<RosettaEntity> entities = emf.getMetamodel().getEntities()
                .stream()
                .map(this::mapEntity)
                .toList();
        return new RosettaSchema(entities);
    }

    public Optional<RosettaEntity> getEntity(String name) {
        return emf.getMetamodel().getEntities()
                .stream()
                .filter(et -> et.getName().equals(name))
                .findFirst()
                .map(this::mapEntity);
    }

    private RosettaEntity mapEntity(EntityType<?> entityType) {
        Class<?> javaType = entityType.getJavaType();

        Table tableAnn = javaType.getAnnotation(Table.class);
        String tableName = (tableAnn != null && !tableAnn.name().isEmpty())
                ? tableAnn.name()
                : javaType.getSimpleName();

        Inscription inscriptionAnn = javaType.getAnnotation(Inscription.class);
        String inscription = inscriptionAnn != null ? inscriptionAnn.value() : null;

        List<RosettaField> fields = new ArrayList<>();
        for (Attribute<?, ?> attribute : entityType.getAttributes()) {
            fields.add(mapField(attribute, javaType));
        }

        return new RosettaEntity(entityType.getName(), tableName, inscription, fields);
    }

    private RosettaField mapField(Attribute<?, ?> attribute, Class<?> entityClass) {
        String fieldName = attribute.getName();
        String type = attribute.getJavaType().getSimpleName();

        Field javaField = findField(entityClass, fieldName);

        String columnName = fieldName;
        boolean nullable = true;
        boolean primaryKey = false;
        String glyph = null;
        String relation = null;

        if (javaField != null) {
            Column colAnn = javaField.getAnnotation(Column.class);
            if (colAnn != null) {
                if (!colAnn.name().isEmpty()) {
                    columnName = colAnn.name();
                }
                nullable = colAnn.nullable();
            }

            if (javaField.isAnnotationPresent(Id.class)) {
                primaryKey = true;
                nullable = false;
            }

            if (javaField.isAnnotationPresent(ManyToOne.class)) {
                relation = "ManyToOne";
            } else if (javaField.isAnnotationPresent(OneToMany.class)) {
                relation = "OneToMany";
            } else if (javaField.isAnnotationPresent(OneToOne.class)) {
                relation = "OneToOne";
            } else if (javaField.isAnnotationPresent(ManyToMany.class)) {
                relation = "ManyToMany";
            }

            Glyph glyphAnn = javaField.getAnnotation(Glyph.class);
            if (glyphAnn != null) {
                glyph = glyphAnn.value();
            }
        }

        return new RosettaField(fieldName, columnName, type, primaryKey, nullable, glyph, relation);
    }

    private Field findField(Class<?> clazz, String fieldName) {
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        return null;
    }
}
