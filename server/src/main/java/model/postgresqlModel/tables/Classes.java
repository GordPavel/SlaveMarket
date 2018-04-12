/*
 * Copyright (c) 2018.
 * You may use, distribute and modify this code
 * under the terms of the TOT (take on trust) public license which unfortunately won't be
 * written for another century. So you can modify and copy this files.
 *
 */

package model.postgresqlModel.tables;

import com.vladmihalcea.hibernate.type.array.StringArrayType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@TypeDef(
        name = "string-array",
        typeClass = StringArrayType.class
)
@Table(name = "classes")
public class Classes {
    @Id
    @Column(name = "classname")
    private String classname;
    @Column(name = "fields",
            columnDefinition = "VARCHAR[]")
    @Type(type = "string-array")
    private String[] fields;
    @Type(type = "string-array")
    private String[] types;

    public String getClassname() {
        return classname;
    }


    public String[] getFields() {
        return fields;
    }

    public String[] getTypes() {
        return types;
    }
}
