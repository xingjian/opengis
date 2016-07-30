package com.tongtu.gis;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

/**  
 * 功能描述: 地图代理
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2016年6月17日 上午11:21:38  
 */
public class MapProxyServlet extends HttpServlet{
    
    private String[] params_gd= {"request","x","y","z"};
    private String[] params_sw_wh= {"request","x","y","z"};
    private Map<String,String[]> paramsMap = new HashMap<String,String[]>();
    private String cacheDir = "mapofflinestudy\\src\\main\\webapp\\tiles\\";
    private String useCache = "true";
    private Map<String,String> baseUrlMap = new HashMap<String,String>();
    
    public void init() throws ServletException{
        //加载高德地图参数
        paramsMap.put("baseUrlGD", params_gd);
        paramsMap.put("baseUrlSWWH", params_sw_wh);
        baseUrlMap.put("baseUrlGD", "http://webrd04.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=1&style=7");
        baseUrlMap.put("baseWHSW", "http://182.254.143.172/fmapimg_chi_day/");
        if (StringUtils.isNotBlank(getInitParameter("cacheDir"))) {
            cacheDir = getInitParameter("cacheDir");
        }
        if (StringUtils.isNotBlank(getInitParameter("useCache"))) {
            useCache = getInitParameter("useCache");
        }
    }

    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException,IOException{
        String request = req.getParameter("request");
        String mapType = req.getParameter("mapType");
        if("getTile".equals(request)&&mapType.equals("gd")){
            getGDTile(req,res);
        }else if("getTile".equals(request)&&mapType.equals("whsw")){
            getWHSWTile(req,res);
        }
    }
    
    
    private void getWHSWTile(HttpServletRequest req,HttpServletResponse res){
        //'http://182.254.143.172/fmapimg_chi_day/{z}/R13/C26/{z}-{x}-{y}.png'
        try {
            Map<String,String> map = parseParam(req,"baseUrlSWWH");
            String x = map.get("x");
            String y = map.get("y");
            String z = map.get("z");
            if (useCache.equals("true")) {
                File file = new File(cacheDir+"baseSWWH\\"+z+File.separator+x+File.separator+y+".png");
                if(file.exists() && file.length()>0){
                    FileInputStream fis = new FileInputStream(file);
                    BufferedInputStream fb = new BufferedInputStream(fis);
                    byte[] data = new byte[1024];
                    int c = 0;
                    while ((c=fb.read(data))!=-1){
                        res.getOutputStream().write(data,0,c);
                    }
                    fb.close();
                    res.getOutputStream().flush();
                    res.getOutputStream().close();
                }else {
                    File f = new File(cacheDir+"baseSWWH\\"+z+File.separator+x);
                    if(!f.exists()){
                        f.mkdirs();
                    }
                    File f1 = new File(cacheDir+"baseSWWH\\"+z+File.separator+x+File.separator+y+".png");
                    FileOutputStream fos = new FileOutputStream(f1);
                    BufferedOutputStream fbo = new BufferedOutputStream(fos);
                    String urlstr = "";
                    urlstr = baseUrlMap.get("baseWHSW")+z+"/R13/C26/"+z+"-"+x+"-"+y+".png";
                    URL url = new URL(urlstr);
                    URLConnection urlc = url.openConnection();
                    urlc.setConnectTimeout(3000);
                    urlc.setReadTimeout(100000);
                    res.setContentType(urlc.getContentType());
                    byte[] data = new byte[1024];
                    BufferedInputStream input = new BufferedInputStream(urlc.getInputStream());
                    int c = -1;
                    while ((c=input.read(data))!=-1){
                        res.getOutputStream().write(data,0,c);
                        fbo.write(data,0,c);
                    }
                    fbo.flush();
                    fbo.close();
                    input.close();
                    res.getOutputStream().flush();
                    res.getOutputStream().close();
                }
            }else {
                String urlstr = "";
                urlstr = baseUrlMap.get("baseWHSW")+z+"/R13/C26/"+z+"-"+x+"-"+y+".png";
                URL url = new URL(urlstr);
                URLConnection urlc = url.openConnection();
                urlc.setConnectTimeout(3000);
                urlc.setReadTimeout(100000);
                res.setContentType(urlc.getContentType());
                
                byte[] data = new byte[1024];
                
                BufferedInputStream input = new BufferedInputStream(urlc.getInputStream());
                int c = -1;
                while ((c=input.read(data))!=-1){
                    res.getOutputStream().write(data,0,c);
                }
                res.getOutputStream().flush();
                res.getOutputStream().close();
                input.close();
            }
            
        }catch (UnknownHostException ex) {
            ex.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void getGDTile(HttpServletRequest req,HttpServletResponse res){
        //http://webrd04.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=1&style=7&x={x}&y={y}&z={z}
        try {
            Map<String,String> map = parseParam(req,"baseUrlGD");
            String x = map.get("x");
            String y = map.get("y");
            String z = map.get("z");
            if (useCache.equals("true")) {
                File file = new File(cacheDir+"baseGD\\"+z+File.separator+x+File.separator+y+".png");
                if(file.exists() && file.length()>0){
                    FileInputStream fis = new FileInputStream(file);
                    BufferedInputStream fb = new BufferedInputStream(fis);
                    byte[] data = new byte[1024];
                    int c = 0;
                    while ((c=fb.read(data))!=-1){
                        res.getOutputStream().write(data,0,c);
                    }
                    fb.close();
                    res.getOutputStream().flush();
                    res.getOutputStream().close();
                }else {
                    File f = new File(cacheDir+"baseGD\\"+z+File.separator+x);
                    if(!f.exists()){
                        f.mkdirs();
                    }
                    File f1 = new File(cacheDir+"baseGD\\"+z+File.separator+x+File.separator+y+".png");
                    FileOutputStream fos = new FileOutputStream(f1);
                    BufferedOutputStream fbo = new BufferedOutputStream(fos);
                    String urlstr = "";
                    urlstr = baseUrlMap.get("baseUrlGD")+"&x="+x+"&y="+y+"&z="+z;
                    URL url = new URL(urlstr);
                    URLConnection urlc = url.openConnection();
                    urlc.setConnectTimeout(3000);
                    urlc.setReadTimeout(100000);
                    res.setContentType(urlc.getContentType());
                    byte[] data = new byte[1024];
                    BufferedInputStream input = new BufferedInputStream(urlc.getInputStream());
                    int c = -1;
                    while ((c=input.read(data))!=-1){
                        res.getOutputStream().write(data,0,c);
                        fbo.write(data,0,c);
                    }
                    fbo.flush();
                    fbo.close();
                    input.close();
                    res.getOutputStream().flush();
                    res.getOutputStream().close();
                }
            }else {
                String urlstr = "";
                urlstr = baseUrlMap.get("baseUrlGD")+"&x="+x+"&y="+y+"&z="+z;
                URL url = new URL(urlstr);
                URLConnection urlc = url.openConnection();
                urlc.setConnectTimeout(3000);
                urlc.setReadTimeout(100000);
                res.setContentType(urlc.getContentType());
                
                byte[] data = new byte[1024];
                
                BufferedInputStream input = new BufferedInputStream(urlc.getInputStream());
                int c = -1;
                while ((c=input.read(data))!=-1){
                    res.getOutputStream().write(data,0,c);
                }
                res.getOutputStream().flush();
                res.getOutputStream().close();
                input.close();
            }
            
        }catch (UnknownHostException ex) {
            ex.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
   

    private Map parseParam(HttpServletRequest req,String paramKey){
        Map<String,String> map = new HashMap<String,String>();
        for (String p : paramsMap.get(paramKey)) {
            map.put(p,req.getParameter(p));
        }
        return map;
    }
}
