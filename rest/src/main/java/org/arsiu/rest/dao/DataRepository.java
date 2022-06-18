package org.arsiu.rest.dao;

import org.arsiu.rest.models.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataRepository extends JpaRepository<Data,Integer> {
}
