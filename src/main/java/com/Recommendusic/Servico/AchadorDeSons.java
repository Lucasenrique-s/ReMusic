package com.Recommendusic.Servico;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class AchadorDeSons {

    public static void main(String[] args) {
        // Caminho para o seu arquivo CSV
        String csvFilePath = "data/tracks_features.csv"; // <-- CONFIRME O NOME DO SEU ARQUIVO!

        // O que estamos procurando
        String targetTrack = "Poker Face";
        String targetArtist = "Queen";

        boolean found = false;

        try (
                Reader reader = new FileReader(csvFilePath);
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                        .withHeader()
                        .withIgnoreHeaderCase()
                        .withTrim())
        ) {
            System.out.printf("Procurando por '%s' de '%s'...%n", targetTrack, targetArtist);

            System.out.println("Iniciando modo de investigação para o artista: " + targetArtist);
            int matches = 0;

            for (CSVRecord csvRecord : csvParser) {
                String trackName = csvRecord.get("name");
                String artistNames = csvRecord.get("artists");

                // --- MODO DE INVESTIGAÇÃO ---
                // Vamos checar APENAS o artista primeiro para ver o que aparece.
                if (artistNames.toLowerCase().contains(targetArtist.toLowerCase())) {

                    // Imprime exatamente como os dados estão no arquivo para este artista
                    System.out.printf("-> Artista encontrado! [Artistas no CSV: '%s'], [Música no CSV: '%s']%n", artistNames, trackName);
                    matches++;
                }
            }

            System.out.println("\nInvestigação concluída. Total de músicas encontradas para o artista: " + matches);

// Após o loop, podemos verificar se a busca original teria funcionado com algum dos resultados
            if (matches == 0) {
                found = false;
            } else {
                // Se você quiser, pode adicionar a lógica de busca aqui novamente,
                // mas o mais importante é o que foi impresso no console.
                // Por enquanto, vamos deixar a variável `found` como está para a mensagem final.
            }

        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo CSV: " + e.getMessage());
            e.printStackTrace();
        }

        if (!found) {
            System.out.println("\nA música não foi encontrada no dataset.");
        }
    }
}