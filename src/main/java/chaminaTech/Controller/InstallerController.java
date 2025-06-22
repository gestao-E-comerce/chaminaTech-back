package chaminaTech.Controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/api/installer")
@CrossOrigin(origins = "*")
public class InstallerController {

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadInstaller(@RequestParam("matrizId") Long matrizId) throws IOException {
        Path baseDir = Paths.get("").toAbsolutePath().resolve("installer");
        Path scriptTemplatePath = baseDir.resolve("installer-template.bat");
        Path scriptPath = baseDir.resolve("installer.bat");
        Path jarPath = baseDir.resolve("app.jar");
        Path jdk64Path = baseDir.resolve("jdk64.msi");
        Path zipPath = baseDir.resolve("installer.zip");

        // Verifica se os arquivos existem
        if (!Files.exists(scriptTemplatePath) || !Files.exists(jarPath) ||
                !Files.exists(jdk64Path)){
            System.err.println("Erro: Alguns arquivos necessários não foram encontrados!");
            throw new IOException("Verifique se installer-template.bat, app.jar, jdk64.msi estão presentes.");
        }

        // Substitui o placeholder no script
        String scriptContent = Files.readString(scriptTemplatePath);
        scriptContent = scriptContent.replace("${MATRIZ_ID}", matrizId.toString());
        Files.writeString(scriptPath, scriptContent);

        // Lista de arquivos a serem zipados
        List<Path> filesToZip = Arrays.asList(
                scriptPath, jarPath, jdk64Path
        );

        // Compactar todos os arquivos
        zipFiles(filesToZip, zipPath);

        // Verifica se o ZIP foi criado
        if (!Files.exists(zipPath)) {
            System.err.println("Erro: Arquivo ZIP não foi criado.");
            throw new IOException("Falha ao criar o arquivo ZIP.");
        }

        Resource resource = new UrlResource(zipPath.toUri());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"installer.zip\"")
                .body(resource);
    }

    private void zipFiles(List<Path> files, Path outputZip) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(outputZip))) {
            for (Path file : files) {
                if (Files.exists(file)) {
                    zos.putNextEntry(new ZipEntry(file.getFileName().toString()));
                    Files.copy(file, zos);
                    zos.closeEntry();
                } else {
                    throw new IOException("Arquivo não encontrado: " + file);
                }
            }
        }
    }
}