package org.arsiu.rest.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name="data", schema="kursach")
public class Data extends GeneralModel{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Basic
    @Column(name = "uid")
    @NotNull(message = "Missing uid")
    private Integer uid;

    @Basic
    @Column(name = "x")
    @NotNull(message = "Missing x")
    private Integer x;

    @Basic
    @Column(name = "y")
    @NotNull(message = "Missing y")
    private Integer y;

    public Data(Integer id, @NotNull(message = "Missing uid") Integer uid, @NotNull(message = "Missing x") Integer x, @NotNull(message = "Missing y") Integer y) {
        this.id = id;
        this.uid = uid;
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Data data = (Data) o;
        return id.equals(data.id) &&
                uid.equals(data.uid) &&
                x.equals(data.x) &&
                y.equals(data.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uid, x, y);
    }
}
