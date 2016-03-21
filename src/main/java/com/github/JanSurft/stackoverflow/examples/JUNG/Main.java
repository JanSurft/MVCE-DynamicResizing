package com.github.JanSurft.stackoverflow.examples.JUNG;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class Main {

    public static double globalWorkLoad = 1;

    public static void main(String[] args) {
	// write your code here

        if (args.length != 1) {
            throw new IllegalArgumentException("needs exactly 1 argument -d (for dynamic) or -s (for static)");
        }

        final Graph<MyVertex, Integer> graph = new DirectedSparseGraph<>();
        final VisualizationViewer<MyVertex, Integer> viewer = new VisualizationViewer<>(new FRLayout<>(graph));
        final List<MyVertex> vertices = new ArrayList<>();

        DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
        gm.setMode(ModalGraphMouse.Mode.PICKING);
        viewer.setGraphMouse(gm);

        if (args[0].equals("-d")) {
            viewer.getRenderContext().setVertexShapeTransformer(vertex -> {

                double local = vertex.workLoad;
                double relation = local / globalWorkLoad;
                return AffineTransform.getScaleInstance(relation, relation).createTransformedShape(vertex.shape);

            });
        } else if (args[0].equals("-s")) {
            viewer.getRenderContext().setVertexShapeTransformer(vertex -> vertex.shape);
        }

        viewer.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<>());



        for (int i = 0; i < 10; i+=2) {
            vertices.add(new MyVertex());
            vertices.add(new MyVertex());
            graph.addVertex(vertices.get(i));
            graph.addVertex(vertices.get(i+1));
            graph.addEdge(i, vertices.get(i), vertices.get(i+1));
        }

        Random rand = new Random(500);

        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> {

            while (true) {

                final Integer someValue = rand.nextInt(10);
                vertices.get(someValue).workLoad += 1.0;

                if (vertices.get(someValue).workLoad > globalWorkLoad) {
                    globalWorkLoad = vertices.get(someValue).workLoad;
                }
                viewer.repaint();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

/*        final Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000), event -> {

            System.out.println("timline doing");
            final Integer someValue = rand.nextInt(10);
            vertices.get(someValue).workLoad += 1.0;

            if (vertices.get(someValue).workLoad > globalWorkLoad) {
                globalWorkLoad = vertices.get(someValue).workLoad;
            }
            viewer.repaint();
        }));

        timeline.setCycleCount(Animation.INDEFINITE);

        timeline.play();*/

        final JFrame frame = new JFrame();
        frame.add(viewer);
        frame.setVisible(true);
        frame.setSize(500, 500);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
