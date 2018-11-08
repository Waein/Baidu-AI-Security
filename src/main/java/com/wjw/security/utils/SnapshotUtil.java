package com.wjw.security.utils;

import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileSystemView;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

/**
 * ===================================
 * Created With IntelliJ IDEA
 *
 * @author Waein :)
 * @version method: SnapshotUtil, v 0.1
 * @CreateDate 2018/11/6
 * @CreateTime 16:06
 * @GitHub https://github.com/Waein
 * ===================================
 */
public class SnapshotUtil extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static BufferedImage mImg;

    private BufferedImage mat2BI(Mat mat) {
        int dataSize = mat.cols() * mat.rows() * (int) mat.elemSize();
        byte[] data = new byte[dataSize];
        mat.get(0, 0, data);
        int type = mat.channels() == 1 ?
                BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_3BYTE_BGR;

        if (type == BufferedImage.TYPE_3BYTE_BGR) {
            for (int i = 0; i < dataSize; i += 3) {
                byte blue = data[i + 0];
                data[i + 0] = data[i + 2];
                data[i + 2] = blue;
            }
        }
        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
        image.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), data);
//        RescaleOp ro = new RescaleOp(0.8f, 0, null);
//        BufferedImage tempImage = ro.filter(image, null);
//        mImg = tempImage;
        return image;
    }

    public void paintComponent(Graphics g) {
        if (mImg != null) {
            g.drawImage(mImg, 0, 0, mImg.getWidth(), mImg.getHeight(), this);
        }
    }

    /**
     * opencv实现人脸识别
     *
     * @param img
     */
    public static Mat detectFace(Mat img) throws Exception {

        System.out.println("Running DetectFace ... ");
        // 从配置文件lbpcascade_frontalface.xml中创建一个人脸识别器，该文件位于opencv安装目录中
        CascadeClassifier faceDetector = new CascadeClassifier("D:\\ruanjian\\OpenCV\\opencv\\sources\\data\\haarcascades\\haarcascade_frontalface_alt.xml");

        //这是opencv的安装路径下找到sources\\data\\haarcascades\\haarcascade_frontalface_alt.xml文件

        // 在图片中检测人脸
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(img, faceDetections);
        //System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));

        Rect[] rects = faceDetections.toArray();
        if (rects != null && rects.length >= 1) { //如果有人脸
            for (Rect rect : rects) { //画框
                Imgproc.rectangle(img, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                        new Scalar(0, 255, 245), 2);
            }
            //拍照
            SimpleDateFormat sdf = new SimpleDateFormat("yyyymmddHHmmss");
            String name = sdf.format(new Date());
            //获取桌面路径
            File path = FileSystemView.getFileSystemView().getHomeDirectory();

            System.out.println(path);
            String format = "jpg";
            File f = new File(path + File.separator + name + "." + format);
            try {
                ImageIO.write(mImg, format, f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return img;
    }

    /**
     * main测试
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

            Mat capImg = new Mat();
            VideoCapture capture = new VideoCapture(1); //打开摄像头 0 代表本地摄像头，我是接了一个外接摄像头
            int height = (int) capture.get(Videoio.CAP_PROP_FRAME_HEIGHT);
            int width = (int) capture.get(Videoio.CAP_PROP_FRAME_WIDTH);
            if (height == 0 || width == 0) {
                throw new Exception("camera not found!");
            }

            JFrame frame = new JFrame("camera");
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            SnapshotUtil panel = new SnapshotUtil();
            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent arg0) {
                    System.out.println("click");
                }

                @Override
                public void mouseMoved(MouseEvent arg0) {
                    System.out.println("move");

                }

                @Override
                public void mouseReleased(MouseEvent arg0) {
                    System.out.println("mouseReleased");
                }

                @Override
                public void mousePressed(MouseEvent arg0) {
                    System.out.println("mousePressed");
                }

                @Override
                public void mouseExited(MouseEvent arg0) {
                    System.out.println("mouseExited");
                    //System.out.println(arg0.toString());
                }

                @Override
                public void mouseDragged(MouseEvent arg0) {
                    System.out.println("mouseDragged");
                    //System.out.println(arg0.toString());
                }

            });
            frame.setContentPane(panel);
            frame.setVisible(true);
            frame.setSize(width + frame.getInsets().left + frame.getInsets().right,
                    height + frame.getInsets().top + frame.getInsets().bottom);
            int n = 0;
            Mat temp = new Mat();
            while (frame.isShowing() && n < 500) {
                capture.read(capImg);
                Imgproc.cvtColor(capImg, temp, Imgproc.COLOR_RGB2BGR555);
//                Imgcodecs.imwrite("/Users/SeungRi/Work/download/Picture" + n + ".png", temp); //输出到文件查看
                Mat detectFace = detectFace(capImg);
                panel.mImg = panel.mat2BI(detectFace);
                panel.repaint();
                //n++;
                //break;
            }
            capture.release();
            frame.dispose();
        } catch (Exception e) {
            System.out.println("例外：" + e);
        } finally {
            System.out.println("--done--");
        }

    }

}
