package org.example;
import basicneuralnetwork.NeuralNetwork;
import org.example.UserInterface.UI;
import org.opencv.core.*;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.io.*;
import java.util.Objects;

public class Main {




    public static void main(String[] args) throws Exception {
        try {
                System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        }catch (UnsatisfiedLinkError e){
                System.out.println("Native OpenCV not found");
                OSUtil.OS os = OSUtil.getOS();
                try(InputStream libraryInputStream=Main.class.getClassLoader().getResourceAsStream(os.getLibraryName()+os.getLibrarySuffix()))
                {
                    File libraryTempFile = File.createTempFile(os.getLibraryName(), os.getLibrarySuffix());
                    try(FileOutputStream libraryTempFileOutputStream = new FileOutputStream(libraryTempFile))
                    {

                        Objects.requireNonNull(libraryInputStream).transferTo(libraryTempFileOutputStream);
                    }
                    libraryTempFile.deleteOnExit();
                    System.load(libraryTempFile.getAbsolutePath());
                }catch (IOException ex)
                {
                    ex.printStackTrace();
                }

        }

        System.out.println("Loaded OpenCV");

        NeuralNetwork neuralNetwork = new NeuralNetwork(3, 2, 30, 1);



        VideoCapture capture;
        if(OSUtil.getOS()== OSUtil.OS.LINUX) capture= new VideoCapture(0, Videoio.CAP_V4L2);
        else capture = new VideoCapture(0);

        UI userInterface = new UI(capture, neuralNetwork);
        userInterface.run(neuralNetwork);

        capture.release();
    }
}