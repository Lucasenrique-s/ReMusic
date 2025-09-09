package com.Recommendusic.Teste;

import com.Recommendusic.Servico.*;
import com.Recommendusic.Servico.Grafo.*;
import com.Recommendusic.Servico.Entidades.*;
import com.Recommendusic.Servico.Grafo.utils.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de teste para o RecomendadorServico.
 * Usa um pequeno conjunto de dados controlados para verificar a lógica de recomendação.
 */
@Nested
class RecomendarServicoTest {

    private MusicaServico catalogoMock;
    private Grafo grafo;
    private RecomendadorServico recomendador;

    private Musica rockSimilar1;
    private Musica rockSimilar2;
    private Musica rockSimilar3;
    private Musica rockMesmoArtista;
    private Musica popDiferente;

    /**
     * O metodo setup é executado antes de cada teste.
     * Ele cria um ambiente controlado com algumas músicas.
     */
    @BeforeEach
    void setUp() {
        // 1. Cria as músicas "falsas" (mocks) para o nosso teste

        // Rock Similar 1 (Ponto de Partida)
        rockSimilar1 = new Musica("1", "Solar Flare", "The Testers", "rock", "Cosmic Rock", 0.60, 0.85, -7.0, 0.05, 0.10, 0.70, 0.15, 0.40, 120.0);

        // Rock Similar 2 (A recomendação esperada)
        rockSimilar2 = new Musica("2", "Meteor Shower", "The Testers", "rock", "Cosmic Rock", 0.62, 0.83, -7.2, 0.06, 0.11, 0.68, 0.16, 0.42, 122.0);

        // Rock Similar 3 (Outra música similar)
        rockSimilar3 = new Musica("5", "Nebula's Cry", "The Testers", "rock", "Cosmic Rock", 0.58, 0.88, -6.8, 0.04, 0.15, 0.75, 0.12, 0.38, 118.0);

        // Outra música de Rock do mesmo artista, mas com som diferente
        rockMesmoArtista = new Musica("3", "Supernova", "The Testers", "rock", "Galactic Hits", 0.55, 0.92, -6.0, 0.08, 0.05, 0.60, 0.25, 0.55, 125.0);

        // Música Pop (Não deve ser recomendada)
        popDiferente = new Musica("4", "Dancing Queen", "The Popsters", "pop", "Pop Stars", 0.85, 0.70, -5.0, 0.15, 0.30, 0.01, 0.08, 0.75, 110.0);

        List<Musica> listaDeMusicas = List.of(rockSimilar1, rockSimilar2, rockSimilar3, rockMesmoArtista, popDiferente);

        // Normaliza os dados como no sistema real
        Musica.normalize(new ArrayList<>(listaDeMusicas));

        // 2. Cria um MusicaServico "falso" que usa a nossa pequena lista
        catalogoMock = new MusicaServico(listaDeMusicas);

        // 3. Constrói o grafo apenas com as nossas músicas
        ConstrutorGrafo construtor = new ConstrutorGrafo();
        construtor.construirGrafo(listaDeMusicas);
        grafo = construtor.getGrafo();

        // 4. Cria a instância do serviço que vamos testar
        recomendador = new RecomendadorServico();
    }

    @Test
    @DisplayName("Deve recomendar a música de rock mais similar")
    void recomendarMusicaSimilar() {
        // Ação: Pede 1 recomendação para a música "Rock Melodrama A"
        List<Musica> recomendacoes = recomendador.recomendar(grafo, rockSimilar1, catalogoMock, 1);

        // Verificação:
        assertNotNull(recomendacoes, "A lista de recomendações não pode ser nula.");
        assertEquals(1, recomendacoes.size(), "Deve retornar exatamente uma recomendação.");
        assertEquals("Meteor Shower", recomendacoes.get(0).getTrackName(), "A primeira recomendação deve ser a música de rock mais parecida.");
        System.out.println(recomendacoes);
    }

    @Test
    @DisplayName("Não deve recomendar a música pop que é muito diferente")
    void naoRecomendarMusicaDiferente() {
        // Ação: Pede 3 recomendações para a música "Rock Melodrama A"
        List<Musica> recomendacoes = recomendador.recomendar(grafo, rockSimilar1, catalogoMock, 3);

        // Verificação:
        assertNotNull(recomendacoes);
        // CORRIGIDO: Acessa a música diretamente no stream
        boolean contemPop = recomendacoes.stream().anyMatch(m -> m.getTrackId().equals(popDiferente.getTrackId()));
        assertFalse(contemPop, "A lista de recomendações não deve incluir a música pop.");
        System.out.println(recomendacoes);
    }

    @Test
    @DisplayName("Deve funcionar corretamente com uma playlist")
    void recomendarPorPlaylist() {
        // Ação: Cria uma playlist e pede recomendações
        List<Musica> playlist = List.of(rockSimilar1, rockMesmoArtista);
        List<Musica> recomendacoes = recomendador.recomendarPorPlaylist(grafo, playlist, catalogoMock, 4);

        // Verificação:
        assertNotNull(recomendacoes);
        assertEquals(3, recomendacoes.size());
        assertEquals("Meteor Shower", recomendacoes.get(0).getTrackName(), "A recomendação da playlist deve ser a música mais próxima do conjunto.");
        System.out.println(recomendacoes);
    }
}

