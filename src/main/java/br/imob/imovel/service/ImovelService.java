package br.imob.imovel.service;

import br.imob.enderecos.dtos.EnderecosResponseDto;
import br.imob.enderecos.model.Enderecos;
import br.imob.fotoImoveis.FotoImovel;
import br.imob.fotoImoveis.repository.FotoImovelRepository;
import br.imob.fotoImoveis.service.FotoService;
import br.imob.imovel.dtos.*;
import br.imob.imovel.enums.Cidades;
import br.imob.imovel.enums.Status;
import br.imob.imovel.enums.TipoImovel;
import br.imob.imovel.model.Imoveis;
import br.imob.imovel.repository.ImovelRepository;
import br.imob.imovel.repository.specs.ImovelSpecs;
import br.imob.imovel.specification.ImovelSpecification;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ImovelService {

    @Autowired
    private ImovelRepository repository;

    @Autowired
    private FotoService fotoService;
    @Autowired
    private FotoImovelRepository fotoImovelRepository;
    @Value("${app.upload.dir}")
    private String uploadDir;

    @Transactional
    public ImovelResponseDto createImovel(ImovelRequestDto dto, List<MultipartFile> fotos) {
        Enderecos enderecos = new Enderecos();
        enderecos.setCep(dto.enderecos().cep());
        enderecos.setLogradouro(dto.enderecos().logradouro());
        enderecos.setNumero(dto.enderecos().numero());
        enderecos.setBairro(dto.enderecos().bairro());
        enderecos.setCidade(dto.enderecos().cidade());
        enderecos.setEstado(dto.enderecos().estado());

        Imoveis imovel = getImoveis(dto, enderecos);
        repository.save(imovel); // aqui o id já é gerado

        if (fotos != null && !fotos.isEmpty()) {
            fotoService.salvarFotos(imovel.getId(), fotos);
        }

        return toResponseDto(imovel);
    }

    public Page<ImovelResponseDto> buscar(Cidades cidades, Integer quartos, Integer vagas, String bairro, BigDecimal valorMin, BigDecimal valorMax , Pageable pageable) {
        Specification<Imoveis> spec = ImovelSpecs.comFiltros(cidades, quartos, vagas, bairro, valorMin, valorMax);

        return repository.findAll(spec, pageable).map(this::toResponseDto);
    }

    private Imoveis getImoveis(ImovelRequestDto dto, Enderecos enderecos) {
        Imoveis imovel = new Imoveis();
        imovel.setTitulo(dto.titulo());
        imovel.setDescricao(dto.descricao());
        imovel.setTipoImovel(dto.tipoImovel());
        imovel.setStatus(dto.status());
        imovel.setValor(dto.valor());
        imovel.setAreaTotal(dto.areaTotal());
        imovel.setAreaConstruida(dto.areaConstruida());
        imovel.setQuartos(dto.quartos());
        imovel.setBanheiros(dto.banheiros());
        imovel.setVagasGaragem(dto.vagasGaragem());
        imovel.setEndereco(enderecos);
        return imovel;
    }

    private ImovelResponseDto toResponseDto(Imoveis imovel) {
        EnderecoDto enderecoDto = null;
        if (imovel.getEndereco() != null) {
            enderecoDto = new EnderecoDto(
                    imovel.getEndereco().getLogradouro(),
                    imovel.getEndereco().getNumero(),
                    imovel.getEndereco().getBairro(),
                    imovel.getEndereco().getCep()
            );
        }

        List<String> listaFotos = new ArrayList<>();
        if (imovel.getFotos() != null) {
            listaFotos = imovel.getFotos().stream()
                    .map(FotoImovel::getUrlArquivo)
                    .collect(Collectors.toList());
        }

        return new ImovelResponseDto(
                imovel.getId(),
                imovel.getTitulo(),
                imovel.getDescricao(),
                (imovel.getCidade() != null) ? imovel.getCidade().name() : null,
                imovel.getTipoImovel(),
                imovel.getStatus(),
                imovel.getValor(),
                imovel.getAreaTotal(),
                imovel.getAreaConstruida(),
                imovel.getQuartos(),
                imovel.getBanheiros(),
                imovel.getVagasGaragem(),
                enderecoDto,
                listaFotos
        );
    }

    public ImovelDetailDto buscarPorId(Long id) {
        Imoveis imovel = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Imóvel não encontrado"));
        return toDetailDto(imovel);
    }

    private ImovelDetailDto toDetailDto(Imoveis imovel) {
        String baseUrl = "http://localhost:8080/uploads/";

        List<String> fotos = imovel.getFotos().stream()
                .map(f -> baseUrl + f.getUrlArquivo())
                .toList();

        return new ImovelDetailDto(
                imovel.getId(), imovel.getTitulo(), imovel.getDescricao(),
                imovel.getValor(), imovel.getTipoImovel(), imovel.getStatus(),
                imovel.getAreaTotal(), imovel.getAreaConstruida(),
                imovel.getQuartos(), imovel.getBanheiros(), imovel.getVagasGaragem(),
                toEnderecoDto(imovel.getEndereco()),
                fotos
        );
    }

    private EnderecosResponseDto toEnderecoDto(Enderecos endereco) {
        if (endereco == null) return null;

        return new EnderecosResponseDto(
                endereco.getCep(),
                endereco.getLogradouro(),
                endereco.getNumero(),
                endereco.getBairro(),
                endereco.getCidade(),
                endereco.getEstado()
        );
    }

    @Transactional
    public void alterarStatus(Long id, boolean ativo) {
        var imovel = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Imóvel não encontrado"));

        imovel.setMovelActive(ativo);
        repository.save(imovel);
    }

    @Transactional
    public void excluir(Long id) {

        Imoveis imovel = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Imóvel não encontrado"));

        // 🔥 Busca as fotos direto do banco
        List<FotoImovel> fotos = fotoImovelRepository.findByImovelId(id);

        // 🔥 Exclui o imóvel (e registros de foto via cascade/orphan)
        repository.delete(imovel);

        // 🔥 Registra ação para depois do COMMIT
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

                    @Override
                    public void afterCommit() {
                        log.info("✅ Commit confirmado, iniciando exclusão física");
                        excluirFotosFisicas(fotos);
                    }

                }
        );
    }

    private void excluirFotosFisicas(List<FotoImovel> fotos) {
        log.info("🧹 Excluindo {} arquivos físicos", fotos.size());

        for (FotoImovel foto : fotos) {
            Path path = Paths.get(uploadDir, foto.getUrlArquivo());
            log.info("Apagando arquivo: {}", path.toAbsolutePath());

            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                log.error("Erro ao deletar arquivo {}", path, e);
            }
        }
    }

    public Page<ImovelTabelaDto> listarParaTabela(
            String nome,
            TipoImovel tipoImovel,
            Status status,
            Cidades cidade,
            Pageable pageable
    ) {
        Specification<Imoveis> spec = ImovelSpecification.tabela(
                nome, tipoImovel, status, cidade
        );

        return repository.findAll(spec, pageable)
                .map(ImovelTabelaDto::new);
    }

    public Map<String, Long> contarPorTipo() {
        Map<String, Long> totais = new HashMap<>();
        totais.put("CASA", repository.countByTipoImovel(TipoImovel.CASA));
        totais.put("APARTAMENTO", repository.countByTipoImovel(TipoImovel.APARTAMENTO));
        totais.put("TERRENO", repository.countByTipoImovel(TipoImovel.TERRENO));
        totais.put("CHACARA", repository.countByTipoImovel(TipoImovel.CHACARA));
        totais.put("BARRACAO", repository.countByTipoImovel(TipoImovel.BARRACAO));
        totais.put("SITIO", repository.countByTipoImovel(TipoImovel.SITIO));
        return totais;
    }





}
