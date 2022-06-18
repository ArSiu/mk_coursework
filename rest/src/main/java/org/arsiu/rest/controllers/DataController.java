package org.arsiu.rest.controllers;

import org.arsiu.rest.dto.DataDto;
import org.arsiu.rest.exception.item.not.found.ItemNotFoundException;
import org.arsiu.rest.models.Data;
import org.arsiu.rest.service.DataService;
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
@RequestMapping(path = "/data")
public class DataController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataController.class);

    @Autowired
    private DataService dataService;
    @Autowired
    private TeleService teleService;

    @PostMapping
    public ResponseEntity<DataDto> createData( @RequestBody final Data data) {
        if (teleService.getTeleById(data.getUid()) == null) {
            LOGGER.error("Can't add(createData) an Data with non-existing uid of Tele id: " + data.getUid());
            throw new ItemNotFoundException("Can't add(createData) an Data with non-existing uid of Tele id: " + data.getUid());
        } else {
            LOGGER.info("Added new Data");
            return new ResponseEntity<DataDto>(new DataDto(dataService.addData(data)), HttpStatus.OK);
        }

    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<DataDto> updateData(@PathVariable("id") final int id, @Valid @RequestBody final Data data) {
        if (dataService.getDataById(id) == null ) {
            LOGGER.error("Can't put(updateData) an Data with non-existing id: " + id);
            throw new ItemNotFoundException("Can't put(updateData) an Data with non-existing id: " + id);
        }
        if (teleService.getTeleById(data.getUid()) != null) {
            LOGGER.error("Can't put(updateData) an Data with non-existing uid of Tele id: " + data.getUid());
            throw new ItemNotFoundException("Can't put(updateData) an Data with non-existing uid of Tele id: " + data.getUid());
        } else {
            LOGGER.info("Successfully updated Data with id: " + id);
            data.setId(id);
            return new ResponseEntity<DataDto>(new DataDto(dataService.updateData(data)), HttpStatus.OK);
        }
    }

    @GetMapping
    public ResponseEntity<List<DataDto>> getDatas() {
        LOGGER.info("Gave away all Datas");
        List<Data> data = dataService.getDatas();
        List<DataDto> dataDto = new ArrayList<>();
        for (Data dataP : data) {
            dataDto.add(new DataDto(dataP));
        }
        return new ResponseEntity<List<DataDto>>(dataDto, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<DataDto> getData(@PathVariable(name = "id") final Integer id) {
        if (dataService.getDataById(id) == null) {
            LOGGER.error("Can't get(getData) an Data with non-existing id: " + id);
            throw new ItemNotFoundException("Can't get(getData) an Data with non-existing id: " + id);
        }
        LOGGER.info("Successfully get an Datawith id: " + id);
        return new ResponseEntity<DataDto>(new DataDto(dataService.getDataById(id)), HttpStatus.OK);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Data> deleteDataById(@PathVariable("id") final Integer id) {
        if (dataService.getDataById(id) == null) {
            LOGGER.error("Can't delete(deleteDataById) an Data with non-existing id: " + id);
            throw new ItemNotFoundException("Can't delete(deleteDataById) an Data with non-existing id: " + id);
        }
        if (teleService.getTeleById(dataService.getDataById(id).getUid()) == null) {
            LOGGER.error("Can't put(updateData) an Data with non-existing uid of Tele id: " + dataService.getDataById(id).getUid());
            throw new ItemNotFoundException("Can't put(updateData) an Data with non-existing uid of Tele id: " + dataService.getDataById(id).getUid());
        } else {
            LOGGER.info("Successfully deleted Data with id: " + id);
            dataService.deleteDataById(id);
            return ResponseEntity.noContent().build();
        }

    }
}
