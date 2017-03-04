package com.tongtu.gis;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**  
 * 功能描述: 地图代理
 * @author:<a href="mailto:xingjian@tongtusoft.com.cn">邢健</a>  
 * @version: V1.0
 * 日期:2016年6月17日 上午11:21:38  
 */
public class MapProxyServlet extends HttpServlet{
    
    private String[] params_gd= {"request","x","y","z"};
    private String[] params_sw= {"request","x","y","z"};
    private Map<String,String[]> paramsMap = new HashMap<String,String[]>();
    private String cacheDir = "mapofflinestudy\\src\\main\\webapp\\tiles\\";
    private String useCache = "true";
    private Map<String,String> config = new HashMap<String,String>();
    
    public void init() throws ServletException{
        //加载高德地图参数
        paramsMap.put("baseUrlGD", params_gd);
        paramsMap.put("baseUrlSW", params_sw);
        config = ReadPropertiesFile(this.getClass().getResource("/").getPath()+File.separator+"mapconfig.properties");
        cacheDir = config.get("cacheDir");
        useCache = config.get("useCache");
        
    }

    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException,IOException{
        String request = req.getParameter("request");
        String mapType = req.getParameter("mapType");
        if(null!=config.get(mapType)){
            if("getTile".equals(request)&&mapType.subSequence(0, 3).equals("gd_")){
                //北京高德基础地图
                getGDTile(req,res,mapType);
            }else if("getTile".equals(request)&&mapType.subSequence(0, 3).equals("sw_")){
                getSWTile(req,res,mapType);
            }else if("getTile".equals(request)&&mapType.subSequence(0, 4).equals("cus_")){
                getCusTile(req,res,mapType);
            }else if("getVectorTile".equals(request)&&mapType.subSequence(0, 3).equals("vt_")){
                getCusVectorTile(req,res,mapType);
            }
        }
    }
    
    /**
     * 返回矢量地图geojson数据
     * @param req
     * @param res
     * @param mapType
     */
    private void getCusVectorTile(HttpServletRequest req,HttpServletResponse res, String mapType) {
        Map<String,String> map = parseParam(req,"baseUrlGD");
        String x = map.get("x");
        String y = map.get("y");
        String z = map.get("z");
        try{
            File file = new File(cacheDir+mapType+File.separator+z+File.separator+y+File.separator+x+".geojson");
            if(!(file.exists() && file.length()>0)){
                file = new File(cacheDir+mapType+File.separator+"default_nomap.geojson");
            }
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
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 获取自定义切片服务
     * @param req
     * @param res
     * @param mapType
     */
    private void getCusTile(HttpServletRequest req,HttpServletResponse res,String mapType){
        try {
            Map<String,String> map = parseParam(req,"baseUrlGD");
            String x = map.get("x");
            String y = map.get("y");
            String z = map.get("z");
            File file = new File(cacheDir+mapType+File.separator+z+File.separator+y+File.separator+x+".png");
            if(!(file.exists() && file.length()>0)){
                file = new File(cacheDir+mapType+File.separator+"default_nomap.png");
            }
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
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void getGDTile(HttpServletRequest req,HttpServletResponse res,String mapType){
        //http://webrd04.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=1&style=7&x={x}&y={y}&z={z}
        try {
            Map<String,String> map = parseParam(req,"baseUrlGD");
            String x = map.get("x");
            String y = map.get("y");
            String z = map.get("z");
            if (useCache.equals("true")) {
                getTileFile(mapType, req, res, x, y, z);
            }else {
                getDyTileFile(mapType, req, res, x, y, z);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void getSWTile(HttpServletRequest req,HttpServletResponse res,String mapType){
        //'http://182.254.143.172/fmapimg_chi_day/{z}/R13/C26/{z}-{x}-{y}.png'
        try {
            Map<String,String> map = parseParam(req,"baseUrlSW");
            String x = map.get("x");
            String y = map.get("y");
            String z = map.get("z");
            if (useCache.equals("true")) {
                getTileFile(mapType, req, res, x, y, z);
            }else {
                getDyTileFile(mapType, req, res, x, y, z);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static Map<String,String> ReadPropertiesFile(String filePath){
        Map<String,String> result = new HashMap<String,String>();
        Properties prop = new Properties();
        try{
            InputStream in = new BufferedInputStream (new FileInputStream(filePath));
            //解决读取中文问题
            BufferedReader bf = new BufferedReader(new InputStreamReader(in));
            prop.load(bf);
            Iterator<String> it=prop.stringPropertyNames().iterator();
            while(it.hasNext()){
                String key=it.next();
                result.put(key,prop.getProperty(key));
            }
            in.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
    
    public void getTileFile(String maptype,HttpServletRequest req,HttpServletResponse res,String x,String y,String z){
        try {
            File file = new File(cacheDir+maptype+File.separator+z+File.separator+x+File.separator+y+".png");
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
                File f = new File(cacheDir+maptype+File.separator+z+File.separator+x);
                if(!f.exists()){
                    f.mkdirs();
                }
                File f1 = new File(cacheDir+maptype+File.separator+z+File.separator+x+File.separator+y+".png");
                FileOutputStream fos = new FileOutputStream(f1);
                BufferedOutputStream fbo = new BufferedOutputStream(fos);
                String urlstr = "";
                urlstr = config.get(maptype).replace("{x}", x).replace("{y}", y).replace("{z}", z);
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
        }catch(Exception e){
            e.printStackTrace();
        }   
    }
    
    private void getDyTileFile(String maptype,HttpServletRequest req,HttpServletResponse res,String x,String y,String z){
        try{
            String urlstr = "";
            urlstr = config.get(maptype).replace("{x}", x).replace("{y}", y).replace("z", z);
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
        }catch(Exception e){
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
