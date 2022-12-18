package org.example.components;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

public class MultiLayerImageView extends JComponent {




    public ArrayList<BufferedImage> layers = new ArrayList<>();
    public MultiLayerImageView()
    {

    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(layers.get(0).getWidth(), layers.get(0).getHeight());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Image image :
                layers) {
            g.drawImage(image, 0,0,image.getWidth(null), image.getHeight(null), 0,0, image.getWidth(null), image.getHeight(null), null);
        }

    }
}
