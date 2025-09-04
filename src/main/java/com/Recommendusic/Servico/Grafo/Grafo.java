package com.Recommendusic.Servico.Grafo;

import com.Recommendusic.Servico.Entidades.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Grafo {

    private final Map<String, List<Aresta>> adjacencias = new HashMap<>();

    public void adicionarNo(Musica musica) {
        adjacencias.putIfAbsent(musica.getTrackId(), new ArrayList<>());
    }

    public void adicionarAresta(Musica musica1, Musica musica2, double peso) {
        adicionarNo(musica1);
        adicionarNo(musica2);

        // Adiciona a aresta nos dois sentidos (grafo não-direcionado)
        adjacencias.get(musica1.getTrackId()).add(new Aresta(musica2, peso));
        adjacencias.get(musica2.getTrackId()).add(new Aresta(musica1, peso));
    }

    public Map<String, List<Aresta>> getAdjacencias() {
        return adjacencias;
    }

}
