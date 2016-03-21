package com.github.JanSurft.stackoverflow.examples.JUNG;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * \brief
 * \details
 * \date 21.03.16
 *
 * @author Jan Hermes
 * @version 0.0.1
 */
public class MyVertex {

    public Shape shape = new Ellipse2D.Double(-50, -50, 100, 100);

    public double workLoad = 1;

    public String toString() {
        return "" + workLoad;
    }
}
