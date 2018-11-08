package com.wjw.security.camera;

import org.bytedeco.javacpp.*;
import org.bytedeco.javacv.*;

import javax.swing.*;
import java.io.File;

import static org.bytedeco.javacpp.opencv_core.Mat;
import static org.bytedeco.javacpp.opencv_imgproc.COLOR_BGRA2GRAY;

//import static org.opencv.imgproc.Imgproc.COLOR_BGRA2GRAY;

/**
 * ===================================
 * Created With IntelliJ IDEA
 *
 * @author Waein :)
 * @version method: ShowCamera, v 0.1
 * @CreateDate 2018/11/7
 * @CreateTime 15:27
 * @GitHub https://github.com/Waein
 * ===================================
 */
public class ShowCamera {

    public static void main(String[] args) {
        try {
//            showFrames();
            videoRecorde();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 调用摄像头
     *
     * @throws FrameGrabber.Exception
     * @throws InterruptedException
     */
    public static void showFrames() throws FrameGrabber.Exception, InterruptedException {
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
        grabber.setImageWidth(1280);
        grabber.setImageHeight(720);
        grabber.start();   //开始获取摄像头数据

        CanvasFrame canvas = new CanvasFrame("Camera", 1);//新建一个窗口
        canvas.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        canvas.setAlwaysOnTop(true);
        while (true) {
            if (!canvas.isVisible()) {
                break;
            }
            Frame frame = grabber.grab();
            canvas.showImage(frame);
            Thread.sleep(50);//50毫秒刷新一次图像
        }

        grabber.stop();
        grabber.close();
    }

    /**
     * 调用视频录制
     *
     * @throws FrameGrabber.Exception
     * @throws FrameRecorder.Exception
     * @throws InterruptedException
     */
    public static void videoRecorde() throws FrameGrabber.Exception, FrameRecorder.Exception, InterruptedException {
        // Preload the opencv_objdetect module to work around a known bug.
        System.out.println(Loader.load(opencv_objdetect.class));

        FrameGrabber grabber = FrameGrabber.createDefault(0);
        grabber.start();
        Frame grabbedImage = grabber.grab();//抓取一帧视频并将其转换为图像，至于用这个图像用来做什么？加水印，人脸识别等等自行添加
        int width = grabbedImage.imageWidth;
        int height = grabbedImage.imageHeight;

        String outputFile = "/Users/SeungRi/opt/video/record.mp4";
        //String outputFile = "rtmp://127.0.0.1:1935/rtmplive/picamera";
        FrameRecorder recorder = FrameRecorder.createDefault(outputFile, width, height); //org.bytedeco.javacv.FFmpegFrameRecorder
        System.out.println(recorder.getClass().getName());//org.bytedeco.javacv.FFmpegFrameRecorder
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);// avcodec.AV_CODEC_ID_H264，编码
        recorder.setFormat("flv");//封装格式，如果是推送到rtmp就必须是flv封装格式
        recorder.setFrameRate(25);
        recorder.start();//开启录制器
        long startTime = 0;
        long videoTS;
        CanvasFrame frame = new CanvasFrame("camera", CanvasFrame.getDefaultGamma() / grabber.getGamma()); //2.2/2.2=1
        //frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        Frame rotatedFrame;
        while (frame.isVisible() && (rotatedFrame = grabber.grab()) != null) {
            frame.showImage(rotatedFrame);
            if (startTime == 0) {
                startTime = System.currentTimeMillis();
            }
            videoTS = (System.currentTimeMillis() - startTime) * 1000;//这里要注意，注意位
            recorder.setTimestamp(videoTS);
            recorder.record(rotatedFrame);
            Thread.sleep(40);
        }
        recorder.stop();
        recorder.release();
        frame.dispose();
        grabber.stop();
        grabber.close();
    }

    /**
     * 利用opencv lib库,图像处理圈出人脸,windows系统可以实现
     *
     * @param winTitle
     * @param grabber
     * @throws FrameGrabber.Exception
     * @throws InterruptedException
     */
    private void showFramesWithFace(String winTitle, FrameGrabber grabber) throws FrameGrabber.Exception, InterruptedException {
        OpenCVFrameConverter.ToMat convertToMat = new OpenCVFrameConverter.ToMat();
        File fileAbsolutePath = new File(ClassLoader.getSystemClassLoader().getResource("data/lbpcascade_frontalface_improved.xml").getFile());
        opencv_objdetect.CascadeClassifier face_cascade = new opencv_objdetect.CascadeClassifier(fileAbsolutePath.getAbsolutePath());
        opencv_core.RectVector faces = new opencv_core.RectVector();
        CanvasFrame canvas = new CanvasFrame(winTitle, 1);//新建一个窗口
        canvas.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        canvas.setAlwaysOnTop(true);
        while (true) {
            if (!canvas.isVisible()) {
                break;
            }
            Frame frame = grabber.grab();
            Mat mat = convertToMat.convert(frame);
            if (mat.empty())
                continue;
            Mat videoMatGray = new Mat();
            opencv_imgproc.cvtColor(mat, videoMatGray, COLOR_BGRA2GRAY);
            opencv_imgproc.equalizeHist(videoMatGray, videoMatGray);
            face_cascade.detectMultiScale(videoMatGray, faces);
            for (int i = 0; i < faces.size(); i++) {
                opencv_core.Rect face = faces.get(i);
                opencv_imgproc.rectangle(mat, face, opencv_core.Scalar.RED, 4, 8, 0);
            }

            canvas.showImage(convertToMat.convert(mat));
            Thread.sleep(30);//50毫秒刷新一次图像
        }
    }

}
