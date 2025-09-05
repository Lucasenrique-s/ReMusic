package com.Recommendusic.Servico.Entidades;

import java.time.LocalDate;
import java.util.List;

public class Musica {

        private final String trackId;
        private final String trackName;
        private final String trackArtist;
        private final String playlistGenre;

        private final double danceability;
        private final double energy;
        private double loudness;
        private final double speechiness;
        private final double acousticness;
        private final double instrumentalness;
        private final double liveness;
        private final double valence;
        private double tempo;

        //Variáveis para normalização de Loudness e Tempo pra colocar eles na faixa de 0 e 1.
        private static final double MIN_TEMPO_FIXO = 50.0;
        private static final double MAX_TEMPO_FIXO = 250.0;
        private static final double MIN_LOUDNESS_FIXO = -60.0;
        private static final double MAX_LOUDNESS_FIXO = 0.0;

        public Musica(String trackId, String trackName, String trackArtist, String playlistGenre,
                      double danceability, double energy, double loudness, double speechiness,
                      double acousticness, double instrumentalness, double liveness,
                      double valence, double tempo) {
            this.trackId = trackId;
            this.trackName = trackName;
            this.trackArtist = trackArtist;
            this.playlistGenre = playlistGenre;
            this.danceability = danceability;
            this.energy = energy;
            this.loudness = loudness;
            this.speechiness = speechiness;
            this.acousticness = acousticness;
            this.instrumentalness = instrumentalness;
            this.liveness = liveness;
            this.valence = valence;
            this.tempo = tempo;
        }

    public static void normalize(List<Musica> musicas) {
        if (musicas == null || musicas.isEmpty()) {
            return;
        }
        double rangeTempo = MAX_TEMPO_FIXO - MIN_TEMPO_FIXO;
        double rangeLoudness = MAX_LOUDNESS_FIXO - MIN_LOUDNESS_FIXO;

        for (Musica musica : musicas) {
            // --- APLICA A FÓRMULA DE NORMALIZAÇÃO ---
            // Garante que o valor não saia da faixa 0-1, caso uma música tenha
            // um valor fora da nossa escala fixa (ex: BPM 40 ou 260)
            double tempoNormalizado = (musica.getTempo() - MIN_TEMPO_FIXO) / rangeTempo;
            tempoNormalizado = Math.max(0.0, Math.min(1.0, tempoNormalizado));

            double loudnessNormalizado = (musica.getLoudness() - MIN_LOUDNESS_FIXO) / rangeLoudness;
            loudnessNormalizado = Math.max(0.0, Math.min(1.0, loudnessNormalizado));

            musica.setTempo(tempoNormalizado);
            musica.setLoudness(loudnessNormalizado);
        }
    }

        public String getTrackId() {
            return trackId;
        }

        public String getTrackName() {
            return trackName;
        }

        public String getTrackArtist() {
            return trackArtist;
        }

        public String getPlaylistGenre() {
            return playlistGenre;
        }

        public double getDanceability() {
            return danceability;
        }

        public double getEnergy() {
            return energy;
        }

        public double getLoudness() {
            return loudness;
        }

        public double getSpeechiness() {
            return speechiness;
        }

        public double getAcousticness() {
            return acousticness;
        }

        public double getInstrumentalness() {
            return instrumentalness;
        }

        public double getLiveness() {
            return liveness;
        }

        public double getValence() {
            return valence;
        }

        public double getTempo() {
            return tempo;
        }

        public void setTempo(double valor){
            tempo = valor;
        }
        public void setLoudness(double valor){
            loudness = valor;
        }

        @Override
        public String toString() {
            return "Musica(nome='" + trackName + "', artista='" + trackArtist + "')";
        }
    }

