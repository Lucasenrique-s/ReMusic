package com.Recommendusic.Servico;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class AchadorDeSonsDefinitivo {

    public static void main(String[] args) {
        // O caminho para o seu novo arquivo
        String csvFilePath = "data/spotify_songs.csv"; // <-- ATUALIZE O NOME DO ARQUIVO!

        // O que estamos procurando
        String targetTrack = "Slow dancing in the dark";
        String targetArtist = "Joji";

        boolean found = false;

        try (
                Reader reader = new FileReader(csvFilePath);
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                        .withHeader()
                        .withIgnoreHeaderCase()
                        .withTrim())
        ) {
            System.out.printf("Procurando por '%s' de '%s' no novo dataset...%n", targetTrack, targetArtist);

            for (CSVRecord csvRecord : csvParser) {
                // Usando os novos nomes das colunas do arquivo
                String trackName = csvRecord.get("track_name");
                String artistName = csvRecord.get("track_artist");

                // A condição de busca fica MUITO mais simples agora!
                // Não precisamos mais limpar a string do artista.
                if (trackName.equalsIgnoreCase(targetTrack) &&
                        artistName.equalsIgnoreCase(targetArtist)) {

                    System.out.println("\n--- MÚSICA ENCONTRADA! ---");
                    System.out.println("Nome: " + csvRecord.get("track_name"));
                    System.out.println("Artista: " + csvRecord.get("track_artist"));
                    System.out.println("Álbum: " + csvRecord.get("track_album_name"));
                    System.out.println("Popularidade: " + csvRecord.get("track_popularity"));
                    System.out.println("Gênero da Playlist: " + csvRecord.get("playlist_genre"));
                    System.out.println("Subgênero: " + csvRecord.get("playlist_subgenre"));
                    System.out.println("Dançabilidade: " + csvRecord.get("danceability"));

                    found = true;
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!found) {
            System.out.println("\nA música não foi encontrada no dataset.");
        }
    }
}