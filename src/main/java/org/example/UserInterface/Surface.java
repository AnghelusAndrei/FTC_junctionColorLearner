package org.example.UserInterface;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import static org.opencv.core.CvType.CV_8U;

public class Surface {
    public Mat image;
    public Mat matrix;
    public Mat overlay;
    public Mat expectedImage;
    public Mat overlay_rgb;
    public Mat expectedImage_rgb;
    public Mat window_surface;

    Surface(VideoCapture capture){
        image = new Mat();
        image.setTo(new Scalar(0,0,0));
        capture.read(image);
        matrix = new Mat(image.rows(), image.cols(), image.type());
        matrix.setTo(new Scalar(0,0,0));
        Core.copyTo(image, matrix, image);
        expectedImage = new Mat(image.rows(), image.cols(), CV_8U);
        expectedImage_rgb = new Mat(image.rows(), image.cols(), image.type());
        window_surface = new Mat(image.rows(), image.cols(), image.type());
        overlay = new Mat(image.rows(), image.cols(), CV_8U);
        overlay_rgb = new Mat(image.rows(), image.cols(), image.type());

        window_surface.setTo(new Scalar(0,0,0));
        overlay.setTo(new Scalar(0));
        overlay_rgb.setTo(new Scalar(0,0,0));
        expectedImage.setTo(new Scalar(0));
        expectedImage_rgb.setTo(new Scalar(0,0,0));

        Core.copyTo(matrix, window_surface, matrix);
    }

    public Mat getWindowSurface(){
        window_surface.setTo(new Scalar(0,0,0));
        Imgproc.cvtColor(overlay, overlay_rgb, Imgproc.COLOR_GRAY2RGB);
        Imgproc.cvtColor(expectedImage, expectedImage_rgb, Imgproc.COLOR_GRAY2RGB);
        Core.copyTo(matrix, window_surface, matrix);
        Core.copyTo(overlay_rgb, window_surface, overlay);
        Core.copyTo(expectedImage_rgb, window_surface, expectedImage);
        return window_surface;
    }

}
