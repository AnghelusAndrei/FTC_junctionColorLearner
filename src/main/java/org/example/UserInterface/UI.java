package org.example.UserInterface;

import basicneuralnetwork.NeuralNetwork;
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

    public UI(VideoCapture capture, NeuralNetwork network) throws Exception {
        this.capture = capture;
        imageJframe = HighGui.createJFrame("Vision training data", JFrame.EXIT_ON_CLOSE);
        label = new JLabel();
        window = new Window(imageJframe, label);
        surface = new Surface(capture);
        events = new EventHandler(window, surface, network);
        window.init(surface.window_surface);
    }

    public void run(NeuralNetwork network) throws Exception {
        while(capture.read(surface.image)){
            if(!window.previewState)window.runTraining(surface.getWindowSurface(window));
            else window.runPreview(surface.getPreviewSurface(network));
        }
    }
}
