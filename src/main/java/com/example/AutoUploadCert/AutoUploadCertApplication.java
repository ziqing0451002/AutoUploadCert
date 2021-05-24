package com.example.AutoUploadCert;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

@SpringBootApplication
public class AutoUploadCertApplication {

    public static void main(String[] args) throws IOException, JSONException {
        SpringApplication.run(AutoUploadCertApplication.class, args);


        Scanner scanner = new Scanner(System.in);
        System.out.print("Please input your userAccount: ");
        String userAccount = scanner.next();
        System.out.print("Please input your userPassword: ");
        String userPassword = scanner.next();
//        String userAccount = "vaultTest2";
//        String userPassword = "1234";

        if (userAccount != null && userPassword != null){
            System.out.print("userAccount is " + userAccount + "\nuserPassword is " + userPassword);
            String result = AutoUpload(userAccount,userPassword);
            System.out.println(result);

        }

    }

    public static String AutoUpload(String userAccount, String userPassword) throws IOException, JSONException {
        if (userVerify(userAccount, userPassword)) {
            String autoUploadURL = "http://localhost:8000/api/TVCertificate/setTVCertificate?userAccount=" + userAccount + "&userPassword=" + userPassword;
            JSONObject jsonData = new JSONObject();
            String[] certColumn = {"certId", "certName", "gettingTime", "agenceFrom", "content"};
            //遍歷上傳目錄下的所有CSV檔案
            FileReader fr = null;
            File dir = new File("C:\\Users\\4884\\Desktop\\4884\\CertuficateAutoUpload\\Cert_CSV_File");
            File uploadComplete = new File("C:\\Users\\4884\\Desktop\\4884\\CertuficateAutoUpload\\Finish");
            File[] files = dir.listFiles();
            try {
                if (files != null) {
                    for (int i = 0; i < files.length; i++) {
                        String filePath = files[i].getAbsolutePath().toLowerCase().trim();
                        String filename = files[i].getName();
                        int startIndex = filename.lastIndexOf('.') + 1;
                        String fcsv = filename.substring(startIndex);
                        System.out.println("fcsv    :" + fcsv);
                        if (fcsv.equals("csv")) {
                            System.out.println(filePath);
                            File getCSVFiles = new File(filePath);
                            Scanner sc = new Scanner(getCSVFiles);
                            while (sc.hasNext()) {
                                String[] cert = sc.next().split(",");
                                for (int j = 0; j < cert.length; j++) {
                                    jsonData.put(certColumn[j], cert[j]);
                                }
//                                System.out.println(jsonData);
                                //呼叫 Set TV_Cert API
                                String jsonString = jsonData.toString();
                                System.out.println("jsonString:" + jsonString);
                                postMethod(autoUploadURL, jsonString);
                            }
                            sc.close();
                            System.out.println(uploadComplete);
                            System.out.println(getCSVFiles.getName());
                            if(getCSVFiles.renameTo(new File(uploadComplete +"\\"+ filename))){
                                System.out.println("File is moved successful!");
                            }else{
                                System.out.println("File is failed to move!");
                            }
                        } else {
                            return "檔案目錄下無CSV檔案";
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "AutoUpload 結束";
    }

//    public static ArrayList getTagList (BufferedReader br)
//    {
//        ArrayList taglist = new ArrayList();
//        String sData = null;
//        try
//        {
//
//            while ((sData = br.readLine()) != null)
//            {
//                String strArray[] = sData.split(",");
//                taglist.add(strArray);
//            }
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//        return taglist;
//    }

    public static boolean userVerify(String userAccount, String userPassword) throws IOException, JSONException {
        String userVerifyURL = "http://localhost:8000/api/account/userLogin/"+ userAccount +"?userPassword=" + userPassword;
        String verifyResult = getMethod(userVerifyURL);
        if (verifyResult.equals("true")){
            System.out.println("身分驗證成功");
            return true;
        }else{
            System.out.println(verifyResult);
            return false;
        }
    }

    //get
    public static String getMethod(String url) throws IOException {
        URL restURL = new URL(url);

        HttpURLConnection conn = (HttpURLConnection) restURL.openConnection();

        conn.setRequestMethod("GET"); // POST GET PUT DELETE
        conn.setRequestProperty("Accept", "application/json");

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while((line = br.readLine()) != null ){
            System.out.println(line);
            return line;
        }
        br.close();
        return "身分驗證失敗";
    }

    //post
    public static boolean postMethod(String url, String query) throws IOException {
        URL restURL = new URL(url);
        System.out.println("url:" + url);
        System.out.println("query:" + query);
        HttpURLConnection conn = (HttpURLConnection) restURL.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        PrintStream ps = new PrintStream(conn.getOutputStream());
        ps.print(query);
        ps.close();

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while((line = br.readLine()) != null ){
            System.out.println(line);
        }

        br.close();

        return true;
    }

}
