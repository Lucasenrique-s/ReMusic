package com.Recommendusic.Servico.Grafo;

import com.Recommendusic.Servico.Entidades.*;
import java.util.List;

public class ConstrutorGrafo {
    private Grafo grafo;

    public ConstrutorGrafo() {
        this.grafo = new Grafo();
    }

    private double calcularDistancia(Musica m1, Musica m2) {
        // Distância Euclidiana com 7 dimensões: danceability, energia, speechiness, acousticness, instrumentalness,
        // liveness e valence

        //A distância é calculada pela raiz quadrada da soma dos quadrados dos valores de cada um dos atributos.

        //Problema: Não sei como inserir tempo e loudness, Tempo é em BPM e Loudness em decibeis, não são emdidos em números de 0-1.

        double dist = Math.sqrt(
                Math.pow(m1.getDanceability() - m2.getDanceability(), 2) +
                        Math.pow(m1.getEnergy() - m2.getEnergy(), 2) +
                        Math.pow(m1.getSpeechiness() - m2.getSpeechiness(), 2) +
                        Math.pow(m1.getAcousticness() - m2.getAcousticness(), 2) +
                        Math.pow(m1.getInstrumentalness() - m2.getInstrumentalness(), 2) +
                        Math.pow(m1.getLiveness() - m2.getLiveness(), 2) +
                        Math.pow(m1.getValence() - m2.getValence(), 2)
        );
        return dist;
    }

    public void construirGrafo(List<Musica> todasAsMusicas) {
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
                // Este valor (0.25) é experimental, você pode ajustá-lo.
                if (distancia < 0.25) {
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

    public Grafo getGrafo() {
        return grafo;
    }
}

