package com.Recommendusic.Servico.Grafo.utils;

import com.Recommendusic.Servico.Entidades.*;
import com.Recommendusic.Servico.Grafo.Grafo;

import java.util.List;

public class ConstrutorGrafo {
    private Grafo grafo;

    public ConstrutorGrafo() {
        this.grafo = new Grafo();
    }

    public static double calcularDistancia(Musica m1, Musica m2) {
        // Distância Euclidiana com 7 dimensões: danceability, energy, speechiness, acousticness, instrumentalness,liveness, valence, tempo, loudness
        // A distância é calculada pela raiz quadrada da soma dos quadrados dos valores de cada um dos atributos.

        final double PESO_ARTISTA = 0.2;
        final double PESO_GENERO = 0.08;
        final double PESO_ALBUM = 0.1;

        double dist = Math.sqrt(
                Math.pow(m1.getDanceability() - m2.getDanceability(), 2) +
                        Math.pow(m1.getEnergy() - m2.getEnergy(), 2) +
                        Math.pow(m1.getSpeechiness() - m2.getSpeechiness(), 2) +
                        Math.pow(m1.getAcousticness() - m2.getAcousticness(), 2) +
                        Math.pow(m1.getInstrumentalness() - m2.getInstrumentalness(), 2) +
                        Math.pow(m1.getLiveness() - m2.getLiveness(), 2) +
                        Math.pow(m1.getValence() - m2.getValence(), 2) +
                        Math.pow(PESO_ARTISTA * compararArtista(m1,m2), 2) +
                        Math.pow(PESO_ALBUM * compararAlbum(m1,m2), 2) +
                        Math.pow(PESO_GENERO * compararGenero(m1,m2), 2) +
                        Math.pow(m1.getTempo() - m2.getTempo(), 2) +
                        Math.pow(m1.getLoudness() - m2.getLoudness(), 2)
        );
        return dist;
    }

    public void construirGrafo(List<Musica> todasAsMusicas) {
        final double NOTA_DE_CORTE_MESMO_ARTISTA = 0.40; // Mais alta = mais fácil de conectar
        final double NOTA_DE_CORTE_MESMO_ALBUM  = 0.30; // Intermediária
        final double NOTA_DE_CORTE_MESMO_GENERO = 0.25; // Quase mais baixa
        final double NOTA_DE_CORTE_PADRAO       = 0.05; // Mais baixa = mais rigorosa

        System.out.println("Iniciando a construção do grafo...");

        // Adiciona todas as músicas como nós no grafo
        for (Musica musica : todasAsMusicas) {
            grafo.adicionarNo(musica);
        }

        // Compara cada música com todas as outras para criar as arestas
        // Este é um processo pesado (O(n^2)), mas necessário.
        for (int i = 0; i < todasAsMusicas.size(); i++) {
            for (int j = i + 1; j < todasAsMusicas.size(); j++) {
                Musica musicaA = todasAsMusicas.get(i);
                Musica musicaB = todasAsMusicas.get(j);

                double distancia = calcularDistancia(musicaA, musicaB);

                // Define um "limite de similaridade" para não conectar
                // músicas muito diferentes e poluir o grafo.
                double notaDeCorteAtual;

                // Prioridade máxima: mesmo artista
                if (musicaA.getTrackArtist().contains(musicaB.getTrackArtist())) {
                    notaDeCorteAtual = NOTA_DE_CORTE_MESMO_ARTISTA;
                }
                // Segunda prioridade: mesmo álbum
                else if (musicaA.getTrackAlbum().equals(musicaB.getTrackAlbum())) {
                    notaDeCorteAtual = NOTA_DE_CORTE_MESMO_ALBUM;
                }
                // Terceira prioridade: mesmo genero
                else if (musicaA.getPlaylistGenre().equals(musicaB.getPlaylistGenre())) {
                    notaDeCorteAtual = NOTA_DE_CORTE_MESMO_GENERO;
                }
                // Caso padrão para todas as outras músicas
                else {
                    notaDeCorteAtual = NOTA_DE_CORTE_PADRAO;
                }

                if (distancia < notaDeCorteAtual) {
                    grafo.adicionarAresta(musicaA, musicaB, distancia);
                }
            }
            if (i % 1000 == 0) {
                System.out.println("Processando música " + i + " de " + todasAsMusicas.size());
            }
        }
        System.out.println("Construção do grafo concluída!");
    }

    public static int compararArtista(Musica m1, Musica m2){
        int ehMesmo = 1;
        if(m1.getTrackArtist().equalsIgnoreCase(m2.getTrackArtist())){
            ehMesmo = 0;
        }
        return ehMesmo;
    }

    public static int compararAlbum(Musica m1, Musica m2){
        int ehMesmo = 1;
        if(m1.getTrackAlbum().equalsIgnoreCase(m2.getTrackAlbum())){
            ehMesmo = 0;
        }
        return ehMesmo;
    }

    public static int compararGenero(Musica m1, Musica m2){
        int ehMesmo = 1;
        if(m1.getPlaylistGenre().equalsIgnoreCase(m2.getPlaylistGenre())){
            ehMesmo = 0;
        }
        return ehMesmo;
    }

    public Grafo getGrafo() {
        return grafo;
    }
}

