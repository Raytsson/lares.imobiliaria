package br.imob.imovel.service;

import br.imob.enderecos.dtos.EnderecosResponseDto;
import br.imob.enderecos.model.Enderecos;
import br.imob.imovel.dtos.ImovelDetailDto;
import br.imob.imovel.dtos.ImovelRequestDto;
import br.imob.imovel.dtos.ImovelResponseDto;
import br.imob.imovel.model.Imoveis;
import br.imob.imovel.repository.ImovelRepository;
import br.imob.imovel.repository.specs.ImovelSpecs;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ImovelService {

    @Autowired
    private ImovelRepository repository;

    @Transactional
    public ImovelResponseDto createImovel(ImovelRequestDto dto) {
        Enderecos enderecos = new Enderecos();
        enderecos.setCep(dto.enderecos().cep());
        enderecos.setLogradouro(dto.enderecos().logradouro());
        enderecos.setNumero(dto.enderecos().numero());
        enderecos.setBairro(dto.enderecos().bairro());
        enderecos.setCidade(dto.enderecos().cidade());
        enderecos.setEstado(dto.enderecos().estado());

        Imoveis imovel = getImoveis(dto, enderecos);
        repository.save(imovel);

        return new ImovelResponseDto(
                imovel.getId(),
                imovel.getTitulo(),
                imovel.getValor(),
                imovel.getEndereco().getCidade(),
                imovel.getEndereco().getLogradouro(),
                imovel.getEndereco().getBairro(),
                List.of());
    }

    public Page<ImovelResponseDto> buscar(Integer quartos, Integer vagas, String bairro, BigDecimal valorMin, BigDecimal valorMax , Pageable pageable) {
        Specification<Imoveis> spec = ImovelSpecs.comFiltros(quartos, vagas, bairro, valorMin, valorMax);

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
        return new ImovelResponseDto(
                imovel.getId(),
                imovel.getTitulo(),
                imovel.getValor(),
                imovel.getEndereco().getCidade(),
                imovel.getEndereco().getLogradouro(),
                imovel.getEndereco().getBairro(),
                imovel.getFotos().stream().map(f -> f.getUrlArquivo()).toList()
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

}
