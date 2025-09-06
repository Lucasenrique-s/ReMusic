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
        // Distância Euclidiana com 7 dimensões: danceability, energia, speechiness, acousticness, instrumentalness,
        // liveness e valence

        //A distância é calculada pela raiz quadrada da soma dos quadrados dos valores de cada um dos atributos.

        //Problema: Não sei como inserir tempo e loudness, Tempo é em BPM e Loudness em decibeis, não são emdidos em números de 0-1.

        final double PESO_ARTISTA = 0.4;
        final double PESO_ALBUM = 0.2;

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
                        Math.pow(m1.getTempo() - m2.getTempo(), 2) +
                        Math.pow(m1.getLoudness() - m2.getLoudness(), 2)
        );
        return dist;
    }

    public void construirGrafo(List<Musica> todasAsMusicas) {
        final double NOTA_DE_CORTE_MESMO_ARTISTA = 0.40; // Mais alta = mais fácil de conectar
        final double NOTA_DE_CORTE_MESMO_ALBUM  = 0.30; // Intermediária
        final double NOTA_DE_CORTE_PADRAO       = 0.20; // Mais baixa = mais rigorosa

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
                // Este valor é experimental, você pode ajustá-lo.
                double notaDeCorteAtual;

                // Prioridade máxima: mesmo artista
                if (musicaA.getTrackArtist().contains(musicaB.getTrackArtist())) {
                    notaDeCorteAtual = NOTA_DE_CORTE_MESMO_ARTISTA;
                }
                // Segunda prioridade: mesmo álbum (caso de coletâneas com artistas diferentes)
                else if (musicaA.getTrackAlbum().equals(musicaB.getTrackAlbum())) {
                    notaDeCorteAtual = NOTA_DE_CORTE_MESMO_ALBUM;
                }
                // Caso padrão para todas as outras músicas
                else {
                    notaDeCorteAtual = NOTA_DE_CORTE_PADRAO;
                }

                // --- 3. APLIQUE A NOTA DE CORTE ESCOLHIDA ---
                if (distancia < notaDeCorteAtual) {
                    grafo.adicionarAresta(musicaA, musicaB, distancia);
                }
            }
            // Imprime um progresso para saber que o programa não travou
            if (i % 1000 == 0) {
                System.out.println("Processando música " + i + " de " + todasAsMusicas.size());
            }
        }
        System.out.println("Construção do grafo concluída!");
    }

    public static int compararArtista(Musica m1, Musica m2){
        int ehMesmo = 1;
        if(m1.getTrackArtist().contains(m2.getTrackArtist())){
            ehMesmo = 0;
        }
        return ehMesmo;
    }

    public static int compararAlbum(Musica m1, Musica m2){
        int ehMesmo = 1;
        if(m1.getTrackAlbum().equals(m2.getTrackAlbum())){
            ehMesmo = 0;
        }
        return ehMesmo;
    }

    public Grafo getGrafo() {
        return grafo;
    }
}

