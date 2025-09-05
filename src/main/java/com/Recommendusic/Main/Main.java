package com.Recommendusic.Main;

import com.Recommendusic.Servico.Entidades.Musica;
import com.Recommendusic.Servico.Grafo.ConstrutorGrafo;
import com.Recommendusic.Servico.Grafo.Grafo;
import com.Recommendusic.Servico.MusicaServico;
import com.Recommendusic.Servico.RecomendadorServico;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try {
            // --- PASSO 1: CARREGAR AS MÚSICAS ---
            // Lembre-se de colocar o caminho correto para o seu arquivo CSV
            String caminhoCsv = "data/spotify_songs.csv";
            MusicaServico catalogo = new MusicaServico(caminhoCsv);

            // Para este exemplo, vamos usar um subconjunto menor de músicas para construir o grafo.
            // Construir com 30.000 músicas pode demorar MUITO tempo (horas).
            // Comece com 5.000 ou 10.000 para testar.
            List<Musica> musicasParaGrafo = catalogo.obterMusicas(5000);

            // --- PASSO 2: CONSTRUIR O GRAFO ---
            ConstrutorGrafo construtor = new ConstrutorGrafo();
            // O metodo construirGrafo agora usa a nossa lista menor
            construtor.construirGrafo(musicasParaGrafo);
            Grafo grafo = construtor.getGrafo();

            // --- PASSO 3: OBTER RECOMENDAÇÕES ---
            RecomendadorServico recomendador = new RecomendadorServico();
            Scanner scanner = new Scanner(System.in);

            System.out.print("\nDigite o nome da música para receber recomendações: ");
            String nomeMusica = scanner.nextLine();
            System.out.print("Digite o nome do artista: ");
            String nomeArtista = scanner.nextLine();

            // Busca a música inicial no catálogo completo
            Optional<Musica> musicaInicialOpt = catalogo.buscarMusicaPorNomeEArtista(nomeMusica, nomeArtista);

            if (musicaInicialOpt.isPresent()) {
                Musica musicaInicial = musicaInicialOpt.get();
                System.out.println("Música encontrada: " + musicaInicial);

                // Verifica se a música escolhida está no grafo que construímos
                if (grafo.getAdjacencias().containsKey(musicaInicial.getTrackId())) {
                    System.out.println("Buscando recomendações...");
                    List<Musica> recomendacoes = recomendador.recomendar(grafo, musicaInicial, catalogo, 10);

                    if (recomendacoes.isEmpty()) {
                        System.out.println("Não foram encontradas recomendações similares o suficiente no grafo.");
                    } else {
                        System.out.println("\n--- Músicas recomendadas para você ---");
                        int i = 1;
                        for (Musica rec : recomendacoes) {
                            System.out.println(i++ + ". " + rec.getTrackName() + " - " + rec.getTrackArtist());
                        }
                    }
                } else {
                    System.out.println("Desculpe, a música '" + musicaInicial.getTrackName() + "' não está no subconjunto de dados usado para gerar recomendações. Tente com uma música mais popular.");
                }
            } else {
                System.out.println("Música não encontrada no catálogo.");
            }

        } catch (IOException e) {
            System.err.println("Erro ao carregar o arquivo CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }
}