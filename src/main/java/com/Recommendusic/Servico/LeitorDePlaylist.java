package com.Recommendusic.Servico;

import com.Recommendusic.Servico.Entidades.Musica;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LeitorDePlaylist {

    //Lê um arquivo de playlist e retorna uma lista de Músicas encontradas no catálogo.
    public static List<Musica> carregarDeArquivo(String caminhoArquivo, MusicaServico catalogo) throws IOException {
        List<Musica> playlist = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            System.out.println("--- Lendo o arquivo da playlist ---");
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty() || linha.startsWith("#")) {
                    continue;
                }

                String[] partes = linha.split(",");
                if (partes.length >= 2) {
                    String nomeMusica = partes[0].trim();
                    String nomeArtista = partes[1].trim();
                    Optional<Musica> musicaOpt = catalogo.buscarMusicaPorNomeEArtista(nomeMusica, nomeArtista);

                    if (musicaOpt.isPresent()) {
                        playlist.add(musicaOpt.get());
                        System.out.println("  [OK] Encontrada: '" + nomeMusica + "' - " + nomeArtista);
                    } else {
                        System.out.println("  [AVISO] Não encontrada: '" + nomeMusica + "' - " + nomeArtista);
                    }
                } else {
                    System.out.println("  [AVISO] Linha mal formatada, ignorando: '" + linha + "'");
                }
            }
        }
        return playlist;
    }
}
