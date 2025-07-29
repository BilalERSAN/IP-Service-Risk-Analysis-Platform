package com.appsolute.asset_publisher.model;

public class AssetData {    //JSON İÇERİĞİ BURADA TEMSİL EDİLECEK
    

    private String ip;
    private int port;
    private String service;
    private String version;

    
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public String getService() { return service; }
    public void setService(String service) { this.service = service; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }


}
