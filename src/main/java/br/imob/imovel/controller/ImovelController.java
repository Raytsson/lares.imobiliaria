package br.imob.imovel.controller;

import br.imob.fotoImoveis.service.FotoService;
import br.imob.imovel.dtos.ImovelDetailDto;
import br.imob.imovel.dtos.ImovelRequestDto;
import br.imob.imovel.dtos.ImovelResponseDto;
import br.imob.imovel.dtos.ImovelTabelaDto;
import br.imob.imovel.enums.Cidades;
import br.imob.imovel.enums.Status;
import br.imob.imovel.enums.TipoImovel;
import br.imob.imovel.service.ImovelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/imoveis")
public class ImovelController {

    @Autowired
    private FotoService fotoService;
    @Autowired
    private ImovelService imovelService;

    @PostMapping(value = "/criarImovel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImovelResponseDto> criarImovel(
            @Valid @RequestPart("imovel") ImovelRequestDto imovelRequestDto,
            @RequestPart(value = "fotos", required = false) List<MultipartFile> fotos
    ) {
        ImovelResponseDto response = imovelService.createImovel(imovelRequestDto, fotos);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/fotos")
    public ResponseEntity<Void> uploadFotos(
            @PathVariable Long id,
            @RequestParam("arquivos") List<MultipartFile> arquivos) {
        fotoService.salvarFotos(id, arquivos);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<ImovelResponseDto>> buscar(
            @RequestParam(required = false) TipoImovel tipoImovel,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Cidades cidades,
            @RequestParam(required = false) Integer quartos,
            @RequestParam(required = false) Integer vagas,
            @RequestParam(required = false) String bairro,
            @RequestParam(required = false) BigDecimal valorMin,
            @RequestParam(required = false) BigDecimal valorMax,
            @RequestParam(required = false) Boolean isComercial,
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(
                imovelService.buscar(tipoImovel, status, cidades, quartos, vagas, bairro, valorMin, valorMax, isComercial, pageable)
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<ImovelResponseDto> buscarPorId(@PathVariable Long id) {
        ImovelResponseDto imovel = imovelService.buscarPorId(id);
        return ResponseEntity.ok(imovel);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> atualizarStatus(@PathVariable Long id, @RequestParam boolean ativo) {
        imovelService.alterarStatus(id, ativo);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        imovelService.excluir(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/tabela")
    public ResponseEntity<Page<ImovelTabelaDto>> listarParaTabela(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) TipoImovel tipoImovel,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Cidades cidade,
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(
                imovelService.listarParaTabela(
                        nome,
                        tipoImovel,
                        status,
                        cidade,
                        pageable
                )
        );
    }

    @GetMapping("/totais")
    public ResponseEntity<Map<String, Long>> totaisPorTipo() {
        Map<String, Long> totais = imovelService.contarPorTipo();
        return ResponseEntity.ok(totais);
    }


}
