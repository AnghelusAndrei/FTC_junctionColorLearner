package org.example.UserInterface;

import org.opencv.highgui.HighGui;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;

public class UI {
    private Window window;
    private Surface surface;
    private EventHandler events;

    private JFrame imageJframe;
    private JLabel label;

    private VideoCapture capture;

    public UI(VideoCapture capture) throws Exception {
        this.capture = capture;
        imageJframe = HighGui.createJFrame("Vision training data", JFrame.EXIT_ON_CLOSE);
        label = new JLabel();
        window = new Window(imageJframe, label);
        surface = new Surface(capture);
        events = new EventHandler(window, surface);
        window.init(surface.window_surface);
    }

    public void run() throws Exception {
        while(capture.read(surface.image)){
            window.run(surface.getWindowSurface());
        }
    }
}
