package br.com.fiap.clyvo.service;

import br.com.fiap.clyvo.dto.VeterinarioAuthResponseDTO;
import br.com.fiap.clyvo.dto.VeterinarioLoginRequestDTO;
import br.com.fiap.clyvo.dto.VeterinarioRequestDTO;
import br.com.fiap.clyvo.dto.VeterinarioResponseDTO;
import br.com.fiap.clyvo.model.Veterinario;
import br.com.fiap.clyvo.repository.VeterinarioRepository;
import br.com.fiap.clyvo.security.CustomUserDetails;
import br.com.fiap.clyvo.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VeterinarioService {

    @Autowired
    private VeterinarioRepository repository;
    @Autowired
    private JwtService jwtService;

    @Transactional
    public VeterinarioResponseDTO cadastrar(VeterinarioRequestDTO dto) {

        if (repository.findByEmail(dto.email()).isPresent()) {
            throw new RuntimeException("E-mail já cadastrado no sistema!");
        }

        if (repository.findByCrmv(dto.crmv()).isPresent()) {
            throw new RuntimeException("CRMV já cadastrado no sistema!");
        }

        Veterinario veterinario = new Veterinario();

        veterinario.setNome(dto.nome());
        veterinario.setEmail(dto.email());
        veterinario.setCrmv(dto.crmv());

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        veterinario.setSenha(encoder.encode(dto.senha()));

        veterinario = repository.save(veterinario);

        return new VeterinarioResponseDTO(
                veterinario.getId(),
                veterinario.getNome(),
                veterinario.getEmail(),
                veterinario.getCrmv()
        );
    }

    public VeterinarioAuthResponseDTO autenticar(VeterinarioLoginRequestDTO dto) {

        Veterinario veterinario = repository.findByEmail(dto.email())
                .orElseThrow(() -> new RuntimeException("E-mail ou senha inválidos."));

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if (!encoder.matches(dto.senha(), veterinario.getSenha())) {
            throw new RuntimeException("E-mail ou senha inválidos.");
        }

        CustomUserDetails userDetails = new CustomUserDetails(
                veterinario.getId(),
                veterinario.getNome(),
                veterinario.getEmail(),
                veterinario.getSenha(),
                "VETERINARIO"
        );

        String token = jwtService.gerarToken(userDetails);

        return new VeterinarioAuthResponseDTO(
                veterinario.getId(),
                veterinario.getNome(),
                veterinario.getEmail(),
                veterinario.getCrmv(),
                "VETERINARIO",
                token
        );
    }

    public Page<VeterinarioResponseDTO> listar(Pageable paginacao) {

        return repository.findAll(paginacao)
                .map(veterinario -> new VeterinarioResponseDTO(
                        veterinario.getId(),
                        veterinario.getNome(),
                        veterinario.getEmail(),
                        veterinario.getCrmv()
                ));
    }

    public VeterinarioResponseDTO buscarPorId(Long id) {

        Veterinario veterinario = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Veterinário não encontrado com o ID: " + id));

        return new VeterinarioResponseDTO(
                veterinario.getId(),
                veterinario.getNome(),
                veterinario.getEmail(),
                veterinario.getCrmv()
        );
    }

    @Transactional
    public VeterinarioResponseDTO atualizar(Long id, VeterinarioRequestDTO dto) {

        Veterinario veterinario = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Veterinário não encontrado com o ID: " + id));

        veterinario.setNome(dto.nome());
        veterinario.setEmail(dto.email());
        veterinario.setCrmv(dto.crmv());

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        veterinario.setSenha(encoder.encode(dto.senha()));

        veterinario = repository.save(veterinario);

        return new VeterinarioResponseDTO(
                veterinario.getId(),
                veterinario.getNome(),
                veterinario.getEmail(),
                veterinario.getCrmv()
        );
    }

    @Transactional
    public void excluir(Long id) {

        Veterinario veterinario = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Veterinário não encontrado com o ID: " + id));

        repository.delete(veterinario);
    }
}