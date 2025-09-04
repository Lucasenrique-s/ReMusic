package com.Recommendusic;

import com.Recommendusic.Servico.Entidades.*;
import com.Recommendusic.Servico.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class mainteste {
    public static void main(String[] args) {
        try {
            //O CSV será lido aqui.
            MusicaServico catalogo = new MusicaServico("data/spotify_songs.csv");

            //Buscar uma música específica
            System.out.println("\n--- Buscando uma música específica ---");
            Optional<Musica> musicaProcurada = catalogo.buscarMusicaPorNomeEArtista("Africa", "TOTO");

            // Verificando se o Optional contém um valor
            if (musicaProcurada.isPresent()) {
                Musica africa = musicaProcurada.get();
                System.out.println("Música encontrada: " + africa);
                System.out.println("   - Danceability: " + africa.getDanceability());
                System.out.println("   - Gênero: " + africa.getPlaylistGenre());
            } else {
                System.out.println("Música 'Africa' do TOTO não encontrada.");
            }

            //Buscando todas as músicas de um artista
            System.out.println("\n--- Buscando músicas por artista ---");
            List<Musica> musicasDoQueen = catalogo.buscarPorArtista("Queen");
            if (!musicasDoQueen.isEmpty()) {
                System.out.println("Encontradas " + musicasDoQueen.size() + " músicas do Queen. As 5 primeiras são:");
                musicasDoQueen.stream().limit(5).forEach(musica -> {
                    System.out.println(" - " + musica.getTrackName());
                });
            }

            // Fazer o próximo passo
            // Para pegar as primeiras 1000 musicas para o grafo:
            // List<Musica> musicasParaOGrafo = catalogo.obterMusicas(1000);
            // System.out.println("\nCarregadas " + musicasParaOGrafo.size() + " músicas para construir o grafo.");

        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }
}