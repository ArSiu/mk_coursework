package org.arsiu.rest.service;

import org.arsiu.rest.dao.DataRepository;
import org.arsiu.rest.models.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.List;

@Service
@ApplicationScope
public class DataService {

    @Autowired
    private DataRepository dataRepository;

    public Data addData(final Data data) {
        return dataRepository.save(data);
    }

    public Data updateData(final Data data) {
        return dataRepository.save(data);
    }

    public List<Data> getDatas() {
        return dataRepository.findAll();
    }

    public Data getDataById(final Integer id) {
        return dataRepository.findById(id).orElse(null);
    }

    public void deleteDataById(final Integer id) {
        dataRepository.deleteById(id);
    }
}
