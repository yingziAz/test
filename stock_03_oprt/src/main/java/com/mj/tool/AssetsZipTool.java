package com.mj.tool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;

import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

import com.yahoo.platform.yui.compressor.CssCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

public class AssetsZipTool {

    /** 需要压缩的文件名 */
    String[] filesNeedZip = new String[]{
    		//共通

            //"js/Base.js",
            //"js/BaseApp.js",
            //"js/Component.js",
            //"js/Config.js",
            //"js/Plugin.js",
            //"js/Site.js",

            //"js/Section/GridMenu.js",
            //"js/Section/Menubar.js",
            //"js/Section/PageAside.js",
            //"js/Section/Sidebar.js",

            "js/util.js",

            //"vendor/babel-external-helpers/babel-external-helpers.js",
            
            
            
            //样式
            "css/site.css",
            "css/style.css",
            //页面
            "css/page/login.css",
            "css/page/dashboard.css",
            
            
            
            //bootstrap -table
            //"plugin/bootstrap-table/bootstrap-table.js",
            //"plugin/bootstrap-table/bootstrap-table.css"
            
            
            
            
            
            
    };

    //基础路径
    private final String basePath = new File("").getAbsolutePath();

    int linebreakpos = -1;
    boolean munge = true;
    boolean verbose = false;
    boolean preserveAllSemiColons = false;
    boolean disableOptimizations = false;

    public static void main(String[] args) {
        try {
            AssetsZipTool fzt = new AssetsZipTool();
            fzt.fileZip();
            //fzt.closeDevMode();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fileZip() throws Exception {
    	System.out.println("--->"+basePath);
        System.out.println("-----文件压缩开始-----");
        for (String fileName : filesNeedZip) {
            File file = new File(basePath + "/src/main/webapp/assets/" + fileName);
            checkFile(file);
        }
        System.out.println("-----文件压缩结束-----");
    }

    /**
     * DevMode修改为false
     * @throws Exception
     */
    public void closeDevMode() throws Exception{
        //    	FileInputStream fis = new FileInputStream(basePath+"/04-resources/env_config.txt");
        //    	InputStreamReader isr = new InputStreamReader(fis);
        //    	BufferedReader br = new BufferedReader(isr);

        FileOutputStream fos = new FileOutputStream(basePath+"/04-resources/env_config.txt");
        OutputStreamWriter osw = new OutputStreamWriter(fos);
        PrintWriter pw = new PrintWriter(osw);

        pw.write("devMode = false");

        //    	br.close();
        pw.close();
    }

    public void checkFile(File file) throws Exception {

        String fileName = file.getName();

        // file 要处理的目录
        if (fileName.endsWith(".svn"))
            return;

        if (fileName.endsWith(".min.js") || fileName.endsWith(".min.css")) {
            return;
        }

        if (file.isFile()) {
            jsZip(file);
        }
    }

    /**
     * @param file
     *            需要压缩的目录
     * @throws Exception
     */
    public void jsZip(File file) throws Exception {
        String fileName = file.getName();
        String lastName = null;
        String cutfilePath = null;

        // fileName：当前正在压缩的文件为
        System.out.println("正在压缩的文件为" + fileName);
        if (fileName.endsWith(".js") == false
                && fileName.endsWith(".css") == false) {
            return;
        }

        Reader in = new FileReader(file);

        String filePath = file.getAbsolutePath();

        File tempFile = new File(filePath + ".tempFile");
        Writer out = new FileWriter(tempFile);


        if (fileName.endsWith(".js")) {
            lastName = ".min.js";
            cutfilePath = filePath.substring(0, filePath.indexOf(".js"));
        } else if (fileName.endsWith(".css")) {
            lastName = ".min.css";
            cutfilePath = filePath.substring(0, filePath.indexOf(".css"));
        }
        File newFileName = new File(cutfilePath + lastName);

        // 如果该文件存在则删除，重新创建该文件
        if (newFileName.exists()) {
            System.out.println(newFileName + "文件已存在，正在删除并重新创建！");
            newFileName.delete();
            newFileName = new File(cutfilePath + lastName);
        }

        if (fileName.endsWith(".js")) {
            JavaScriptCompressor jscompressor = new JavaScriptCompressor(in,
                    new ErrorReporter() {

                public void warning(String message, String sourceName,
                        int line, String lineSource, int lineOffset) {
                    if (line < 0) {
                        System.err.println("\n[WARNING] " + message);
                    } else {
                        System.err.println("\n[WARNING] " + line + ':'
                                + lineOffset + ':' + message);
                    }
                }

                public void error(String message, String sourceName,
                        int line, String lineSource, int lineOffset) {
                    if (line < 0) {
                        System.err.println("\n[ERROR] " + message);
                    } else {
                        System.err.println("\n[ERROR] " + line + ':'
                                + lineOffset + ':' + message);
                    }
                }

                public EvaluatorException runtimeError(String message,
                        String sourceName, int line, String lineSource,
                        int lineOffset) {
                    error(message, sourceName, line, lineSource,
                            lineOffset);
                    return new EvaluatorException(message);
                }
            });

            jscompressor.compress(out, linebreakpos, munge, verbose,
                    preserveAllSemiColons, disableOptimizations);
        } else if (fileName.endsWith(".css")) {
            CssCompressor csscompressor = new CssCompressor(in);
            csscompressor.compress(out, linebreakpos);
        }
        out.close();
        in.close();
        // file.delete();
        tempFile.renameTo(newFileName);
        tempFile.delete();
    }

}
