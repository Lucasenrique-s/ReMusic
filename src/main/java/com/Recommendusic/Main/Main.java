package com.Recommendusic.Main;

import com.Recommendusic.Servico.Grafo.Grafo;
import com.Recommendusic.Servico.Grafo.utils.ConstrutorGrafo;
import com.Recommendusic.Servico.MusicaServico;
import com.Recommendusic.Servico.RecomendadorServico;
import com.Recommendusic.Servico.Entidades.Musica;
import com.Recommendusic.UI.InterfaceUsuario;
import javafx.application.Application;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {
            System.out.println("### INICIANDO SISTEMA DE RECOMENDAÇÃO RE-MUSIC ###");

            String caminhoCsv = "data/spotify_songs.csv";
            MusicaServico catalogo = new MusicaServico(caminhoCsv);

            int tamanhoDoGrafo = 32835;
            System.out.println("\nUsando as primeiras " + tamanhoDoGrafo + " músicas para construir o grafo...");
            List<Musica> musicasParaGrafo = catalogo.obterMusicas(tamanhoDoGrafo);
            ConstrutorGrafo construtor = new ConstrutorGrafo();
            construtor.construirGrafo(musicasParaGrafo);
            Grafo grafo = construtor.getGrafo();

            System.out.println("Sistema pronto! Iniciando a interface gráfica...");

            RecomendadorServico recomendador = new RecomendadorServico();
            InterfaceUsuario.setServicos(catalogo, grafo, recomendador);
            Application.launch(InterfaceUsuario.class, args);

        } catch (IOException e) {
            System.err.println("ERRO CRÍTICO: Não foi possível carregar o ficheiro CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }
}