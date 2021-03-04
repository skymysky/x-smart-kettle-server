package org.yaukie.frame;

/**
 * @Author: yuenbin
 * @Date :2020/10/28
 * @Time :14:13
 * @Motto: It is better to be clear than to be clever !
 * @Destrib:
 **/

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Test {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        String batPath = "C:/Philips/SIServer/PostStartupScript.bat"; // 把你的bat脚本路径写在这里
        File batFile = new File(batPath);
        boolean batFileExist = batFile.exists();
        System.out.println("batFileExist:" + batFileExist);
        if (batFileExist) {
            callCmd(batPath);
        }
    }

    private static void  callCmd(String locationCmd){
        StringBuilder sb = new StringBuilder();
        try {
            Process child = Runtime.getRuntime().exec(locationCmd);
            InputStream in = child.getInputStream();
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(in));
            String line;
            while((line=bufferedReader.readLine())!=null)
            {
                sb.append(line + "\n");
            }
            in.close();
            try {
                child.waitFor();
            } catch (InterruptedException e) {
                System.out.println(e);
            }
            System.out.println("sb:" + sb.toString());
            System.out.println("callCmd execute finished");
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
