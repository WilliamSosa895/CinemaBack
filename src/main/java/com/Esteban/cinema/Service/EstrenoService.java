package com.Esteban.cinema.Service;

import com.Esteban.cinema.DTO.Response.EstrenoDto;
import com.Esteban.cinema.Model.Estreno;
import com.Esteban.cinema.Repository.EstrenoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class EstrenoService {

    @Autowired
    private EstrenoRepository estrenoRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Transactional(readOnly = true)
    public List<EstrenoDto> listarTodos() {
        return estrenoRepository.findAllByOrderByFechaEstrenoDesc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EstrenoDto> listarDestacados() {
        return estrenoRepository.findByFechaEstrenoGreaterThanOrderByFechaEstrenoAsc(LocalDate.now())
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public EstrenoDto buscarPorId(Long id) {
        Estreno estreno = estrenoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estreno no encontrado"));
        return toDto(estreno);
    }

    @Transactional
    public EstrenoDto crear(EstrenoDto dto, MultipartFile imagen) {
        validarFecha(dto.getFechaEstreno());

        Estreno estreno = new Estreno();
        estreno.setTitulo(dto.getTitulo());
        estreno.setFechaEstreno(dto.getFechaEstreno());
        estreno.setSinopsis(dto.getSinopsis());

        Estreno saved = estrenoRepository.save(estreno);

        if (imagen != null && !imagen.isEmpty()) {
            String imagenUrl = cloudinaryService.subirImagen(imagen, "estrenos/estreno_" + saved.getId());
            saved.setImagenUrl(imagenUrl);
            saved = estrenoRepository.save(saved);
        }

        return toDto(saved);
    }

    @Transactional
    public EstrenoDto editar(Long id, EstrenoDto dto, MultipartFile imagen) {
        validarFecha(dto.getFechaEstreno());

        Estreno estreno = estrenoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estreno no encontrado"));

        estreno.setTitulo(dto.getTitulo());
        estreno.setFechaEstreno(dto.getFechaEstreno());
        estreno.setSinopsis(dto.getSinopsis());

        if (imagen != null && !imagen.isEmpty()) {
            String imagenUrl = cloudinaryService.subirImagen(imagen, "estrenos/estreno_" + estreno.getId());
            estreno.setImagenUrl(imagenUrl);
        }

        return toDto(estrenoRepository.save(estreno));
    }

    @Transactional
    public void eliminar(Long id) {
        Estreno estreno = estrenoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estreno no encontrado"));
        estrenoRepository.delete(estreno);
    }

    private void validarFecha(LocalDate fechaEstreno) {
        if (fechaEstreno == null || !fechaEstreno.isAfter(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de estreno debe ser posterior a hoy");
        }
    }

    private EstrenoDto toDto(Estreno estreno) {
        return EstrenoDto.builder()
                .id(estreno.getId())
                .titulo(estreno.getTitulo())
                .fechaEstreno(estreno.getFechaEstreno())
                .sinopsis(estreno.getSinopsis())
                .imagenUrl(estreno.getImagenUrl())
                .build();
    }
}
