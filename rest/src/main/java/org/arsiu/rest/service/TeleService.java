package org.arsiu.rest.service;

import org.arsiu.rest.dao.TeleRepository;
import org.arsiu.rest.models.Tele;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.List;

@Service
@ApplicationScope
public class TeleService {

    @Autowired
    private TeleRepository teleRepository;

    public Tele addTele(final Tele tele) {
        return teleRepository.save(tele);
    }

    public Tele updateTele(final Tele tele) {
        return teleRepository.save(tele);
    }

    public List<Tele> getTeles() {
        return teleRepository.findAll();
    }

    public Tele getTeleById(final Integer id) {
        return teleRepository.findById(id).orElse(null);
    }

    public void deleteTeleById(final Integer id) {
        teleRepository.deleteById(id);
    }

}
