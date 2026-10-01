package br.com.fiap.clyvo.service;

import br.com.fiap.clyvo.dto.TutorAuthResponseDTO;
import br.com.fiap.clyvo.dto.TutorLoginRequestDTO;
import br.com.fiap.clyvo.dto.TutorRequestDTO;
import br.com.fiap.clyvo.dto.TutorResponseDTO;
import br.com.fiap.clyvo.exception.AcessoNegadoException;
import br.com.fiap.clyvo.exception.RecursoNaoEncontradoException;
import br.com.fiap.clyvo.model.Tutor;
import br.com.fiap.clyvo.repository.TutorRepository;
import br.com.fiap.clyvo.security.CustomUserDetails;
import br.com.fiap.clyvo.security.JwtService;
import br.com.fiap.clyvo.security.AuthUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.fiap.clyvo.model.enums.Perfil;

@Service
public class TutorService {

    private final TutorRepository repository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;     // ALTERADO - Sprint 4: bean unico
    private final AuthUser authUser; // NOVO - Sprint 4

    public TutorService(
            TutorRepository repository,
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            AuthUser authUser
    ) {
        this.repository = repository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authUser = authUser;
    }

    // NOVO - Sprint 4: leitura. Tutor so le a propria conta; veterinario pode
    // consultar tutores para o fluxo de atendimento.
    private void validarLeituraDoTutor(Long id) {
        if (authUser.isTutor()
                && !id.equals(authUser.getId())) {
            throw new AcessoNegadoException(
                    "Você só pode consultar os seus próprios dados.");
        }
    }

    // NOVO - Sprint 4: escrita. So o proprio tutor altera ou apaga a conta dele.
    // Nem outro tutor, nem veterinario.
    private void validarEscritaDoTutor(Long id) {
        if (!authUser.isTutor()
                || !id.equals(authUser.getId())) {
            throw new AcessoNegadoException(
                    "Você só pode alterar a sua própria conta.");
        }
    }

    // NOVO - Sprint 4: mapeamento unico
    private TutorResponseDTO toResponse(Tutor tutor) {
        return new TutorResponseDTO(
                tutor.getId(),
                tutor.getNome(),
                tutor.getEmail(),
                tutor.getTelefone()
        );
    }

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
        tutor.setSenha(passwordEncoder.encode(dto.senha()));

        tutor = repository.save(tutor);

        return toResponse(tutor);
    }

    public TutorAuthResponseDTO autenticar(TutorLoginRequestDTO dto) {

        Tutor tutor = repository.findByEmail(dto.email())
                .orElseThrow(() -> new RuntimeException("E-mail ou senha inválidos."));

        if (!passwordEncoder.matches(dto.senha(), tutor.getSenha())) {
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

    // ALTERADO - Sprint 4: ownership + a senha so e trocada se vier preenchida.
    // Antes, um PUT em /api/tutores/{id} de outra pessoa reescrevia a senha dela.
    @Transactional
    public TutorResponseDTO atualizar(Long id, TutorRequestDTO dto) {

        validarEscritaDoTutor(id);

        Tutor tutor = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Tutor não encontrado com o ID: " + id));

        tutor.setNome(dto.nome());
        tutor.setEmail(dto.email());
        tutor.setTelefone(dto.telefone());

        if (dto.senha() != null && !dto.senha().isBlank()) {
            tutor.setSenha(passwordEncoder.encode(dto.senha()));
        }

        tutor = repository.save(tutor);

        return toResponse(tutor);
    }

    // ALTERADO - Sprint 4: so a propria conta
    @Transactional
    public void excluir(Long id) {

        validarEscritaDoTutor(id);

        Tutor tutor = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Tutor não encontrado com o ID: " + id));

        repository.delete(tutor);
    }

    // Listagem geral: liberada apenas para VETERINARIO no SecurityConfig
    public Page<TutorResponseDTO> listar(Pageable paginacao) {
        return repository.findAll(paginacao).map(this::toResponse);
    }

    // ALTERADO - Sprint 4: ownership na leitura
    public TutorResponseDTO buscarPorId(Long id) {

        validarLeituraDoTutor(id);

        Tutor tutor = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Tutor não encontrado com o ID: " + id));

        return toResponse(tutor);
    }
}
