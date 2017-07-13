package cn.sursoft.minicap;

/**
 * Created by gtguo on 7/13/2017.
 */

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.android.ddmlib.IDevice;

import cn.sursoft.util.Adb;


public class MinicapTest extends JFrame {
    private static final String TAG = "Sursoft MinicapTest ";

    private MyPanel mp = null;
    private IDevice device;
    private int width = 300;
    private int height = 500;
    private Thread thread = null;

    public MinicapTest() {
        Adb adb = new Adb();
        if (adb.getDevices().length <= 0) {
            System.out.println(TAG + "无连接设备,请检查");
            return;
        }
        device = adb.getDevices()[0];
        mp = new MyPanel(device,this);
        this.getContentPane().add(mp);
        this.setSize(300, height);
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

    public static void main(String[] args) {
        new MinicapTest();
    }

    class MyPanel extends JPanel implements AndroidScreenObserver {

        BufferedImage image = null;
        MiniCapUtil minicap = null;

        public MyPanel(IDevice device,MinicapTest frame) {
            minicap = new MiniCapUtil(device);
            minicap.registerObserver(this);
            minicap.startScreenListener();

        }

        public void paint(Graphics g) {
            try {
                if (image == null)
                    return;
                MinicapTest.this.setSize(width, height);
                g.drawImage(image, 0, 0, width, height, null);
                this.setSize(300, height + 300);
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