package com.Recommendusic;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class App {

    public static void main(String[] args) {
        // Caminho para o seu arquivo CSV dentro do projeto
        String csvFilePath = "data/spotify_songs.csv"; // <-- CONFIRME O NOME DO SEU ARQUIVO!

        try (
                Reader reader = new FileReader(csvFilePath);
                // Configura o parser para entender que a primeira linha é o cabeçalho (header)
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                        .withHeader()
                        .withIgnoreHeaderCase()
                        .withTrim())
        ) {
            System.out.println("Lendo o dataset. Processando as primeiras músicas...");
            int count = 0;

            // Itera sobre cada linha (registro) do CSV
            for (CSVRecord csvRecord : csvParser) {
                // Pega os dados de cada coluna pelo nome do cabeçalho
                String trackName = csvRecord.get("name");
                String artistName = csvRecord.get("artists");
                String albumName = csvRecord.get("album");
                double danceability = Double.parseDouble(csvRecord.get("danceability"));
                double energy = Double.parseDouble(csvRecord.get("energy"));
                int tempo = (int) Double.parseDouble(csvRecord.get("tempo"));

                System.out.println("\nTrack: " + trackName);
                System.out.println("Artist: " + artistName);
                System.out.println("Album: " + albumName);
                System.out.println("Danceability: " + danceability*100);
                System.out.println("Energy: " + energy*100);


                // --- AQUI COMEÇA A LÓGICA DO SEU GRAFO ---
                // Por exemplo, você pode criar um nó para cada música ou artista.
                // System.out.printf("Música: %s, Artista: %s, Dançabilidade: %.2f%n", trackName, artistName, danceability);

                // Para não processar 12 milhões de linhas durante os testes:
                count++;
                if (count >= 100) { // Processa apenas as 100 primeiras linhas para teste
                    break;
                }
            }
            System.out.println("Processamento de teste concluído.");

        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo CSV: " + e.getMessage());
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("Erro ao converter um número. Verifique o formato dos dados no CSV.");
            e.printStackTrace();
        }
    }
}