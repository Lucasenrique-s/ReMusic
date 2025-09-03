package com.Recommendusic.Servico;
import com.Recommendusic.Servico.Entidades.Musica;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MusicaServico {

        private final List<Musica> musicas;

        public MusicaServico(String caminhoCsv) throws IOException {
            this.musicas = new ArrayList<>();
            carregarMusicas(caminhoCsv);
        }

        private void carregarMusicas(String caminhoCsv) throws IOException {
            System.out.println("Carregando o catálogo de músicas...");

            try (Reader reader = new FileReader(caminhoCsv);
                 CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withTrim())) {

                for (CSVRecord record : csvParser) {
                    try {
                        Musica musica = new Musica(
                                record.get("track_id"),
                                record.get("track_name"),
                                record.get("track_artist"),
                                record.get("playlist_genre"),
                                Double.parseDouble(record.get("danceability")),
                                Double.parseDouble(record.get("energy")),
                                Double.parseDouble(record.get("loudness")),
                                Double.parseDouble(record.get("speechiness")),
                                Double.parseDouble(record.get("acousticness")),
                                Double.parseDouble(record.get("instrumentalness")),
                                Double.parseDouble(record.get("liveness")),
                                Double.parseDouble(record.get("valence")),
                                Double.parseDouble(record.get("tempo"))
                        );
                        this.musicas.add(musica);
                    } catch (NumberFormatException e) {
                        System.err.println("Aviso: Pulando linha com dados inválidos: " + record.getRecordNumber());
                    }
                }
            }
            System.out.println("Catálogo carregado com sucesso! " + this.musicas.size() + " músicas na memória.");
        }


        public Optional<Musica> buscarMusicaPorNomeEArtista(String nomeMusica, String nomeArtista) {
            return this.musicas.stream()
                    .filter(m -> m.getTrackName().equalsIgnoreCase(nomeMusica) &&
                            m.getTrackArtist().equalsIgnoreCase(nomeArtista))
                    .findFirst();
        }

        public List<Musica> buscarPorArtista(String nomeArtista) {
            return this.musicas.stream()
                    .filter(m -> m.getTrackArtist().equalsIgnoreCase(nomeArtista))
                    .collect(Collectors.toList());
        }

        public List<Musica> buscarPorGenero(String genero) {
            return this.musicas.stream()
                    .filter(m -> m.getPlaylistGenre().equalsIgnoreCase(genero))
                    .collect(Collectors.toList());
        }

        public List<Musica> obterTodasAsMusicas() {
            return new ArrayList<>(this.musicas); // Retorna uma cópia para proteger a lista original
        }

        public List<Musica> obterMusicas(int limite) {
            return this.musicas.stream()
                    .limit(limite)
                    .collect(Collectors.toList());
        }
    }

