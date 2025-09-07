package com.Recommendusic.Servico.Grafo.utils;

import com.Recommendusic.Servico.Entidades.Musica;
import com.Recommendusic.Servico.Grafo.Aresta;
import com.Recommendusic.Servico.Grafo.Grafo;
import com.Recommendusic.Servico.MusicaServico;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.layout.springbox.implementations.SpringBox; // Importa a classe específica
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.view.Viewer;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.*;

public class VisualizadorGrafo {
    public static void exibir(Grafo nossoGrafo, MusicaServico catalogo) {

        System.setProperty("org.graphstream.ui", "swing");
        SingleGraph graph = new SingleGraph("Grafo Musical");

        // Stylesheet e adição de nós/arestas (continua igual, já estava correto)
        String stylesheet =
                "graph {" +
                        "   fill-color: #f0f0f0; padding: 40px; " +
                        "}" +
                        "node {" +
                        "   size: 20px; stroke-mode: plain; stroke-color: #444444; stroke-width: 1px; " +
                        // 1. O texto agora é visível por padrão
                        "   text-mode: normal; " +
                        // 2. Ajustes para melhorar a leitura do texto
                        "   text-size: 10px; " +
                        "   text-color: #222222; " +
                        "   text-background-mode: plain; " +
                        "   text-background-color: rgba(255,255,255,180); " +
                        "   text-padding: 1px; " +
                        "   text-offset: 0px, -20px; " +
                        "}" +
                        "edge {" +
                        "   size: 1px; fill-color: rgba(150, 150, 150, 150); " +
                        "}";
        graph.setAttribute("ui.stylesheet", stylesheet);
        graph.setAttribute("ui.quality");
        graph.setAttribute("ui.antialias");

        Map<String, String> coresPorArtista = new HashMap<>();
        String[] coresDisponiveis = {"#FF5733", "#33FF57", "#3357FF", "#FF33A1", "#A133FF", "#33FFA1", "#FFC300", "#C70039"};
        int corIndex = 0;

        for (String trackId : nossoGrafo.getAdjacencias().keySet()) {
            if (graph.getNode(trackId) == null) {
                Optional<Musica> musicaOpt = catalogo.buscarMusicaPorId(trackId);
                if (musicaOpt.isPresent()) {
                    Musica musica = musicaOpt.get();
                    String artista = musica.getTrackArtist();
                    if (!coresPorArtista.containsKey(artista)) {
                        coresPorArtista.put(artista, coresDisponiveis[corIndex % coresDisponiveis.length]);
                        corIndex++;
                    }
                    org.graphstream.graph.Node node = graph.addNode(trackId);
                    node.setAttribute("ui.label", musica.getTrackName() + " - " + artista);
                    node.setAttribute("ui.style", "fill-color: " + coresPorArtista.get(artista) + ";");
                }
            }
        }

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

        // --- SEÇÃO CORRIGIDA ---
        // 1. Declara o layout como SpringBox para acessar seus métodos
        SpringBox layout = new SpringBox(false);

        // 2. Usa os métodos SETTER específicos da classe SpringBox
        // Aumenta a força de repulsão (experimente valores como 100, 200, 500)
        layout.setForce(1.4);
        // Define o limite de estabilização (valores entre 0 e 1, mais perto de 1 é melhor)
        layout.setStabilizationLimit(0.8);

        Viewer viewer = new SwingViewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.enableAutoLayout(layout); // Passa nosso layout configurado

        ViewPanel viewPanel = (ViewPanel) viewer.addDefaultView(false);

        viewPanel.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double zoomFactor = e.getWheelRotation() < 0 ? 1.1 : 0.9;
                viewPanel.getCamera().setViewPercent(viewPanel.getCamera().getViewPercent() * zoomFactor);
            }
        });

        javax.swing.JFrame frame = new javax.swing.JFrame("Grafo Musical");
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        frame.add(viewPanel);
        frame.pack();
        frame.setSize(1280, 720);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}