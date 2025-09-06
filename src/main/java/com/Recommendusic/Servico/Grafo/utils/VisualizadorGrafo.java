package com.Recommendusic.Servico.Grafo.utils;

import com.Recommendusic.Servico.Entidades.Musica;
import com.Recommendusic.Servico.Grafo.Aresta;
import com.Recommendusic.Servico.Grafo.Grafo;
import com.Recommendusic.Servico.MusicaServico;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.*;

public class VisualizadorGrafo {
    public static void exibir(Grafo nossoGrafo, MusicaServico catalogo) {

        System.setProperty("org.graphstream.ui", "swing");
        SingleGraph graph = new SingleGraph("Grafo Musical");

        String stylesheet =
                "graph {" +
                        "   fill-color: white; " + // Fundo branco
                        "}" +
                        "node {" +
                        "   size: 20px; " +                                  // Nós (pontos) menores
                        "   fill-color: rgba(60, 130, 246, 180); " +       // Cor azul com 70% de transparência
                        "   stroke-mode: none; " +                         // Sem borda preta nos nós
                        "   text-mode: hidden; " +                         // Esconde os nomes para não poluir
                        "}" +
                        "node:hover {" +
                        "   fill-color: red; " +                           // Nó fica vermelho quando o mouse passa por cima
                        "   text-mode: normal; " +                         // Mostra o nome da música ao passar o mouse
                        "   text-background-mode: plain; " +
                        "   text-background-color: white; " +
                        "   text-padding: 3px; " +
                        "}" +
                        "edge {" +
                        "   size: 2px; " +                               // Arestas (linhas) bem finas
                        "   fill-color: rgba(200, 200, 200, 120); " +      // Cor cinza com 50% de transparência
                        "}";

        graph.setAttribute("ui.stylesheet", stylesheet);

        // Adiciona os nós
        for (String trackId : nossoGrafo.getAdjacencias().keySet()) {
            if (graph.getNode(trackId) == null) {
                // USA O CATÁLOGO PARA BUSCAR A MÚSICA PELO ID
                Optional<Musica> musicaOpt = catalogo.buscarMusicaPorId(trackId);
                if (musicaOpt.isPresent()) {
                    Musica musica = musicaOpt.get();
                    // Define o nome da música como o label do nó
                    graph.addNode(trackId).setAttribute("ui.label", musica.getTrackArtist());
                } else {
                    // Caso não encontre a música no catálogo (pouco provável)
                    graph.addNode(trackId).setAttribute("ui.label", trackId);
                }
            }
        }

        // Adiciona as arestas
        Set<String> arestasAdicionadas = new HashSet<>();
        for (Map.Entry<String, List<Aresta>> entry : nossoGrafo.getAdjacencias().entrySet()) {
            String sourceId = entry.getKey();
            for (Aresta aresta : entry.getValue()) {
                String targetId = aresta.vizinho().getTrackId();
                String edgeId = sourceId + "-" + targetId;
                String reverseEdgeId = targetId + "-" + sourceId;

                if (!arestasAdicionadas.contains(edgeId) && !arestasAdicionadas.contains(reverseEdgeId)) {
                    graph.addEdge(edgeId, sourceId, targetId);
                    arestasAdicionadas.add(edgeId);
                }
            }
        }

        // --- ESTRUTURA CORRETA PARA GARANTIR TODA A INTERATIVIDADE ---
        // 1. Cria o Viewer (visualizador)
        Viewer viewer = new SwingViewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);

        // 2. Ativa o layout automático
        viewer.enableAutoLayout();

        // 3. Pega o painel de visualização (a "tela" onde o grafo é desenhado)
        ViewPanel viewPanel = (ViewPanel) viewer.addDefaultView(false);

        // 4. Adiciona o listener de zoom que já tínhamos feito
        viewPanel.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double zoomFactor = e.getWheelRotation() < 0 ? 1.1 : 0.9;
                viewPanel.getCamera().setViewPercent(viewPanel.getCamera().getViewPercent() * zoomFactor);
            }
        });
        viewer = graph.display();
        viewer.enableAutoLayout();
    }
}

