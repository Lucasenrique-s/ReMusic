package com.Recommendusic.Servico;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;

public class AchadorDeSonsPrecisos {

    public static void main(String[] args) {
        String csvFilePath = "data/tracks_features.csv"; // <-- CONFIRME O NOME DO SEU ARQUIVO!

        String targetTrack = "Cookie Jar"; // Vamos procurar por Poker Face de novo
        String targetArtist = "Doja Cat";

        boolean found = false;

        try (
                Reader reader = new FileReader(csvFilePath);
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                        .withHeader()
                        .withIgnoreHeaderCase()
                        .withTrim())
        ) {
            System.out.printf("Procurando por '%s' de '%s' com método preciso...%n", targetTrack, targetArtist);

            for (CSVRecord csvRecord : csvParser) {
                String trackName = csvRecord.get("name");
                String rawArtistString = csvRecord.get("artists"); // Ex: "['Lady Gaga', 'Bradley Cooper']"

                // --- PASSO DE LIMPEZA DOS DADOS ---
                String cleanedArtists = rawArtistString
                        .replace("[", "")
                        .replace("]", "")
                        .replace("'", ""); // Resultado: "Lady Gaga, Bradley Cooper"

                // Separa a string limpa em um array de artistas
                String[] artistsArray = cleanedArtists.split(",");

                // --- VERIFICAÇÃO PRECISA ---
                boolean artistMatch = false;
                for (String artist : artistsArray) {
                    // .trim() remove espaços em branco antes/depois do nome
                    if (artist.trim().equalsIgnoreCase(targetArtist)) {
                        artistMatch = true;
                        break; // Encontrou o artista, pode parar de procurar nesta linha
                    }
                }

                // Se o artista bateu E o nome da música contém o que procuramos
                if (artistMatch && trackName.toLowerCase().contains(targetTrack.toLowerCase())) {
                    System.out.println("\n--- MÚSICA ENCONTRADA! ---");
                    System.out.println("ID: " + csvRecord.get("id"));
                    System.out.println("Nome: " + csvRecord.get("name"));
                    System.out.println("Artistas (Original): " + csvRecord.get("artists"));
                    System.out.println("Artistas (Limpo): " + Arrays.toString(artistsArray));
                    System.out.println("Álbum: " + csvRecord.get("album"));
                    System.out.println("Ano: " + csvRecord.get("year"));
                    System.out.println("Dançabilidade: " + csvRecord.get("danceability"));

                    found = true;
                    break; // Para o loop principal
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!found) {
            System.out.println("\nA música não foi encontrada no dataset.");
            System.out.println("(Verifique se a música realmente está no seu arquivo de teste ou se o nome está correto)");
        }
    }
}
