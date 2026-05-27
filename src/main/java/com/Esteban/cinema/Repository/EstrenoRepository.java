package com.Esteban.cinema.Repository;

import com.Esteban.cinema.Model.Estreno;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EstrenoRepository extends JpaRepository<Estreno, Long> {
    List<Estreno> findAllByOrderByFechaEstrenoDesc();
}
