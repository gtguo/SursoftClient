/**
 * 
 */
package cn.sursoft.minicap;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import cn.sursoft.minicap.AndroidScreenObserver;
import cn.sursoft.util.Adb;
import com.android.ddmlib.IDevice;


public class MinicapTest extends JFrame {
	//private static final Logger LOG = Logger.getLogger("PageTest.class");

	private MyPanel mp = null;
	private IDevice device;
	private int width = 350;
	private int height = 600;
	private Thread thread = null;
	private Socket socket;
	private Banner banner = new Banner();
	private OutputStream outputStream = null;

	public MinicapTest() {
		Adb adb = new Adb();
		if (adb.getDevices().length <= 0) {
			//LOG.error("无连接设备,请检查");
			return;
		}
		device = adb.getDevices()[0];
		mp = new MyPanel(device,this);
		mp.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {

			}

			@Override
			public void mousePressed(MouseEvent e) {

				System.out.println("i press"+e.getX()+","+e.getY());
				Point point = pointConvert(e.getPoint());
				if (outputStream != null) {
					String command = String.format("d 0 %s %s 50\n", (int)point.getX(), (int)point.getY());
					executeTouch(command);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				System.out.println("i release");
				if (outputStream != null) {
					String command =  "u 0\n";
					executeTouch(command);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {

			}
		});


		mp.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {
				System.out.println(e.getPoint().getX()+","+e.getPoint().getY());
				Point point = pointConvert(e.getPoint());
				if (outputStream != null) {
					String command = String.format("m 0 %s %s 50\n", (int)point.getX(), (int)point.getY());
					executeTouch(command);
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {

			}
		});




		this.getContentPane().add(mp);
		this.setSize(width, height);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((dim.width - this.getWidth()) / 2, 0);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {

			}
		});
		this.setVisible(true);
		pack();

	}



	private Point pointConvert(Point point)
	{
		Point realpoint = new Point((int)((point.getX()*1.0 / width) * banner.getMaxX()) , (int)((point.getY()*1.0 /height) * banner.getMaxY()) );
		return realpoint;
	}

	private void executeTouch(String command) {
		if (outputStream != null) {
			try {
				System.out.println("command" + command);
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

	public static void main(String[] args) {
		new MinicapTest();
	}

	class MyPanel extends JPanel implements AndroidScreenObserver {

		BufferedImage image = null;
		MiniCapUtil minicap = null;

		public MyPanel(IDevice device,MinicapTest frame) {
			minicap = new MiniCapUtil(device.getSerialNumber());
			minicap.registerObserver(this);

			minicap.startScreenListener();
			outputStream = minicap.getOutputStream();
			banner = minicap.getBanner();
			/**********************
			try {
				try {
					Thread.sleep(4*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				socket = new Socket("localhost",MiniCapUtil.CURRENTPORT+100);
				InputStream stream = socket.getInputStream();
				outputStream = socket.getOutputStream();
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
			 **************/
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

		public void paint(Graphics g) {
			try {
				if (image == null)
					return;
				MinicapTest.this.setSize(width, height);
				g.drawImage(image, 0, 0, width, height, null);
				System.out.println(width+","+height);
				this.setSize(width,height);
				image.flush();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
		@Override
		public void frameImageChange(byte[] image) {
			//获取image二进制数据来处理转换
			this.image = createImageFromByte(image);
			//this.image = (BufferedImage) b;
			int w = this.image.getWidth();
			int h = this.image.getHeight();
			float radio = (float) width / (float) w;
			height = (int) (radio * h);
			System.out.println("width : " + w + ",height : " + h);
			this.repaint();
		}
	}

}
