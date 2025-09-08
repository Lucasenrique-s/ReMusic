package com.Recommendusic.Servico;

import com.Recommendusic.Servico.Entidades.Musica;
import com.Recommendusic.Servico.Grafo.Aresta;
import com.Recommendusic.Servico.Grafo.Grafo;

import java.util.*;
import java.util.stream.Collectors;

public class RecomendadorServico {
    public List<Musica> recomendar(Grafo grafo, Musica musicaInicial, MusicaServico catalogo, int quantidade) {
        // passa uma lista com uma única música pra procurar se for uma musica só
        return recomendarPorPlaylist(grafo, Collections.singletonList(musicaInicial), catalogo, quantidade);
    }

    /**
     * Recomenda músicas com base na similaridade com uma playlist inteira.
     * Usa o algoritmo de Dijkstra a partir de múltiplas fontes.
     */
    public List<Musica> recomendarPorPlaylist(Grafo grafo, List<Musica> playlist, MusicaServico catalogo, int quantidade) {
        if (playlist == null || playlist.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, Double> distancias = new HashMap<>();
        PriorityQueue<Map.Entry<String, Double>> filaDePrioridade =
                new PriorityQueue<>(Map.Entry.comparingByValue());

        // Inicialização: a distância para todos é "infinita".
        for (String trackId : grafo.getAdjacencias().keySet()) {
            distancias.put(trackId, Double.POSITIVE_INFINITY);
        }

        // Inicializa todas as músicas da playlist com distância 0 e as adiciona à fila.
        Set<String> playlistIds = new HashSet<>();
        for (Musica musicaDaPlaylist : playlist) {
            if (grafo.getAdjacencias().containsKey(musicaDaPlaylist.getTrackId())) {
                distancias.put(musicaDaPlaylist.getTrackId(), 0.0);
                filaDePrioridade.add(Map.entry(musicaDaPlaylist.getTrackId(), 0.0));
                playlistIds.add(musicaDaPlaylist.getTrackId());
            }
        }

        // O algoritmo de Dijkstra
        while (!filaDePrioridade.isEmpty()) {
            Map.Entry<String, Double> entradaAtual = filaDePrioridade.poll();
            String idAtual = entradaAtual.getKey();
            double distanciaAtual = entradaAtual.getValue();

            // Se já encontrámos um caminho mais curto, ignoramos este.
            if (distanciaAtual > distancias.get(idAtual)) {
                continue;
            }

            List<Aresta> vizinhos = grafo.getAdjacencias().get(idAtual);
            if (vizinhos == null) continue;

            for (Aresta aresta : vizinhos) {
                Musica vizinho = aresta.vizinho();
                String idVizinho = vizinho.getTrackId();
                double pesoAresta = aresta.peso();

                if (distanciaAtual + pesoAresta < distancias.get(idVizinho)) {
                    distancias.put(idVizinho, distanciaAtual + pesoAresta);
                    filaDePrioridade.add(Map.entry(idVizinho, distanciaAtual + pesoAresta));
                }
            }
        }

        // Agora, o mapa 'distancias' contém o caminho mais curto a partir de QUALQUER música da playlist.
        // Vamos ordenar e pegar as 'quantidade' melhores recomendações.
        return distancias.entrySet().stream()
                .filter(entry -> !playlistIds.contains(entry.getKey())) // Exclui as músicas que já estão na playlist
                .sorted(Map.Entry.comparingByValue()) // Ordena pela menor distância
                .limit(quantidade) // Pega as top N
                .map(entry -> catalogo.buscarMusicaPorId(entry.getKey()).orElse(null)) // Converte o ID de volta para um objeto Musica
                .filter(Objects::nonNull) // Remove qualquer música que não foi encontrada no catálogo
                .collect(Collectors.toList());
    }
}
