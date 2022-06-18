package org.arsiu.rest.dao;

import org.arsiu.rest.models.Tele;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface TeleRepository extends JpaRepository<Tele,Integer> {
}