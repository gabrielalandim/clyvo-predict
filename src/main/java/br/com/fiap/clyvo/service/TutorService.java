package br.com.fiap.clyvo.service;

import br.com.fiap.clyvo.dto.TutorAuthResponseDTO;
import br.com.fiap.clyvo.dto.TutorLoginRequestDTO;
import br.com.fiap.clyvo.dto.TutorRequestDTO;
import br.com.fiap.clyvo.dto.TutorResponseDTO;
import br.com.fiap.clyvo.model.Tutor;
import br.com.fiap.clyvo.repository.TutorRepository;
import br.com.fiap.clyvo.security.CustomUserDetails;
import br.com.fiap.clyvo.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.fiap.clyvo.model.enums.Perfil;

@Service
public class TutorService {

    @Autowired
    private TutorRepository repository;

    @Autowired
    private JwtService jwtService;

    @Transactional
    public TutorResponseDTO cadastrar(TutorRequestDTO dto) {

        if (repository.findByEmail(dto.email()).isPresent()) {
            throw new RuntimeException("E-mail já cadastrado no sistema!");
        }

        Tutor tutor = new Tutor();
        tutor.setNome(dto.nome());
        tutor.setEmail(dto.email());
        tutor.setTelefone(dto.telefone());
        tutor.setPerfil(Perfil.TUTOR);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        tutor.setSenha(encoder.encode(dto.senha()));

        tutor = repository.save(tutor);

        return new TutorResponseDTO(tutor.getId(), tutor.getNome(), tutor.getEmail(), tutor.getTelefone());
    }

    public TutorAuthResponseDTO autenticar(TutorLoginRequestDTO dto) {

        Tutor tutor = repository.findByEmail(dto.email())
                .orElseThrow(() -> new RuntimeException("E-mail ou senha inválidos."));

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if (!encoder.matches(dto.senha(), tutor.getSenha())) {
            throw new RuntimeException("E-mail ou senha inválidos.");
        }

        CustomUserDetails userDetails = new CustomUserDetails(
                tutor.getId(),
                tutor.getNome(),
                tutor.getEmail(),
                tutor.getSenha(),
                "TUTOR"
        );

        String token = jwtService.gerarToken(userDetails);

        return new TutorAuthResponseDTO(
                tutor.getId(),
                tutor.getNome(),
                tutor.getEmail(),
                "TUTOR",
                token
        );
    }

    @Transactional
    public TutorResponseDTO atualizar(Long id, TutorRequestDTO dto) {
        Tutor tutor = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tutor não encontrado com o ID: " + id));

        tutor.setNome(dto.nome());
        tutor.setEmail(dto.email());
        tutor.setTelefone(dto.telefone());

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        tutor.setSenha(encoder.encode(dto.senha()));

        tutor = repository.save(tutor);

        return new TutorResponseDTO(tutor.getId(), tutor.getNome(), tutor.getEmail(), tutor.getTelefone());
    }

    @Transactional
    public void excluir(Long id) {
        Tutor tutor = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tutor não encontrado com o ID: " + id));

        repository.delete(tutor);
    }
    public Page<TutorResponseDTO> listar(Pageable paginacao) {
        return repository.findAll(paginacao)
                .map(tutor -> new TutorResponseDTO(
                        tutor.getId(),
                        tutor.getNome(),
                        tutor.getEmail(),
                        tutor.getTelefone()
                ));
    }
    public TutorResponseDTO buscarPorId(Long id) {
        Tutor tutor = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Tutor não encontrado com o ID: " + id));

        return new TutorResponseDTO(
                tutor.getId(),
                tutor.getNome(),
                tutor.getEmail(),
                tutor.getTelefone()
        );
    }
}