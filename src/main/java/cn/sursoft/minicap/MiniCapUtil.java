/**
 *
 */
package cn.sursoft.minicap;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.imageio.ImageIO;

import cn.sursoft.minicap.Constant;
import cn.sursoft.minicap.TimeUtil;

import cn.sursoft.util.SingletonMinitorIDevice;
import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.CollectingOutputReceiver;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.IDevice.DeviceUnixSocketNamespace;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.SyncException;
import com.android.ddmlib.TimeoutException;

/**
 * @author hui.qian qianhui@58.com
 * @date 2015年8月12日 上午11:02:53
 */
public class MiniCapUtil implements ScreenSubject {
	// CPU架构的种类
	public static final String ABIS_ARM64_V8A = "arm64-v8a";
	public static final String ABIS_ARMEABI_V7A = "armeabi-v7a";
	public static final String ABIS_X86 = "x86";
	public static final String ABIS_X86_64 = "x86_64";

	private Queue<byte[]> dataQueue = new LinkedBlockingQueue<byte[]>();
	private List<AndroidScreenObserver> observers = new ArrayList<AndroidScreenObserver>();

	private Banner banner = new Banner();
	private static final int PORT = 1717;
	private static final int PORT2 = 1111;
	private static final int MAXPORT = 1732;
	public static int CURRENTPORT = 1716;
	private Socket socket;
	private Socket minitouchSocket = null;
	private IDevice device;
	private String REMOTE_PATH = "/data/local/tmp/";
	private String ABI_COMMAND = "ro.product.cpu.abi";
	private String SDK_COMMAND = "ro.build.version.sdk";
	private String MINICAP_BIN = "minicap";
	private String MINITOUCH_BIN = "minitouch";
	private String MINICAP_NOPIE = "minicap-nopie";
	private String MINICAP_FILE = "";
	private String MINITOUCH_NOPIE = "minitouch-nopie";
	private String MINITOUCH_FILE = "";

	private String MINICAP_SO = "minicap.so";
	private String MINICAP_CHMOD_COMMAND = "chmod 777 %s/%s";
	private String MINICAP_WM_SIZE_COMMAND = "wm size";
	private String MINICAP_START_COMMAND = "LD_LIBRARY_PATH=/data/local/tmp /data/local/tmp/%s -P %s@%s/0";
	private String MINITOUCH_START_COMMAND = "/data/local/tmp/%s";
	private String MINICAP_TAKESCREENSHOT_COMMAND = "LD_LIBRARY_PATH=/data/local/tmp /data/local/tmp/%s -P %s@%s/0 -s >%s";
	private String ADB_PULL_COMMAND = "adb -s %s pull %s %s";


	private boolean isRunning = false;
	private String size;
	private String serialNum;
	private OutputStream outputStream = null;

	public MiniCapUtil(String serialId) {
		this.serialNum = serialId;
		this.device = SingletonMinitorIDevice.getInstance().getIDeviceBySerialNum(serialId);
		init();
	}
	//判断是否支持minicap
	public boolean isSupoort(){
		String supportCommand = String.format("LD_LIBRARY_PATH=/data/local/tmp /data/local/tmp/minicap -P %s@%s/0 -t", size,size);
		String output = executeShellCommand(supportCommand);
		if(output.trim().endsWith("OK")){
			return true;
		}
		return false;
	}

	/**
	 * 将minicap的二进制和.so文件push到/data/local/tmp文件夹下，启动minicap服务
	 */
	private void init() {

		String abi = null;
		while (abi==null){
			abi = device.getProperty(ABI_COMMAND);
			System.out.println(abi);
		}

		String sdk = device.getProperty(SDK_COMMAND);
		if (Integer.valueOf(sdk) < 16) {
			MINITOUCH_FILE = MINITOUCH_NOPIE;
			MINICAP_FILE = MINICAP_NOPIE;
		}else {
			MINITOUCH_FILE = MINITOUCH_BIN;
			MINICAP_FILE = MINICAP_BIN;
		}
		System.out.println("获取abi 信息以及系统的sdk为abi:"+abi+"sdk:"+sdk);
		File minicapBinFile = new File(Constant.getMinicapBin(), abi
				+ File.separator + MINICAP_FILE);
		File minitouchBinFile = new File(Constant.getMiniTouchBin(), abi
				+ File.separator + MINITOUCH_FILE);
		File minicapSoFile = new File(Constant.getMinicapSo(), "android-" + sdk
				+ File.separator + abi + File.separator + MINICAP_SO);
		System.out.println(minicapSoFile.getAbsolutePath());
		try {
			// 将minicap的可执行文件和.so文件一起push到设备中
			device.pushFile(minicapBinFile.getAbsolutePath(), REMOTE_PATH
					 + MINICAP_FILE);
			device.pushFile(minitouchBinFile.getAbsolutePath(), REMOTE_PATH
					 + MINITOUCH_FILE);
			device.pushFile(minicapSoFile.getAbsolutePath(), REMOTE_PATH
					 + MINICAP_SO);
			executeShellCommand(String.format(MINICAP_CHMOD_COMMAND,
					REMOTE_PATH, MINICAP_FILE));
			executeShellCommand(String.format(MINICAP_CHMOD_COMMAND,
					REMOTE_PATH, MINITOUCH_FILE));
			if (CURRENTPORT < MAXPORT) {
				CURRENTPORT++;
			}
			// 端口转发
			device.createForward(CURRENTPORT, "minicap",
					DeviceUnixSocketNamespace.ABSTRACT);

			// 端口转发
			device.createForward(CURRENTPORT+100, "minitouch",
					DeviceUnixSocketNamespace.ABSTRACT);

			// 获取设备屏幕的尺寸
			String output = executeShellCommand(MINICAP_WM_SIZE_COMMAND);
			size = output.split(":")[1].trim();
		} catch (SyncException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AdbCommandRejectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public OutputStream getOutputStream(){
		return this.outputStream;
	}

	public Banner getBanner(){
		return this.banner;
	}
	public void takeScreenShotOnce() {
		String savePath = "/data/local/tmp/screenshot.jpg";
		String takeScreenShotCommand = String.format(
				MINICAP_TAKESCREENSHOT_COMMAND, MINICAP_FILE,size,
				size, savePath);
		String localPath = System.getProperty("user.dir") + "/screenshot.jpg";
		String pullCommand = String.format(ADB_PULL_COMMAND,
				device.getSerialNumber(), savePath, localPath);
		try {
			// Process process =
			// Runtime.getRuntime().exec(takeScreenShotCommand);
			executeShellCommand(takeScreenShotCommand);
			device.pullFile(savePath, localPath);
			// BufferedReader br = new BufferedReader(new InputStreamReader(
			// process.getInputStream()));
			// String line = null;
			// while ((line = br.readLine()) != null) {
			// LOG.debug(line);
			// }
			// Thread.sleep(200);
			// process.waitFor();
			Runtime.getRuntime().exec(pullCommand);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SyncException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AdbCommandRejectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv + " ");
		}
		return stringBuilder.toString();
	}

	private String executeShellCommand(String command) {
		CollectingOutputReceiver output = new CollectingOutputReceiver();
		try {
			device.executeShellCommand(command, output, 0);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AdbCommandRejectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ShellCommandUnresponsiveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output.getOutput();
	}

	public void startScreenListener() {
		isRunning = true;
		Thread frame = new Thread(new ImageBinaryFrameCollector());
		frame.start();
		Thread convert = new Thread(new ImageConverter());
		convert.start();
		Thread touch = new Thread(new TouchThread());
		touch.start();
		try {
			try {
				Thread.sleep(4*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			minitouchSocket = new Socket("localhost",MiniCapUtil.CURRENTPORT+100);

			InputStream stream = minitouchSocket.getInputStream();
			outputStream = minitouchSocket.getOutputStream();
			int len = 4096;

			byte[] buffer;
			buffer = new byte[len];
			int realLen = stream.read(buffer);
			if (buffer.length != realLen) {
				buffer = subByteArray(buffer, 0, realLen);
			}
			String result = new String(buffer);
			String array[] = result.split(" |\n");
			System.out.println("hhe");
			banner.setVersion(Integer.valueOf(array[1]));
			banner.setMaxPoint(Integer.valueOf(array[3]));
			banner.setMaxPress(Integer.valueOf(array[6]));
			banner.setMaxX(Integer.valueOf(array[4]));
			banner.setMaxY(Integer.valueOf(array[5]));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stopScreenListener() {
		isRunning = false;
	}
	public void miniTouchPress(int x,int y){
		System.out.println("miniTouchPress");
		if (outputStream != null) {
			String command = String.format("d 0 %s %s 50\n", x,y);
			System.out.println("miniTouchPress command:"+command);
			executeTouch(command);
		}
	}

	public void miniTouchRelease(){
		if (outputStream != null) {
			String command =  "u 0\n";
			executeTouch(command);
		}
	}

	public void miniTouchMove(int x,int y){
		if (outputStream != null) {
			String command = String.format("m 0 %s %s 50\n", x,y);
			executeTouch(command);
		}
	}

	private void executeTouch(String command) {
		if (outputStream != null) {
			try {
				System.out.println("command:" + command);
				outputStream.write(command.getBytes());
				outputStream.flush();
				String endCommand = "c\n";
				outputStream.write(endCommand.getBytes());
				outputStream.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private BufferedImage createImageFromByte(byte[] binaryData) {
		BufferedImage bufferedImage = null;
		InputStream in = new ByteArrayInputStream(binaryData);
		try {
			bufferedImage = ImageIO.read(in);
			if (bufferedImage == null) {
				//LOG.debug("bufferimage为空");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return bufferedImage;
	}

	private Image createImage(byte[] data) {
		Image image = Toolkit.getDefaultToolkit().createImage(data);
		//LOG.info("创建成功");
		return image;
	}

	// java合并两个byte数组
	private static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
		byte[] byte_3 = new byte[byte_1.length + byte_2.length];
		System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
		System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
		return byte_3;
	}

	private byte[] subByteArray(byte[] byte1, int start, int end) {
		byte[] byte2 = new byte[0];
		try {
			byte2 = new byte[end - start];
		} catch (NegativeArraySizeException e) {
			e.printStackTrace();
		}
		System.arraycopy(byte1, start, byte2, 0, end - start);
		return byte2;
	}

	class ImageBinaryFrameCollector implements Runnable {
		private InputStream stream = null;

		// private DataInputStream input = null;

		public void run() {
			//LOG.debug("图片二进制数据收集器已经开启");
			try {

				final String startCommand = String.format(
						MINICAP_START_COMMAND, MINICAP_FILE,size, size);
				// 启动minicap服务
				new Thread(new Runnable() {
					public void run() {
						//LOG.info("minicap服务器启动 : " + startCommand);
						executeShellCommand(startCommand);

					}
				}).start();
				try {
					Thread.sleep(2 * 1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				socket = new Socket("localhost", CURRENTPORT);
				stream = socket.getInputStream();
				// input = new DataInputStream(stream);
				int len = 4096;
				while (isRunning) {
					byte[] buffer;
					buffer = new byte[len];
					int realLen = stream.read(buffer);
					if (buffer.length != realLen ) {
						buffer = subByteArray(buffer, 0, realLen);

					}
					dataQueue.add(buffer);

				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (socket != null && socket.isConnected()) {
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (stream != null) {
					try {
						stream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			//LOG.debug("图片二进制数据收集器已关闭");
		}

	}


	class TouchThread implements Runnable {
		@Override
		public void run() {
			String startCmd = String.format(MINITOUCH_START_COMMAND,MINITOUCH_FILE);
			System.out.println(startCmd);
			executeShellCommand(startCmd);
		}
	}


	class ImageConverter implements Runnable {
		private int readBannerBytes = 0;
		private int bannerLength = 2;
		private int readFrameBytes = 0;
		private int frameBodyLength = 0;
		private byte[] frameBody = new byte[0];

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			// TODO Auto-generated method stub
			long start = System.currentTimeMillis();
			while (isRunning) {
				if (dataQueue.isEmpty()) {
					// LOG.info("数据队列为空");
					continue;
				}
				byte[] buffer = dataQueue.poll();
				int len = buffer.length;
				for (int cursor = 0; cursor < len;) {
					int byte10 = buffer[cursor] & 0xff;
					if (readBannerBytes < bannerLength) {
						cursor = parserBanner(cursor, byte10);
					} else if (readFrameBytes < 4) {
						// 第二次的缓冲区中前4位数字和为frame的缓冲区大小
						frameBodyLength += (byte10 << (readFrameBytes * 8)) >>> 0;
						cursor += 1;
						readFrameBytes += 1;
						// LOG.debug("解析图片大小 = " + readFrameBytes);
					} else {
						if (len - cursor >= frameBodyLength) {
							//LOG.debug("frameBodyLength = " + frameBodyLength);
							byte[] subByte = subByteArray(buffer, cursor,
									cursor + frameBodyLength);
							frameBody = byteMerger(frameBody, subByte);
							if ((frameBody[0] != -1) || frameBody[1] != -40) {
								//LOG.error(String
								//		.format("Frame body does not start with JPG header"));
								return;
							}
							final byte[] finalBytes = subByteArray(frameBody,
									0, frameBody.length);
							// 转化成bufferImage
							new Thread(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									//Image image = createImageFromByte(finalBytes);
									notifyObservers(finalBytes);
								}
							}).start();

							long current = System.currentTimeMillis();

							start = current;
							cursor += frameBodyLength;
							restore();
						} else {

							byte[] subByte = subByteArray(buffer, cursor, len);
							frameBody = byteMerger(frameBody, subByte);
							frameBodyLength -= (len - cursor);
							readFrameBytes += (len - cursor);
							cursor = len;
						}
					}
				}
			}

		}

		private void restore() {
			frameBodyLength = 0;
			readFrameBytes = 0;
			frameBody = new byte[0];
		}

		private int parserBanner(int cursor, int byte10) {
			switch (readBannerBytes) {
				case 0:
					// version
					banner.setVersion(byte10);
					break;
				case 1:
					// length
					bannerLength = byte10;
					banner.setLength(byte10);
					break;
				case 2:
				case 3:
				case 4:
				case 5:
					// pid
					int pid = banner.getPid();
					pid += (byte10 << ((readBannerBytes - 2) * 8)) >>> 0;
					banner.setPid(pid);
					break;
				case 6:
				case 7:
				case 8:
				case 9:
					// real width
					int realWidth = banner.getReadWidth();
					System.out.println("realwidth0"+realWidth);
					realWidth += (byte10 << ((readBannerBytes - 6) * 8)) >>> 0;
					System.out.println("realwidth1"+realWidth);
					banner.setReadWidth(realWidth);
					break;
				case 10:
				case 11:
				case 12:
				case 13:
					// real height
					int realHeight = banner.getReadHeight();
					realHeight += (byte10 << ((readBannerBytes - 10) * 8)) >>> 0;
					banner.setReadHeight(realHeight);
					break;
				case 14:
				case 15:
				case 16:
				case 17:
					// virtual width
					int virtualWidth = banner.getVirtualWidth();
					virtualWidth += (byte10 << ((readBannerBytes - 14) * 8)) >>> 0;
					banner.setVirtualWidth(virtualWidth);
					System.out.println("virtual"+virtualWidth);
					break;
				case 18:
				case 19:
				case 20:
				case 21:
					// virtual height
					int virtualHeight = banner.getVirtualHeight();
					virtualHeight += (byte10 << ((readBannerBytes - 18) * 8)) >>> 0;
					banner.setVirtualHeight(virtualHeight);
					System.out.println("virtualhegith"+virtualHeight);
					break;
				case 22:
					// orientation
					banner.setOrientation(byte10 * 90);
					break;
				case 23:
					// quirks
					banner.setQuirks(byte10);
					break;
			}

			cursor += 1;
			readBannerBytes += 1;

			if (readBannerBytes == bannerLength) {

			}
			return cursor;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wuba.utils.screenshot.ScreenSubject#registerObserver(com.wuba.utils
	 * .screenshot.AndroidScreenObserver)
	 */
	public void registerObserver(AndroidScreenObserver o) {
		// TODO Auto-generated method stub
		observers.add(o);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wuba.utils.screenshot.ScreenSubject#removeObserver(com.wuba.utils
	 * .screenshot.AndroidScreenObserver)
	 */
	public void removeObserver(AndroidScreenObserver o) {
		// TODO Auto-generated method stub
		int index = observers.indexOf(o);
		if (index != -1) {
			observers.remove(o);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wuba.minicap.ScreenSubject#notifyObservers(java.awt.Image)
	 */
	@Override
	public void notifyObservers(byte[] image) {
		for (AndroidScreenObserver observer : observers) {
			observer.frameImageChange(image);
		}
		// TODO Auto-generated method stub

	}
}
