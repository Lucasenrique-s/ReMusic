package com.Recommendusic.Main;

import com.Recommendusic.Servico.Entidades.Musica;
import com.Recommendusic.Servico.Grafo.Grafo;
import com.Recommendusic.Servico.Grafo.utils.ConstrutorGrafo;
import com.Recommendusic.Servico.Grafo.utils.VisualizadorGrafo;
import com.Recommendusic.Servico.MusicaServico;
import com.Recommendusic.Servico.RecomendadorServico;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        try {
            // --- ETAPA DE CONFIGURAÇÃO (FEITA APENAS UMA VEZ) ---
            System.out.println("### INICIANDO SISTEMA DE RECOMENDAÇÃO RE-MUSIC ###");
            String caminhoCsv = "data/spotify_songs.csv";
            MusicaServico catalogo = new MusicaServico(caminhoCsv);

            int tamanhoDoGrafo = 100; // Comece com um valor baixo (5000) para ser rápido
            System.out.println("\nUsando as primeiras " + tamanhoDoGrafo + " músicas para construir o grafo de similaridade...");
            List<Musica> musicasParaGrafo = catalogo.obterMusicas(tamanhoDoGrafo);

            ConstrutorGrafo construtor = new ConstrutorGrafo();
            construtor.construirGrafo(musicasParaGrafo);
            Grafo grafo = construtor.getGrafo();
            System.out.println("Sistema pronto!");

            // --- LOOP PRINCIPAL DA APLICAÇÃO ---
            Scanner scanner = new Scanner(System.in);
            RecomendadorServico recomendador = new RecomendadorServico();

            while (true) {
                exibirMenu();
                String escolha = scanner.nextLine();

                switch (escolha) {
                    case "1":
                        lidarComRecomendacaoUnica(scanner, catalogo, grafo, recomendador);
                        break;
                    case "2":
                        lidarComRecomendacaoPlaylist(scanner, catalogo, grafo, recomendador);
                        break;
                    case "3":
                        System.out.println("A abrir a janela de visualização do grafo...");
                        VisualizadorGrafo.exibir(grafo, catalogo);
                        System.out.println("A janela do grafo foi iniciada. Pode fechá-la para continuar.");
                        break;
                    case "4":
                        System.out.println("Até à próxima!");
                        scanner.close();
                        return; // Encerra o programa
                    default:
                        System.out.println("Opção inválida. Por favor, tente novamente.");
                        break;
                }
            }

        } catch (IOException e) {
            System.err.println("ERRO CRÍTICO: Não foi possível carregar o ficheiro CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void exibirMenu() {
        System.out.println("\n----------- MENU RE-MUSIC -----------");
        System.out.println("O que deseja fazer?");
        System.out.println("1. Recomendar com base numa única música");
        System.out.println("2. Recomendar com base numa playlist");
        System.out.println("3. Visualizar o grafo de músicas");
        System.out.println("4. Sair");
        System.out.print("Escolha uma opção: ");
    }

    private static void lidarComRecomendacaoUnica(Scanner scanner, MusicaServico catalogo, Grafo grafo, RecomendadorServico recomendador) {
        System.out.print("\nDigite o nome da música: ");
        String nomeMusica = scanner.nextLine();
        System.out.print("Digite o nome do artista: ");
        String nomeArtista = scanner.nextLine();

        Optional<Musica> musicaInicialOpt = catalogo.buscarMusicaPorNomeEArtista(nomeMusica, nomeArtista);

        if (musicaInicialOpt.isPresent()) {
            Musica musicaInicial = musicaInicialOpt.get();
            System.out.println("Música de referência encontrada: " + musicaInicial);
            List<Musica> recomendacoes = recomendador.recomendar(grafo, musicaInicial, catalogo, 30); // Pede mais para ter margem para filtrar
            exibirRecomendacoes(recomendacoes, List.of(musicaInicial));
        } else {
            System.out.println("Música não encontrada no catálogo.");
        }
    }

    private static void lidarComRecomendacaoPlaylist(Scanner scanner, MusicaServico catalogo, Grafo grafo, RecomendadorServico recomendador) {
        List<Musica> playlist = new ArrayList<>();
        System.out.println("\n--- Construtor de Playlist ---");
        System.out.println("Adicione músicas à sua playlist. Digite 'fim' no nome da música para terminar.");

        while (true) {
            System.out.print("Nome da música ('fim' para parar): ");
            String nomeMusica = scanner.nextLine();
            if (nomeMusica.equalsIgnoreCase("fim")) {
                break;
            }
            System.out.print("Nome do artista: ");
            String nomeArtista = scanner.nextLine();

            Optional<Musica> musicaOpt = catalogo.buscarMusicaPorNomeEArtista(nomeMusica, nomeArtista);
            if (musicaOpt.isPresent()) {
                playlist.add(musicaOpt.get());
                System.out.println("'" + musicaOpt.get().getTrackName() + "' adicionada à playlist. Músicas atuais: " + playlist.size());
            } else {
                System.out.println("Música não encontrada. Tente novamente.");
            }
        }

        if (playlist.isEmpty()) {
            System.out.println("A sua playlist está vazia. Nenhuma recomendação a gerar.");
        } else {
            System.out.println("A gerar recomendações com base na sua playlist de " + playlist.size() + " músicas...");
            List<Musica> recomendacoes = recomendador.recomendarPorPlaylist(grafo, playlist, catalogo, 30); // Pede mais para ter margem para filtrar
            exibirRecomendacoes(recomendacoes, playlist);
        }
    }

    /**
     * Verifica se dois nomes de música são demasiado similares (ex: versões diferentes da mesma canção).
     * @return true se os nomes forem muito parecidos, false caso contrário.
     */
    private static boolean isNomeSimilar(String s1, String s2) {
        // Limite de similaridade: se os nomes forem 85% ou mais parecidos, consideramo-los "iguais".
        final double LIMITE_SIMILARIDADE = 0.85;

        // Ignora parênteses e o seu conteúdo (ex: "Song (Live)", "Song (Remastered)")
        String s1Limpo = s1.replaceAll("\\(.*\\)", "").trim();
        String s2Limpo = s2.replaceAll("\\(.*\\)", "").trim();

        LevenshteinDistance levenshtein = new LevenshteinDistance();
        int distancia = levenshtein.apply(s1Limpo.toLowerCase(), s2Limpo.toLowerCase());

        // Normaliza a distância para obter uma percentagem de similaridade
        int lenMax = Math.max(s1Limpo.length(), s2Limpo.length());
        if (lenMax == 0) return true;

        double similaridade = 1.0 - ((double) distancia / lenMax);

        return similaridade >= LIMITE_SIMILARIDADE;
    }

    private static void exibirRecomendacoes(List<Musica> recomendacoes, List<Musica> musicasIniciais) {
        // --- FILTRO 1: REMOVER VERSÕES DIFERENTES DA MESMA MÚSICA (ex: "Song (Remastered)") ---
        List<Musica> recomendacoesFiltradas = recomendacoes.stream()
                .filter(rec -> {
                    // Verifica se a música recomendada é muito similar a QUALQUER música da lista inicial
                    boolean nomeMuitoSimilar = musicasIniciais.stream()
                            .anyMatch(inicial -> isNomeSimilar(rec.getTrackName(), inicial.getTrackName()));
                    return !nomeMuitoSimilar; // Mantém a música apenas se o nome NÃO for muito similar
                })
                .collect(Collectors.toList());

        // --- FILTRO 2: REMOVER DUPLICADOS EXATOS DA LISTA DE RECOMENDAÇÕES ---
        List<Musica> recomendacoesUnicas = new ArrayList<>();
        Set<String> musicasJaAdicionadas = new HashSet<>();
        for (Musica rec : recomendacoesFiltradas) {
            String chaveUnica = rec.getTrackName().toLowerCase() + "|" + rec.getTrackArtist().toLowerCase();
            // O método .add() de um Set retorna 'true' apenas se o item for novo.
            if (musicasJaAdicionadas.add(chaveUnica)) {
                recomendacoesUnicas.add(rec);
            }
        }

        // Pega as 10 primeiras recomendações da lista já sem duplicados
        List<Musica> resultadoFinal = recomendacoesUnicas.stream()
                .limit(10)
                .collect(Collectors.toList());


        if (resultadoFinal.isEmpty()) {
            System.out.println("Não foram encontradas recomendações únicas o suficiente (outras foram filtradas).");
        } else {
            System.out.println("\n♫ Músicas recomendadas para si ♫");
            int i = 1;
            for (Musica rec : resultadoFinal) {
                System.out.println(i++ + ". " + rec.getTrackName() + " - " + rec.getTrackArtist());
                System.out.println(rec.getAudioFeatures());
            }
        }
    }
}

