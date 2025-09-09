package com.Recommendusic.UI;

import com.Recommendusic.Servico.Entidades.Musica;
import com.Recommendusic.Servico.Grafo.Grafo;
import com.Recommendusic.Servico.Grafo.utils.VisualizadorGrafo;
import com.Recommendusic.Servico.MusicaServico;
import com.Recommendusic.Servico.RecomendadorServico;
import com.Recommendusic.Servico.LeitorDePlaylist;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class InterfaceUsuario extends Application {

    private static MusicaServico catalogo;
    private static Grafo grafo;
    private static RecomendadorServico recomendador;

    private TextField nomeMusicaField, nomeArtistaField;
    private ListView<String> recomendacoesListView;
    private Label statusLabel;
    private Spinner<Integer> quantidadeSpinner;

    public static void setServicos(MusicaServico catalogo, Grafo grafo, RecomendadorServico recomendador) {
        InterfaceUsuario.catalogo = catalogo;
        InterfaceUsuario.grafo = grafo;
        InterfaceUsuario.recomendador = recomendador;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Re-Music - Sistema de Recomendação");

        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10));

        VBox controlsVBox = new VBox(15);
        controlsVBox.setPadding(new Insets(10));
        controlsVBox.setSpacing(8);

        // Seção 1: Recomendação por Música Única
        Label singleSongLabel = new Label("Recomendação por Música");
        singleSongLabel.setStyle("-fx-font-weight: bold;");
        nomeMusicaField = new TextField();
        nomeMusicaField.setPromptText("Nome da música");
        nomeArtistaField = new TextField();
        nomeArtistaField.setPromptText("Nome do artista");
        Button recommendSingleButton = new Button("Recomendar");
        recommendSingleButton.setMaxWidth(Double.MAX_VALUE);
        recommendSingleButton.setOnAction(e -> lidarComRecomendacaoMusicaUnica());

        // Seção 2: Recomendação por Arquivo
        Label playlistFileLabel = new Label("Recomendação por Arquivo");
        playlistFileLabel.setStyle("-fx-font-weight: bold;");
        Button loadFileButton = new Button("Carregar Playlist...");
        loadFileButton.setMaxWidth(Double.MAX_VALUE);
        loadFileButton.setOnAction(e -> lidarComRecomendacaoArquivo(primaryStage));

        // Seção 3: Visualização
        Label graphLabel = new Label("Visualização");
        graphLabel.setStyle("-fx-font-weight: bold;");
        Button viewGraphButton = new Button("Visualizar Grafo");
        viewGraphButton.setMaxWidth(Double.MAX_VALUE);
        viewGraphButton.setOnAction(e -> lidarComVisualizacaoGrafo());

        // Seção 4: Quantidade
        Label quantidadeLabel = new Label("Quantidade de Recomendações");
        quantidadeLabel.setStyle("-fx-font-weight: bold;");
        quantidadeSpinner = new Spinner<>(1, 50, 10);
        quantidadeSpinner.setEditable(true);

        controlsVBox.getChildren().addAll(
                singleSongLabel, nomeMusicaField, nomeArtistaField, recommendSingleButton,
                new Separator(Orientation.HORIZONTAL),
                playlistFileLabel, loadFileButton,
                new Separator(Orientation.HORIZONTAL),
                graphLabel, viewGraphButton,
                new Separator(Orientation.HORIZONTAL),
                quantidadeLabel, quantidadeSpinner
        );
        borderPane.setLeft(controlsVBox);

        VBox centerVBox = new VBox(10);
        Label resultsLabel = new Label("Recomendações:");
        recomendacoesListView = new ListView<>();
        centerVBox.getChildren().addAll(resultsLabel, recomendacoesListView);
        BorderPane.setMargin(centerVBox, new Insets(0, 0, 0, 10));
        borderPane.setCenter(centerVBox);

        statusLabel = new Label("Bem-vindo! Escolha uma opção para começar.");
        borderPane.setBottom(statusLabel);

        Scene scene = new Scene(borderPane, 800, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void lidarComRecomendacaoMusicaUnica() {
        String nomeMusica = nomeMusicaField.getText();
        String nomeArtista = nomeArtistaField.getText();

        if (nomeMusica == null || nomeMusica.trim().isEmpty() || nomeArtista == null || nomeArtista.trim().isEmpty()) {
            statusLabel.setText("Por favor, preencha o nome da música e do artista.");
            return;
        }

        Optional<Musica> musicaInicialOpt = catalogo.buscarMusicaPorNomeEArtista(nomeMusica, nomeArtista);

        if (musicaInicialOpt.isPresent()) {
            statusLabel.setText("Gerando recomendações para: " + musicaInicialOpt.get().getTrackName());
            int quantidade = getQuantidadeRecomendacoes();
            List<Musica> recomendacoes = recomendador.recomendar(grafo, musicaInicialOpt.get(), catalogo, quantidade);
            exibirRecomendacoes(recomendacoes);
            statusLabel.setText("Recomendações geradas com sucesso!");
        } else {
            statusLabel.setText("Música não encontrada no catálogo.");
            recomendacoesListView.getItems().clear();
        }
    }

    private void lidarComRecomendacaoArquivo(Stage owner) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir Arquivo de Playlist");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivos CSV", "*.csv"));
        File arquivo = fileChooser.showOpenDialog(owner);

        if (arquivo != null) {
            try {
                statusLabel.setText("Lendo a playlist: " + arquivo.getName());
                List<Musica> playlist = LeitorDePlaylist.carregarDeArquivo(arquivo.getAbsolutePath(), catalogo);

                if (playlist.isEmpty()) {
                    statusLabel.setText("Playlist vazia ou nenhuma música encontrada.");
                    recomendacoesListView.getItems().clear();
                } else {
                    statusLabel.setText("Gerando recomendações...");
                    int quantidade = getQuantidadeRecomendacoes(); // Pega o valor do spinner
                    List<Musica> recomendacoes = recomendador.recomendarPorPlaylist(grafo, playlist, catalogo, quantidade);
                    exibirRecomendacoes(recomendacoes);
                    statusLabel.setText("Recomendações para a playlist: " + arquivo.getName());
                }
            } catch (IOException ex) {
                statusLabel.setText("Erro ao ler o arquivo: " + ex.getMessage());
                recomendacoesListView.getItems().clear();
            }
        }
    }

    private int getQuantidadeRecomendacoes() {
        try {
            // Garante que o valor digitado (se houver) seja validado
            quantidadeSpinner.commitValue();
            return quantidadeSpinner.getValue();
        } catch (Exception e) {
            statusLabel.setText("Quantidade inválida. Usando o padrão (10).");
            return 10;
        }
    }

    private void lidarComVisualizacaoGrafo() {
        statusLabel.setText("Abrindo visualizador de grafo...");
        new Thread(() -> {
            try {
                VisualizadorGrafo.exibir(grafo, catalogo);
                Platform.runLater(() -> statusLabel.setText("Visualizador de grafo fechado."));
            } catch (Exception e) {
                Platform.runLater(() -> statusLabel.setText("Erro ao abrir o visualizador de grafo."));
                e.printStackTrace();
            }
        }).start();
    }

    private void exibirRecomendacoes(List<Musica> recomendacoes) {
        recomendacoesListView.getItems().clear();
        if (recomendacoes.isEmpty()) {
            recomendacoesListView.getItems().add("Nenhuma recomendação única foi encontrada.");
        } else {
            for (Musica rec : recomendacoes) {
                recomendacoesListView.getItems().add(rec.getTrackName() + " - " + rec.getTrackArtist());
            }
        }
    }
}

