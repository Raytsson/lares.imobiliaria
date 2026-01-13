package br.imob.fotoImoveis.service;

import br.imob.fotoImoveis.FotoImovel;
import br.imob.fotoImoveis.repository.FotoImovelRepository;
import br.imob.imovel.model.Imoveis;
import br.imob.imovel.repository.ImovelRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class FotoService {

    @Autowired
    private FotoImovelRepository fotoRepository;

    @Autowired
    private ImovelRepository imovelRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Transactional
    public void salvarFotos(Long imovelId, List<MultipartFile> arquivos) {
        Imoveis imovel = imovelRepository.findById(imovelId)
                .orElseThrow(() -> new RuntimeException("Imóvel não encontrado"));

        arquivos.forEach(arquivo -> {
            String nomeArquivo = salvarArquivoNoDisco(arquivo);

            FotoImovel foto = new FotoImovel();
            foto.setUrlArquivo(nomeArquivo);
            foto.setImovel(imovel);

            fotoRepository.save(foto);
        });
    }

    private String salvarArquivoNoDisco(MultipartFile arquivo) {
        try {
            String nomeUnico = UUID.randomUUID() + "_" + arquivo.getOriginalFilename();
            Path caminhoDestino = Paths.get(uploadDir).resolve(nomeUnico);
            Files.createDirectories(caminhoDestino.getParent());
            Files.copy(arquivo.getInputStream(), caminhoDestino, StandardCopyOption.REPLACE_EXISTING);
            return nomeUnico;
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar imagem", e);
        }
    }
}
