package org.arsiu.rest.controllers;

import org.arsiu.rest.dto.TeleDto;
import org.arsiu.rest.exception.item.not.found.ItemNotFoundException;
import org.arsiu.rest.models.Tele;
import org.arsiu.rest.service.TeleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/tele")
public class TeleController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TeleController.class);

    @Autowired
    private TeleService teleService;

    @PostMapping
    public ResponseEntity<TeleDto> createTele(@Valid @RequestBody final Tele tele) {
        if(teleService.getTeleById(tele.getUid()) != null) {
            LOGGER.error("Can't add(createTele) an tele with existing id: " + tele.getUid());
            throw new ItemNotFoundException("Can't add(createTele) an tele with existing id: " + tele.getUid());
        }
        teleService.addTele(tele);
        LOGGER.info("Added new tele");
        return new ResponseEntity<TeleDto>(new TeleDto(tele), HttpStatus.OK);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<TeleDto> updateTele(
            @PathVariable("id") final int id,
            @Valid @RequestBody final Tele tele) {

        if (teleService.getTeleById(id) == null) {
            LOGGER.error("Can't put(updateTele) an tele with non-existing id: " + id);
            throw new ItemNotFoundException("Can't put(updateTele) an tele with non-existing id: " + id);
        }
        LOGGER.info("Successfully updated tele with id: " + id);
        tele.setUid(id);
        teleService.updateTele(tele);
        return new ResponseEntity<TeleDto>(new TeleDto(tele), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<TeleDto>> getTeles() {
        LOGGER.info("Gave away all Teles");
        List<Tele> teles = teleService.getTeles();
        List<TeleDto> clientsDto = new ArrayList<>();
        for (Tele tele : teles) {
            TeleDto teleDto = new TeleDto(tele);
            clientsDto.add(teleDto);
        }
        return new ResponseEntity<List<TeleDto>>(clientsDto, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<TeleDto> getTele(@PathVariable(name = "id") final Integer id) {
        if (teleService.getTeleById(id) == null) {
            LOGGER.error("Can't get(getTele) an Tele with non-existing id: " + id);
            throw new ItemNotFoundException("Can't get(getTele) an Tele with non-existing id: " + id);
        }
        LOGGER.info("Successfully get an Tele with id: " + id);
        Tele tele = teleService.getTeleById(id);
        return new ResponseEntity<TeleDto>(new TeleDto(tele), HttpStatus.OK);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Tele> deleteTeleById(@PathVariable("id") final Integer id) {
        if (teleService.getTeleById(id) == null) {
            LOGGER.error("Can't delete(deleteTeleById) an Tele with non-existing id: " + id);
            throw new ItemNotFoundException("Can't delete(deleteTeleById) an Tele with non-existing id: " + id);
        }
        LOGGER.info("Successfully deleted Tele with id: " + id);
        teleService.deleteTeleById(id);
        return ResponseEntity.noContent().build();
    }
}
