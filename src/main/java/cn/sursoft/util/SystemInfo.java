package cn.sursoft.util;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Properties;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.net.InetAddress;
import java.net.Inet6Address;
/**
 * Created by gtguo on 5/16/2017.
 */

public class SystemInfo {
    private String OS;
    private String IpAddress;
    private String ComputerName;
    private String UserDomain;

    public SystemInfo(){
        //this.propertis = System.getProperties();
        this.OS = System.getProperty("os.name");
        this.ComputerName = System.getenv().get("COMPUTERNAME");
        this.UserDomain = System.getenv().get("USERDOMAIN");

    }

    public String getOS(){
        return this.OS;
    }

    private void setUbuntuIpAddress(){
        Enumeration<NetworkInterface> interfaces = null;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                if (!ni.getName().equals("eth0")) {
                    continue;
                }else {
                    Enumeration<InetAddress> addresss = ni.getInetAddresses();
                    while (addresss.hasMoreElements()) {
                        InetAddress nextElement = addresss.nextElement();
                        if (nextElement instanceof Inet6Address) {
                            continue;
                        }else {
                            this.IpAddress = nextElement.getHostAddress();
                            System.out.println("ip:"+this.IpAddress);
                        }
                    }
                }
            }
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }
    public String getIpAddress(){
        if (getOS() != null && getOS().startsWith("Windows")) {
            try {
                String ip = InetAddress.getLocalHost().toString();
                int index = ip.indexOf("/");
                this.IpAddress = ip.substring(index+1);
                System.out.println("ip:"+this.IpAddress);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }else{
            setUbuntuIpAddress();
        }
        return this.IpAddress;
    }

    public String getComputerName(){
        return this.ComputerName;
    }

    public String getUserDomain(){
        return this.UserDomain;
    }

    public static void main(String[] args){
        SystemInfo s = new SystemInfo();

        System.out.println(s.getComputerName());
        System.out.println(s.getOS());
        System.out.println(s.getIpAddress());
        System.out.println(s.getUserDomain());
        System.out.println(System.getProperty("user.dir")
                +System.getProperty("file.separator")
                +System.getProperty("path.separator"));
        Map<String,String> env = System.getenv();
        /***************************
        for (String key:env.keySet()){
            System.out.println(key+":"+env.get(key));
        }
         ***************************/
    }
}
