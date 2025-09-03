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
        private final double loudness;
        private final double speechiness;
        private final double acousticness;
        private final double instrumentalness;
        private final double liveness;
        private final double valence;
        private final double tempo;

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

        @Override
        public String toString() {
            return "Musica(nome='" + trackName + "', artista='" + trackArtist + "')";
        }
    }

