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
        rockSimilar1 = new Musica("1", "Rock Song A", "The Testers", "Rock", "Mamonas", 0.9, 0.5, -7.0, 0.8, 0.6, 0.0, 0.2, 0.9, 100.0);

        // Rock Similar 3 (O filho da puta)
        rockSimilar3 = new Musica("5", "Rock Song D", "The Testers", "Rock", "Mamonas", 0.9, 0.5, -7.0, 0.8, 0.6, 0.0, 0.2, 0.9, 100.0);

        // Rock Similar 2 (A recomendação esperada)
        rockSimilar2 = new Musica("2", "Rock Song B", "The Testers", "Rock", "Mamonas", 0.9, 0.5, -7.0, 0.8, 0.6, 0.0, 0.2, 0.9, 100.0);

        // Outra música de Rock do mesmo artista, mas com som um pouco diferente
        rockMesmoArtista = new Musica("3", "Rock Song C", "The Testers", "Rock", "Miminos", 0.9, 0.5, -7.0, 0.8, 0.6, 0.0, 0.2, 0.9, 100.0);

        // Música Pop (Não deve ser recomendada)
        popDiferente = new Musica("4", "Pop Song X", "The Popsters", "Pop", "Popestrelas", 0.9, 0.5, -7.0, 0.8, 0.6, 0.0, 0.2, 0.9, 100.0);

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
        // Ação: Pede 1 recomendação para a música "Rock Song A"
        List<Musica> recomendacoes = recomendador.recomendar(grafo, rockSimilar1, catalogoMock, 1);

        // Verificação:
        assertNotNull(recomendacoes, "A lista de recomendações não pode ser nula.");
        assertEquals(1, recomendacoes.size(), "Deve retornar exatamente uma recomendação.");
        assertEquals("Rock Song B", recomendacoes.get(0).getTrackName(), "A primeira recomendação deve ser a música de rock mais parecida.");
    }

    @Test
    @DisplayName("Não deve recomendar a música pop que é muito diferente")
    void naoRecomendarMusicaDiferente() {
        // Ação: Pede 3 recomendações para a música "Rock Song A"
        List<Musica> recomendacoes = recomendador.recomendar(grafo, rockSimilar1, catalogoMock, 3);

        // Verificação:
        assertNotNull(recomendacoes);
        // Verifica se a música pop NÃO está na lista de recomendações
        boolean contemPop = recomendacoes.stream().anyMatch(m -> m.getTrackId().equals(popDiferente.getTrackId()));
        assertFalse(contemPop, "A lista de recomendações não deve incluir a música pop.");
    }

    @Test
    @DisplayName("Deve funcionar corretamente com uma playlist")
    void recomendarPorPlaylist() {
        // Ação: Cria uma playlist e pede recomendações
        List<Musica> playlist = List.of(rockSimilar1, rockMesmoArtista);
        List<Musica> recomendacoes = recomendador.recomendarPorPlaylist(grafo, playlist, catalogoMock, 1);

        // Verificação:
        assertNotNull(recomendacoes);
        assertEquals(1, recomendacoes.size());
        // A recomendação mais próxima da playlist deve ser a "Rock Song B"
        assertEquals("Rock Song B", recomendacoes.get(0).getTrackName(), "A recomendação da playlist deve ser a música mais próxima do conjunto.");
    }
}

