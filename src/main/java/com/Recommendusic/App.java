package com.Recommendusic;

import com.github.yvasyliev.model.Track;
import com.github.yvasyliev.service.DeezerApi;

public class App {

    public static void main(String[] args) {
        // 1. Crie uma instância da API
        com.Recommendusic.DeezerApi deezerApi = new DeezerApi();

        String nomeMusica = "Poker Face";
        System.out.println("Buscando por '" + nomeMusica + "' na API da Deezer...");

        try {
            // 2. Use o serviço de busca para procurar a música
            // O 'findTracks' retorna uma página de resultados, então pegamos o primeiro.
            deezerApi.search().findTracks(nomeMusica)
                    .getData()
                    .stream()
                    .findFirst() // Pega o primeiro resultado da busca
                    .ifPresentOrElse(
                            // 3. Se encontrou a música, imprima os dados dela
                            track -> {
                                System.out.println("\n--- Música Encontrada ---");
                                System.out.println("ID: " + track.getId());
                                System.out.println("Título: " + track.getTitle());
                                System.out.println("Artista: " + track.getArtist().getName());
                                System.out.println("Álbum: " + track.getAlbum().getTitle());
                                System.out.println("Duração (segundos): " + track.getDuration());
                                System.out.println("BPM (Tempo): " + track.getBpm());
                                System.out.println("Link para prévia: " + track.getPreview());
                            },
                            // O que fazer se não encontrar a música
                            () -> System.out.println("Música não encontrada.")
                    );

        } catch (Exception e) {
            System.err.println("Ocorreu um erro ao chamar a API da Deezer: " + e.getMessage());
            e.printStackTrace();
        }
    }
}