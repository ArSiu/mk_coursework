package org.arsiu.rest.dto;

import org.arsiu.rest.models.*;

public class DataDto {

    private Data data;

    public DataDto(Data data) {
        this.data = data;
    }

    public Integer getId() {
        return data.getId();
    }

    public Integer getUid() {
        return data.getUid();
    }

    public Integer getX() {
        return data.getX();
    }

    public Integer getY() {
        return data.getY();
    }
}
