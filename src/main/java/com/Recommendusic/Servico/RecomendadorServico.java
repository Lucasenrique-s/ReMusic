package com.Recommendusic.Servico;

import com.Recommendusic.Servico.Entidades.*;
import com.Recommendusic.Servico.Grafo.Aresta;
import com.Recommendusic.Servico.Grafo.Grafo;

import java.util.*;
import java.util.stream.Collectors;

public class RecomendadorServico {
    public List<Musica> recomendar(Grafo grafo, Musica musicaInicial, MusicaServico catalogo, int quantidade) {
        // Mapa para guardar a menor distância encontrada do início até cada música
        Map<String, Double> distancias = new HashMap<>();

        // Fila de prioridade para decidir qual nó visitar em seguida (o com menor distância)
        PriorityQueue<Musica> filaDePrioridade = new PriorityQueue<>(Comparator.comparingDouble(m -> distancias.get(m.getTrackId())));

        // Inicialização: a distância para todos é "infinita", exceto para o ponto de partida que é 0.
        for (String trackId : grafo.getAdjacencias().keySet()) {
            distancias.put(trackId, Double.POSITIVE_INFINITY);
        }
        distancias.put(musicaInicial.getTrackId(), 0.0);
        filaDePrioridade.add(musicaInicial);

        // --- Início do Algoritmo de Dijkstra ---
        while (!filaDePrioridade.isEmpty()) {
            Musica atual = filaDePrioridade.poll();
            String idAtual = atual.getTrackId();
            double distanciaAtual = distancias.get(idAtual);

            List<Aresta> vizinhos = grafo.getAdjacencias().get(idAtual);
            if (vizinhos == null) continue;

            for (Aresta aresta : vizinhos) {
                Musica vizinho = aresta.vizinho();
                String idVizinho = vizinho.getTrackId();
                double pesoAresta = aresta.peso();

                // Se encontrarmos um caminho mais curto para o vizinho...
                if (distanciaAtual + pesoAresta < distancias.get(idVizinho)) {
                    // ...atualizamos sua distância e o adicionamos à fila para ser explorado.
                    distancias.put(idVizinho, distanciaAtual + pesoAresta);
                    filaDePrioridade.add(vizinho);
                }
            }
        }
        // --- Fim do Algoritmo ---
/*
        // Agora, o mapa 'distancias' contém o caminho mais curto para todas as músicas.
        // Vamos ordenar e pegar as 'quantidade' melhores recomendações.
        return distancias.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(musicaInicial.getTrackId())) // Exclui a própria música
                .sorted(Map.Entry.comparingByValue()) // Ordena pela menor distância
                .limit(quantidade) // Pega as top N
                .map(entry -> catalogo.buscarMusicaPorId(entry.getKey()).orElse(null)) // Converte o ID de volta para um objeto Musica
                .filter(Objects::nonNull) // Remove qualquer música que não foi encontrada no catálogo
                .collect(Collectors.toList());
                */
    }
}
