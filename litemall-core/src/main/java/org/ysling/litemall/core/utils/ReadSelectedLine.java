package org.ysling.litemall.core.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义文件读取
 */
public class ReadSelectedLine {


    /**
     * 从指定行开始读文件
     * @param txtPath 文件路径
     * @param lineNum 指定读取开始行号
     */
    public static List<String> readLineByNum(String txtPath, int lineNum) throws IOException {
        //文件总行数
        long count = Files.lines(Paths.get(txtPath)).count();
        LineNumberReader lnr = new LineNumberReader(new FileReader(txtPath));
        return readLine(lnr , lineNum , count);
    }


    /**
     * 从指定行开始读文件
     * @param file    文件
     * @param fromIndex 指定读取开始行号
     */
    public static List<String> readLineByNum(File file, int fromIndex) throws IOException {
        //文件总行数
        long count = Files.lines(file.toPath()).count();
        LineNumberReader lnr = new LineNumberReader(new FileReader(file));
        return readLine(lnr , fromIndex , count);
    }


    private static List<String> readLine(LineNumberReader lnr, int fromIndex, long count) throws IOException {
        ArrayList<String> list = new ArrayList<>();
        String line = lnr.readLine();
        while (line != null){
            if (lnr.getLineNumber() >= fromIndex && lnr.getLineNumber() <= count){
                list.add(line);
            }
            line = lnr.readLine();
        }
        return list;
    }

}
