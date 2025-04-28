package com.jungle.jungledemo;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/image")
public class OpencvGImgController {

    //输入文件地址
    public static final String INPUT_PATH = "/workspaces/jungle-demo/src/main/resources/img/image.png";

    //输出文件地址
    public static final String OUTPUT_DIR = "/workspaces/jungle-demo/src/main/resources/outputs/cropped_shapes/";
    public static final String COORDINATES_DIR = "/workspaces/jungle-demo/src/main/resources/outputs/contours_coordinates/";

    /**
     * 加载OpenCV动态库
     */
//    @PostConstruct
    public void loadOpenCV() {
        //使用 opencv 官网提供的库，需要额外安装软件
//        if (SystemUtils.IS_OS_WINDOWS) {
//            log.info("windows, 加载 opencv");
//            String libName = "lib/opencv/opencv_java4110.dll";
//            URL url = ClassLoader.getSystemResource(libName);
//            System.load(url.getPath());
//        } else if (SystemUtils.IS_OS_LINUX) {
//            log.info("linux, 加载 opencv");
//            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        }

        // 加载 OpenCV 库 开源地址：https://github.com/openpnp/opencv
        nu.pattern.OpenCV.loadLocally();
        System.out.println("OpenCV library loaded successfully. " + processImage());
    }

    @GetMapping("/process")
    public String processImage() {

        try {
            // 输出目录
            File outputDirectory = new File(OUTPUT_DIR);
            File coordinatesDirectory = new File(COORDINATES_DIR);
            if (!outputDirectory.exists()) {
                outputDirectory.mkdirs();
            }
            if (!coordinatesDirectory.exists()) {
                coordinatesDirectory.mkdirs();
            }

            // 读取图像
            Mat img = Imgcodecs.imread(INPUT_PATH, Imgcodecs.IMREAD_UNCHANGED);
            if (img.empty()) {
                return "无法读取图像文件";
            }

            // 转换为灰度图
            Mat gray = new Mat();
            Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);

            // 应用自适应阈值
            Mat binary = new Mat();
            Imgproc.adaptiveThreshold(gray, binary, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 11, 2);

            // 查找轮廓
            List<MatOfPoint> contours = new ArrayList<>();
            Mat hierarchy = new Mat();
            Imgproc.findContours(binary, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

            int count = 0;
            for (MatOfPoint contour : contours) {
                // 过滤小面积轮廓
                double area = Imgproc.contourArea(contour);
                if (area > 500) {
                    // 计算边界框
                    Rect rect = Imgproc.boundingRect(contour);

                    // 裁剪图像
                    Mat cropped = new Mat(img, rect);

                    // 创建透明背景
                    Mat transparent = new Mat(cropped.size(), CvType.CV_8UC4);
                    Mat mask = Mat.zeros(cropped.size(), CvType.CV_8UC1);
                    Imgproc.drawContours(mask, List.of(contour), -1, new Scalar(255), Imgproc.FILLED, Imgproc.LINE_8, new Mat(), 0, new Point(-rect.x, -rect.y));

                    // 将裁剪的图像复制到透明背景上
                    for (int row = 0; row < cropped.rows(); row++) {
                        for (int col = 0; col < cropped.cols(); col++) {
                            double[] pixel = cropped.get(row, col);
                            double alpha = mask.get(row, col)[0];
                            transparent.put(row, col, new double[]{pixel[0], pixel[1], pixel[2], alpha});
                        }
                    }

                    // 保存裁剪结果
                    String croppedPath = OUTPUT_DIR + "cropped_" + count + ".png";
                    Imgcodecs.imwrite(croppedPath, transparent);

                    // 保存轮廓坐标到单独的文件
                    String coordinatesPath = COORDINATES_DIR + "contour_" + count + ".txt";
                    try (FileWriter writer = new FileWriter(coordinatesPath)) {
                        for (Point point : contour.toArray()) {
                            writer.write((int) point.x + ", " + (int) point.y + "\n");
                        }
                    }

                    count++;
                }
            }

            return "处理完成，共裁剪 " + count + " 个图形，裁剪结果保存在 " + OUTPUT_DIR + "，轮廓坐标保存在 " + COORDINATES_DIR;

        } catch (IOException e) {
            e.printStackTrace();
            return "处理失败: " + e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return "处理失败: " + e.getMessage();
        }
    }
}