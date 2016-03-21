package com.github.JanSurft.stackoverflow.examples.JUNG;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

import javax.swing.JFrame;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * \brief
 * \details
 * \date 21.03.16
 *
 * @author Jan Hermes
 * @version 0.0.1
 */
public class Main {

    // the global workload
    private static double globalWorkLoad = 1;

    public static void main(String[] args) {
	// write your code here

        if (args.length != 1) {
            throw new IllegalArgumentException("needs exactly 1 argument -d (for dynamic) or -s (for static)");
        }

        // INitialize the graph, layout and the visualization of the graph
        final Graph<MyVertex, Integer> graph = new DirectedSparseGraph<>();
        final VisualizationViewer<MyVertex, Integer> viewer = new VisualizationViewer<>(new FRLayout<>(graph));

        // All vertices will be stored in this list
        final List<MyVertex> vertices = new ArrayList<>();

        // Create a graph mouse that lets you move around the vertices
        DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
        gm.setMode(ModalGraphMouse.Mode.PICKING);
        viewer.setGraphMouse(gm);


        // parse the command line and do dynamic or static
        if (args[0].equals("-d")) {
            viewer.getRenderContext().setVertexShapeTransformer(vertex -> {

                double local = vertex.workLoad;
                double relation = local / globalWorkLoad;
                return AffineTransform.getScaleInstance(relation, relation).createTransformedShape(vertex.shape);

            });
        } else if (args[0].equals("-s")) {
            viewer.getRenderContext().setVertexShapeTransformer(vertex -> vertex.shape);
        }

        // make the node transparend so you see if arrowhead are inside or not
        viewer.getRenderContext().setVertexFillPaintTransformer(v -> new Color(0x00000000, true));

        // label the vertices for info on workload
        viewer.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<>());

        // create the starting layout
        for (int i = 0; i < 10; i+=2) {
            vertices.add(new MyVertex());
            vertices.add(new MyVertex());
            graph.addVertex(vertices.get(i));
            graph.addVertex(vertices.get(i+1));
            graph.addEdge(i, vertices.get(i), vertices.get(i+1));
        }

        // init a pseudo random generator
        final Random rand = new Random(500);

        // service for manipulating workload and edges
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> {

            // This thread adds workloads to the vertices and updates the global worklaod accordingly
            // also it repaints the view on every step
            int nextID = 10;
            while (!Thread.interrupted()) {

                final Integer someValue = rand.nextInt(10);
                vertices.get(someValue).workLoad += 1.0;

                final int someValueDest = rand.nextInt(10);

                graph.addEdge(nextID++, vertices.get(someValue), vertices.get(someValueDest));

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

        // Create jFrame for showing the visualization
        final JFrame frame = new JFrame();
        frame.add(viewer);
        frame.setVisible(true);
        frame.setSize(500, 500);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
