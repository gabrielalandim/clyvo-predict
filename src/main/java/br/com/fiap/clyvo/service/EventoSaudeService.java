package br.com.fiap.clyvo.service;

import br.com.fiap.clyvo.dto.EventoSaudeRequestDTO;
import br.com.fiap.clyvo.exception.RecursoNaoEncontradoException;
import br.com.fiap.clyvo.dto.EventoSaudeResponseDTO;
import br.com.fiap.clyvo.model.EventoSaude;
import br.com.fiap.clyvo.model.Pet;
import br.com.fiap.clyvo.repository.EventoSaudeRepository;
import br.com.fiap.clyvo.repository.PetRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheEvict;

@Service
public class EventoSaudeService {

    private final EventoSaudeRepository repository;
    private final PetRepository petRepository;
    private final PetService petService;

    public EventoSaudeService(
            EventoSaudeRepository repository,
            PetRepository petRepository,
            PetService petService
    ) {
        this.repository = repository;
        this.petRepository = petRepository;
        this.petService = petService;
    }


    private EventoSaudeResponseDTO toResponse(EventoSaude evento, Pet pet) {
        return new EventoSaudeResponseDTO(
                evento.getId(),
                pet.getId(),
                evento.getTipoEvento(),
                evento.getDescricao(),
                evento.getDataEvento(),
                pet.getHealthScore()
        );
    }


    @CacheEvict(value = "listaDePets", allEntries = true)
    @Transactional
    public EventoSaudeResponseDTO cadastrarEvento(EventoSaudeRequestDTO dto) {

        Pet pet = petService.buscarPetAutorizado(dto.petId());

        EventoSaude evento = new EventoSaude();
        evento.setPet(pet);
        evento.setTipoEvento(dto.tipoEvento());
        evento.setDescricao(dto.descricao());
        evento.setDataEvento(dto.dataEvento());

        int novoScore = dto.tipoEvento().calcularNovoScore(pet.getHealthScore());

        pet.setHealthScore(novoScore);
        petRepository.save(pet);

        evento = repository.save(evento);

        return toResponse(evento, pet);
    }

    @Transactional(readOnly = true)
    public Page<EventoSaudeResponseDTO> buscarEventosPorPet(Long petId, Pageable paginacao) {

        Pet pet = petService.buscarPetAutorizado(petId);

        Page<EventoSaude> eventos =
                repository.findByPetIdOrderByDataEventoDesc(pet.getId(), paginacao);

        return eventos.map(evento -> toResponse(evento, pet));
    }

    private EventoSaude buscarEventoAutorizado(Long id) {
        EventoSaude evento = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Evento não encontrado com o ID: " + id));
        petService.buscarPetAutorizado(evento.getPet().getId());
        return evento;
    }

    @Transactional(readOnly = true)
    public EventoSaudeResponseDTO buscarPorId(Long id) {
        EventoSaude evento = buscarEventoAutorizado(id);
        return toResponse(evento, evento.getPet());
    }

    private void recalcularScore(Pet pet) {
        int score = 100;
        for (EventoSaude evento : repository.findByPetIdOrderByIdAsc(pet.getId())) {
            score = evento.getTipoEvento().calcularNovoScore(score);
        }
        pet.setHealthScore(score);
        petRepository.save(pet);
    }

    @CacheEvict(value = "listaDePets", allEntries = true)
    @Transactional
    public EventoSaudeResponseDTO atualizar(Long id, EventoSaudeRequestDTO dto) {
        EventoSaude evento = buscarEventoAutorizado(id);
        Pet pet = evento.getPet();
        if (!pet.getId().equals(dto.petId())) {
            throw new IllegalArgumentException("Não é permitido alterar o pet de um evento.");
        }
        evento.setTipoEvento(dto.tipoEvento());
        evento.setDescricao(dto.descricao());
        evento.setDataEvento(dto.dataEvento());
        repository.saveAndFlush(evento);
        recalcularScore(pet);
        return toResponse(evento, pet);
    }

    @CacheEvict(value = "listaDePets", allEntries = true)
    @Transactional
    public void excluir(Long id) {
        EventoSaude evento = buscarEventoAutorizado(id);
        Pet pet = evento.getPet();
        repository.delete(evento);
        repository.flush();
        recalcularScore(pet);
    }
}
