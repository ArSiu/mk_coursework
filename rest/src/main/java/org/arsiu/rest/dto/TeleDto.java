package org.arsiu.rest.dto;

import org.arsiu.rest.models.Tele;

public class TeleDto {
    private Tele tele;

    public TeleDto(Tele tele) {
        this.tele = tele;
    }

    public Integer getUid() {
        return tele.getUid();
    }

}
