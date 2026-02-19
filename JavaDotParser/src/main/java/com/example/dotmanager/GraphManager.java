package com.example.dotmanager;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.nio.dot.DOTImporter;
import org.jgrapht.nio.ImportException;

import java.io.StringReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class GraphManager {
    private Graph<String, DefaultEdge> graph;

    public GraphManager() {
        this.graph = new SimpleDirectedGraph<>(DefaultEdge.class);
    }

    public void parseGraph(String filepath) throws IOException {
        DOTImporter<String, DefaultEdge> importer = new DOTImporter<>();
        importer.setVertexFactory(v -> v);
        importer.importGraph(graph, new File(filepath));
    }

    @Override
    public String toString() {
        if (graph == null) return "Graph is empty.";
        StringBuilder sb = new StringBuilder();
        sb.append("Number of nodes: ").append(graph.vertexSet().size()).append("\n");
        sb.append("Nodes: ").append(graph.vertexSet()).append("\n");
        sb.append("Number of edges: ").append(graph.edgeSet().size()).append("\n");
        sb.append("Edges: \n");
        for (DefaultEdge edge : graph.edgeSet()) {
            sb.append(graph.getEdgeSource(edge)).append(" -> ").append(graph.getEdgeTarget(edge)).append("\n");
        }
        return sb.toString();
    }
    
    // API for output to file: outputGraph(String filepath)
    // Placeholder for now
    public void outputGraph(String filepath) {
        // To be implemented
    }

    // Getters for testing
    public Graph<String, DefaultEdge> getGraph() {
        return graph;
    }
}
