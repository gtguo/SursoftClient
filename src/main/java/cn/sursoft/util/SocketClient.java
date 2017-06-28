package cn.sursoft.util;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.CollectingOutputReceiver;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.InstallException;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.google.protobuf.InvalidProtocolBufferException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.sursoft.Wire;

import static cn.sursoft.Wire.MessageType.GET_PROPERTIES;

/**
 * Created by gtguo on 5/25/2017.
 */

/*****************
 * adb uninstall jp.co.cyberagent.stf
 * adb install d:/xxx/STFService.apk
 * adb shell am start -n jp.co.cyberagent.stf/.IdentityActivity
 * adb shell am startservice  -n jp.co.cyberagent.stf/.Service
 ************/
class SocketClient {
    private static final String TAG = "Sursoft SocketClient ";
    private static final String cmdStartPgk = "am startservice --user 0 -a jp.co.cyberagent.stf.ACTION_START -n jp.co.cyberagent.stf/.Service";
    //private static final String cmdStartPgk = "am startservice --user -n jp.co.cyberagent.stf/.Service";
    private static final String SocketAddress = "127.0.0.1";
    private static final String STF_APK_PATH = "apk";
    private static final String STF_APK_NAME = "STFService.apk";
    private static final String REMOTE_PATH = "/data/local/tmp/";
    private static final String STF_PKG_NAME = "jp.co.cyberagent.stf";
    private String ROMInfo;
    private String NetWorkInfo;
    private String SIMInfo;
    private String phoneNum;
    private String IMEI;

    private DevPhoneInfo devPhoneInfo = null;
    private  int portID ;
    private String STFApkFile = null;
    private IDevice iDevice = null;
    private MessageWriter messageWriter = null;
    private CreateSocket createSocket = null;

    public SocketClient(IDevice device){
        java.util.Random random = new java.util.Random();
        this.portID = random.nextInt(65535);
        this.iDevice = device;
        init();
    }

    public DevPhoneInfo getDevPhoneInfo(){
        return this.devPhoneInfo;
    }

    private void init(){
        try {
            //iDevice.pushFile(getWindowsStfPath(), REMOTE_PATH + STF_APK_NAME);
            //startStfServie();
            //System.out.println(System.currentTimeMillis());
            iDevice.uninstallPackage(STF_PKG_NAME);
            iDevice.installPackage(getWindowsStfPath(),true);
            //System.out.println(System.currentTimeMillis());
            startStfServie();
            iDevice.createForward(portID, "stfservice",
                    IDevice.DeviceUnixSocketNamespace.ABSTRACT);
            socketConnect();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (AdbCommandRejectedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InstallException e) {
            e.printStackTrace();
        }
    }

    private void getUbuntuStfPath(){

    }
    /**
     * 获取系统apk路径
     * @return
     */
    private String getWindowsStfPath(){
        if (STFApkFile == null){
            STFApkFile = System.getProperty("user.dir");
            System.out.println(TAG + STFApkFile);
            if(STFApkFile != null){
                STFApkFile += File.separator + STF_APK_PATH;
                System.out.println(STFApkFile);
            }else {
                return null;
            }
        }
        STFApkFile += File.separator + STF_APK_NAME;
        System.out.println(STFApkFile);
        return STFApkFile;
    }

    //Connect socket
    private void socketConnect() throws InterruptedException {
        Socket socket = null;
        try{
            socket = new Socket(SocketAddress,portID);
        }catch (IOException e){
            e.printStackTrace();
        }

        createSocket = new CreateSocket(socket);
        createSocket.start();
       // createSocket.join();
       // Thread.sleep(1000);
       // createSocket.interrupt();
    }

    private void socketClose(){
        createSocket.interrupt();
    }
    class CreateSocket extends Thread{
        private Socket socket;
        public CreateSocket(Socket st){
            this.socket = st;
        }

        @Override
        public void interrupt(){
            super.interrupt();
            try{
                if(socket.isConnected()) {
                    socket.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        private void handleGetProperties(Wire.Envelope envelope) throws InvalidProtocolBufferException {

            Wire.GetPropertiesResponse propertiesResponse = Wire.GetPropertiesResponse.parseFrom(envelope.getMessage());
            System.out.println(TAG+"Success:"+propertiesResponse.getSuccess());
            for (Wire.Property property : propertiesResponse.getPropertiesList()){
                if(property.getName().equals("network")){
                    SocketClient.this.NetWorkInfo = property.getValue();
                }else if(property.getName().equals("operator")){
                    SocketClient.this.SIMInfo = property.getValue();
                }else if(property.getName().equals("phoneNumber")){
                    SocketClient.this.phoneNum = property.getValue();
                }else if(property.getName().equals("imei")){
                    SocketClient.this.IMEI = property.getValue();
                }else if(property.getName().equals("rom")){
                    SocketClient.this.ROMInfo = property.getValue();
                }
            }
            interrupt();
        }
        @Override
        public void run(){
            System.out.println(TAG + "Socket running....");
            try{
                messageWriter = new MessageWriter(socket.getOutputStream());
                {
                    devPhoneInfo = new DevPhoneInfo();
                    messageWriter.write(devPhoneInfo.getPropertiesEnvelope());
                }

                while(!isInterrupted()){
                    ////socket read message
                    InputStream in = null;
                    if(socket.isConnected()){
                        in = socket.getInputStream();
                    }else {
                        break;
                    }
                    Wire.Envelope envelope = new MessageReader(in).read();
                    if(envelope == null){
                        break;
                    }

                    switch (envelope.getType()){
                        case EVENT_BATTERY:
                            //handleEventBattery(envelope);
                            break;
                        case EVENT_SRR_STATUS:
                            //handleEventGetSrrStatus(envelope);
                            break;
                        case EVENT_CONNECTIVITY:
                            //handleEventConnect(envelope);
                            break;
                        case EVENT_ROTATION:
                            //handleEventRotation(envelope);
                            break;
                        case EVENT_AIRPLANE_MODE:
                            //handleEventAirplaneMode(envelope);
                            break;
                        case EVENT_BROWSER_PACKAGE:
                            //handleEventBrowserPackage(envelope);
                            break;
                        case EVENT_PHONE_STATE:
                            //handleEventPhoneMode(envelope);
                            break;
                        case DO_IDENTIFY:
                            //handleEventIdentify(envelope);
                            break;
                        case GET_CLIPBOARD:
                            //handleGetClipboard(envelope);
                            break;
                        case GET_PROPERTIES:
                            handleGetProperties(envelope);
                            break;
                        case GET_VERSION:
                            //handleEventGetVision(envelope);
                            break;
                        case GET_DISPLAY:
                            //handleEventGetDisplay(envelope);
                            break;
                        case GET_WIFI_STATUS:
                            //handleEventGetWifiStatus(envelope);
                            break;
                        case GET_SD_STATUS:
                            //handleEventGetSdcardStatus(envelope);
                            break;
                        default:
                            System.out.println(TAG + "Unknowing eventType:" + envelope.getType());
                            break;
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }finally {
                try{
                    socket.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void startStfServie(){
        CollectingOutputReceiver output = new CollectingOutputReceiver();
        try {
            iDevice.executeShellCommand(cmdStartPgk,output);
            Thread.sleep(200);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (AdbCommandRejectedException e) {
            e.printStackTrace();
        } catch (ShellCommandUnresponsiveException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getNetWorkInfo(){return this.NetWorkInfo;}
    public String getSIMInfo(){return this.SIMInfo;}
    public String getROMInfo(){return this.ROMInfo;}
    public String getPhoneNum(){return this.phoneNum;}
    public String getIMEI(){return this.IMEI;}
}
