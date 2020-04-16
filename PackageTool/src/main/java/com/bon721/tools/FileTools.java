package com.bon721.tools;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;

/**
 * @author 10271
 * <p>
 * 1. 读取配置文件
 * 2. 读取文件清单
 * 3.
 */
public class FileTools {

    /**
     * config配置文件名称
     */
    private static String configFileName = "config.properties";
    /**
     * 基础Java源路径
     */
    private static String sourceBasePath = "";
    /**
     * 基础Classes路径
     */
    private static String classesBasePath = "";
    /**
     * 目标源路径
     */
    private static String targetBasePath = "";
    /**
     * 文件清单文件名称
     */
    private static String filelistName = "";
    /**
     * 补丁文件夹名称
     */
    private static String patchDirectoryName = "";
    /**
     * 失败文件清单
     */
    private static StringBuffer failureFileBuf = new StringBuffer();
    /**
     * 输出路径文件清单名称
     */
    private static String outFileListName = "补丁文件清单.properties";

    /**
     * 当前目录
     */
    private static String currentPath = System.getProperty("user.dir");


    /**
     * 获取字段值
     * */
    static {
        try {
            System.out.println("=======                     =======");
            System.out.println("=======    开始读取配置_1            ");
            System.out.println("=======                            ");

            getProperties();
            System.out.println("=======    获取到  源路径:" + sourceBasePath + "    ");
            System.out.println("=======    获取到目标路径:" + targetBasePath + "    ");
            System.out.println("=======    获取到文件列表:" + filelistName + "    ");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println("=======                            ");
        System.out.println("=======    结束读取配置_1            ");
        System.out.println("=======                     =======");
    }


    /**
     * 读取properties并返回文件对象
     */
    public static void getProperties() throws Exception {
        Properties properties = null;
        File file = null;
        InputStreamReader fileInputStream = null;
        if (StringUtils.isEmpty(configFileName)) {
            throw new Exception("配置文件为空!");
        }
        properties = new Properties();
        file = new File(currentPath + File.separator + configFileName);
        if (!file.exists()) {
            failureFileBuf.append("配置文件 config.properties 必须与jar包在同一目录下\n\n");
            throw new Exception("=======    配置文件 config.properties 必须与jar包在同一目录下    =======");
        }
        fileInputStream = new InputStreamReader(new FileInputStream(file), "UTF-8");
        if (fileInputStream == null) {
            failureFileBuf.append("未加载到正确的properties文件, properties的正确命名格式: ")
                    .append(configFileName);
            throw new Exception("=======    未加载到正确的properties, 入参: " + configFileName + "    =======");
        }
        properties.load(fileInputStream);
        sourceBasePath = properties.getProperty("projectDirectory");
        targetBasePath = properties.getProperty("targetDirectory");
        filelistName = properties.getProperty("filelistName");
        classesBasePath = properties.getProperty("classesDirectory");
        patchDirectoryName = properties.getProperty("patchDirectoryName");

        if (StringUtils.isEmpty(sourceBasePath)) {
            failureFileBuf.append("=======   配置文件中未配置 sourceBasePath");
            throw new Exception("=======    项目路径为空    =======");
        }
        if (StringUtils.isEmpty(targetBasePath)) {
            failureFileBuf.append("=======   配置文件中未配置 targetDirectory");
            throw new Exception("=======    目标路径为空    =======");
        }
        if (StringUtils.isEmpty(filelistName)) {
            failureFileBuf.append("=======   配置文件中未配置 补丁文件清单  filelistName= 必须有值");
            throw new Exception("=======    补丁清单名称为空    =======");
        }
        if (StringUtils.isEmpty(classesBasePath)) {
            failureFileBuf.append("=======   配置文件中未配置 classes");
            throw new Exception("=======    classes文件目录为空    =======");
        }
        if (StringUtils.isEmpty(patchDirectoryName)) {
            failureFileBuf.append("=======   配置文件中未配置 patchDirectoryName");
            throw new Exception("=======    补丁文件夹名称为空    =======");
        }

    }

    /**
     * 将源文件复制到目标路径
     */
    public static void copySourceFileToTarget(String sourceFile, String targetFile) {

        File souFile = new File(sourceFile);
        File tarFile = new File(targetFile);
        try {
            FileUtils.copyFile(souFile, tarFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取待生成目录Path
     */
    public static void getTargetPrefix() {

        //基础源路径
        String basePath = "D:\\dev\\iSoftStone\\projects\\中广核财务有限责任公司\\trunk\\01-workspace\\workspace-01";
        //待补丁文件路径
        String sourceFilePath = "\\trunk\\src\\main\\java\\com\\iss\\finance\\batchapprove\\dao\\BatchApproveDao.java";
        //项目名称
        String projectName = sourceFilePath.substring(0, sourceFilePath.indexOf("src"));
        //构建完整路径  =>  用于参数1
        String sourcePath_JAVA = basePath + sourceFilePath;
        //目标基础路径
        String targetPath = "C:\\Users\\10271\\Desktop\\代码补丁";
        //构建目标完成路径  =>  用于参数2
        targetPath = targetPath + sourceFilePath;
        //classes基础路径
        String classesBasePath = "D:\\dev\\iSoftStone\\projects\\中广核财务有限责任公司\\trunk\\01-workspace\\ZGH-001\\target\\production";
        //构建完整路径  =>  用于参数1
        String sourcePath_CLASSES = classesBasePath + sourceFilePath.substring(0, sourceFilePath.indexOf(".java")) + ".class";
//        copySourceFileToTarget(sourcePath, targetPath);
        System.out.println(sourcePath_CLASSES);
    }

    /**
     * 替换JAVA文件/CLASSES
     */
    public static void copyFile(String sourceFilePath, boolean isJava) throws Exception {
        String sourcePath = "";
        String targetPath = "";
        String classesPath = "";

        //源完整路径
        sourcePath = sourceBasePath + sourceFilePath;
        //Classes完整路径
        classesPath = classesBasePath + sourceFilePath;
        //目标完整路径
        targetPath = targetBasePath + sourceFilePath;

        //复制JAVA / 非classes
        try {
            FileUtils.copyFile(new File(sourcePath), new File(targetPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //文件后缀为java则复制对应classes
        if (isJava) {
            try {
                targetPath = targetPath.replace("src", "classes");
                targetPath = targetPath.substring(0, targetPath.indexOf(".")) + ".class";

                classesPath = classesPath.replace("/src/main/java/", "/");
                classesPath = classesPath.substring(0, classesPath.indexOf(".")) + ".class";

                System.out.println("classPath路径：" + classesPath.replace("//", "/"));
                System.out.println("targetPath路径：" + targetPath.replace("//", "/"));
                FileUtils.copyFile(new File(classesPath), new File(targetPath));
            } catch (IOException e) {
                throw new Exception("=======    复制classes文件失败, 错误信息: " + e.getMessage() + "    =======");
            }
        }
    }

//    /**
//     * 读取文件列表 filelist的详情
//     */
//    @Deprecated
//    public static List<String> getFileList() throws Exception {
//
//        Properties properties = null;
//        List<String> fileList = null;
//        properties = new Properties();
//        InputStream systemResourceAsStream = ClassLoader.getSystemResourceAsStream(filelistName);
//        if (systemResourceAsStream == null) {
//            throw new Exception("=======    补丁清单配置文件不存在    =======");
//        }
//        properties.load(systemResourceAsStream);
//        Iterator<Map.Entry<Object, Object>> iterator = properties.entrySet().iterator();
//        fileList = new ArrayList<>();
//        System.out.println("=======    开始遍历文件清单    =======");
//        String filePath = "";
//        while (iterator.hasNext()) {
//            filePath = iterator.next().getKey().toString();
//            fileList.add(filePath);
//            System.out.println("=======    " + filePath + "    =======");
//        }
//        return fileList;
//    }

    /**
     * 复制 filelist 文件清单properties
     */
    public static void copyFileListPro() throws Exception {

        File file = null;
        FileInputStream systemResourceAsStream = null;

        //InputStream systemResourceAsStream = ClassLoader.getSystemResourceAsStream(filelistName);
        file = new File(currentPath + File.separator + filelistName);

        if (!file.exists()) {
            throw new Exception("=======    补丁清单 filelist.properties 文件不存在    ========");
        }
        systemResourceAsStream = new FileInputStream(file);
        if (systemResourceAsStream == null) {
            throw new Exception("=======    补丁清单文件不存在    =======");
        }
        int index;
        byte[] bytes = new byte[1024];
        FileOutputStream fileOutputStream = new FileOutputStream(targetBasePath + File.separator +  outFileListName);
        while ((index = systemResourceAsStream.read(bytes)) != -1) {
            fileOutputStream.write(bytes, 0, index);
            fileOutputStream.flush();
        }
        fileOutputStream.close();
        systemResourceAsStream.close();

    }

    /**
     * 清空目标文件夹
     */
    public static void clearTargetDirctory() {
        try {
            System.out.println("=======    开始清空目标文件夹_3    =======");
            if (-1 != targetBasePath.indexOf(patchDirectoryName)) {
                FileUtils.cleanDirectory(new File(targetBasePath));
            } else {
                System.out.println("=======    无效的目标路径    =======");
                return;
            }
            System.out.println("=======    结束清空目标文件夹_3    =======");
        } catch (IOException e) {
            System.out.println("=======    无效路径 " + e.getMessage() + "    =======");
        }
    }


    /**
     * 获取当前目录下配置文件清单
     */
    public static List<String> getFileList() throws Exception {
        System.out.println("=======                               =======");
        System.out.println("=======    开始读取文件清单配置文件_2    =======");
        System.out.println("=======                               =======");

        System.out.println("=======    准备获取当前目录下配置文件  " + currentPath + "  =======");
        Properties properties = new Properties();
        List<String> fileList = null;
        FileInputStream fileInputStream = null;
        Iterator<Map.Entry<Object, Object>> iterator = null;
        //InputStream systemResourceAsStream = ClassLoader.getSystemResourceAsStream(filelistName);
        File file = new File(currentPath + File.separator + filelistName);
        if (!file.exists()) {
            failureFileBuf.append("补丁清单配置文件不存在!");
            throw new Exception("=======    补丁清单配置文件不存在 " + filelistName + "     =======");
        }
        try {
            fileInputStream = new FileInputStream(file);
            properties.load(fileInputStream);
            iterator = properties.entrySet().iterator();
        } catch (IOException e) {
            throw new Exception("=======    加载配置清单配置文件失败    =======");
        }
        fileList = new ArrayList<>();
        System.out.println("=======    开始遍历文件清单    ");
        System.out.println("=======  ==  ==  ==  ==  ==  ==  ==  ==  ==  =");
        String filePath = "";
        while (iterator.hasNext()) {
            filePath = iterator.next().getKey().toString();
            fileList.add(filePath);
            System.out.println("=======    " + filePath + "    ");
        }
        System.out.println("=======  ==  ==  ==  ==  ==  ==  ==  ==  ==  =");
        System.out.println("=======                               ");
        System.out.println("=======    结束读取文件清单配置文件_2    =======");
        System.out.println("=======                               ");
        System.out.println("=======                               =======");
        return fileList;
    }

    /**
     * 复制文件清单中文件
     */
    public static void copyListFile(List<String> fileList) throws Exception {
        System.out.println("=======    开始复制CLASSES_4    =======");
        System.out.println("\n");
        for (String filePath : fileList) {
            copyFile(filePath, filePath.contains(".java"));
        }
        System.out.println("\n");
        System.out.println("=======    复制结束CLASSES_4    =======");
    }

    /**
     * 复制清单文件
     */
    public static void createFileList() throws Exception {
        System.out.println("=======    开始复制文件清单文件_5    =======");
        copyFileListPro();
        System.out.println("=======    结束复制文件清单文件_5    =======");
    }

    /**
     * 入口
     */
    public static void doCopy() {
        try {
            if (StringUtils.isNotEmpty(failureFileBuf)) {
                throw new Exception(failureFileBuf.toString());
            }
            List<String> fileList = getFileList();
            clearTargetDirctory();
            copyListFile(fileList);
            createFileList();
            System.out.println("=======               =======");
            System.out.println("=======               =======");
            System.out.println("=======    执行完成    =======");
            System.out.println("=======               =======");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getMessage());
            System.out.println(e.getMessage());
        }
    }

}
