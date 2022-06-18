package org.arsiu.rest.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name="tele", schema="kursach")
public class Tele extends GeneralModel {
    @Id
    @Column(name = "uid", unique=true)
    private Integer uid;

    public Tele(Integer uid) {
        this.uid = uid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tele tele = (Tele) o;
        return uid.equals(tele.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid);
    }
}
