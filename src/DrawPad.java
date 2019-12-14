import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DrawPad extends JFrame implements ActionListener,ItemListener {
    private JToolBar button_panel;// 按钮面板
    private JMenuBar menuBar;// 菜单条
    private JMenu file, color, stroke;// 菜单（文件，颜色，编辑）
    private JMenuItem newfile, openfile, savefile, exit;//文件 菜单项
    private JMenuItem colorchoice, cancel,narrow,enlarge;//颜色，编辑，菜单项
    private JLabel startbar;// 状态栏
    private DrawArea drawArea;// 画布
    private JComboBox<String> combox;//字体选择下拉框
    String[] fontname;//字体

    private String[] icon_path = { "src/icons/newfile.png", "src/icons/openfile.png", "src/icons/savefile.png", "src/icons/eraser.png",
            "src/icons/pan.png", "src/icons/line.png", "src/icons/rect.png", "src/icons/frect.png", "src/icons/oval.png",
            "src/icons/foval.png", "src/icons/circle.png", "src/icons/fcircle.png", "src/icons/roundrect.png",
            "src/icons/froundrect.png", "src/icons/color.png", "src/icons/stroke.png", "src/icons/word.png" };
    private Icon[] icons;

    private String Prompt_message[] = { "新建图片", "打开图片", "保存图片", "橡皮擦", "铅笔", "直线", "空心矩形", "实心矩形", "空心椭圆", "实心椭圆",
            "空心圆", "实心圆", "空心圆角矩形", "实心圆角矩形", "颜色", "线条粗细", "文字输入" };
    JButton[] button;// 工具条中的按钮组
    private JButton bold, italic, plain;// 字体格式


    public DrawPad(String string) {
        super(string);

        // 菜单初始化
        file = new JMenu("文件");
        color = new JMenu("颜色");
        stroke = new JMenu("编辑");
        menuBar = new JMenuBar();

        menuBar.add(file);
        menuBar.add(color);
        menuBar.add(stroke);

        this.setJMenuBar(menuBar);
        this.setTitle(string);

        newfile = new JMenuItem("新建");
        openfile = new JMenuItem("打开");
        savefile = new JMenuItem("保存");
        exit = new JMenuItem("退出");

        file.add(newfile);
        file.add(openfile);
        file.add(savefile);
        file.add(exit);

        // 菜单项注册监听
        newfile.addActionListener(this);
        openfile.addActionListener(this);
        savefile.addActionListener(this);
        exit.addActionListener(this);

        colorchoice = new JMenuItem("调色板");
        color.add(colorchoice);
        colorchoice.addActionListener(this);

        cancel = new JMenuItem("撤回");
        stroke.add(cancel);
        cancel.addActionListener(this);

        enlarge = new JMenuItem("放大");
        narrow = new JMenuItem("缩小");
        stroke.add(enlarge);
        stroke.add(narrow);
        enlarge.addActionListener(this);
        narrow.addActionListener(this);

        // 工具栏
        button_panel = new JToolBar("工具箱",JToolBar.HORIZONTAL);
        button_panel.setBackground(Color.LIGHT_GRAY);
        icons = new ImageIcon[icon_path.length];//按钮图标数组
        button = new JButton[icon_path.length];


        for (int i = 0; i < icon_path.length; i++) {
            icons[i] = new ImageIcon(icon_path[i]);
            button[i] = new JButton(icons[i]);
            button[i].setToolTipText(Prompt_message[i]);

            button_panel.add(button[i]);
            button[i].setBackground(Color.white);
            button[i].addActionListener(this);
        }

        bold = new JButton("B");
        bold.setFont(new Font(Font.DIALOG, Font.BOLD, 20));
        bold.addActionListener(this);

        italic = new JButton("I");
        italic.setFont(new Font(Font.DIALOG, Font.ITALIC, 20));
        italic.addActionListener(this);

        plain = new JButton("reset");
        plain.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));
        plain.addActionListener(this);

        GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();// 字体可用名称

        fontname = g.getAvailableFontFamilyNames();
        combox = new JComboBox(fontname);

        combox.addItemListener(this);
        combox.setMaximumSize(new Dimension(70, 40));// 下拉最大尺寸

        button_panel.add(bold);
        button_panel.add(italic);
        button_panel.add(plain);
        button_panel.add(combox);

        startbar = new JLabel("坐标");
        startbar.setFont(new Font("Serief", Font.ITALIC + Font.BOLD, 25));
        startbar.setForeground(Color.black);

        drawArea = new DrawArea(this);

        Container con = getContentPane();
        con.add(button_panel, BorderLayout.NORTH);
        con.add(drawArea, BorderLayout.CENTER);
        con.add(startbar, BorderLayout.SOUTH);

        Toolkit tool = getToolkit();
        Dimension dim = tool.getScreenSize();
        setBounds(100, 100, dim.width - 200, dim.height - 200);
        setVisible(true);
        validate();// 参数校验
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }//界面初始化

    public void setStratBar(String s) {
        startbar.setText(s);
    }//鼠标状态标签栏

    @Override
    public void actionPerformed(ActionEvent e) {
        for (int i = 3; i <= 13; i++) {
            if (e.getSource() == button[i]) {
                drawArea.setCurrentchoice(i);
                drawArea.createNewitem();
                drawArea.repaint();
            }
        }
        if (e.getSource() == newfile || e.getSource() == button[0]) {
            this.newFile();
        } else if (e.getSource() == openfile || e.getSource() == button[1]) {
            this.openFile();
        } else if (e.getSource() == savefile || e.getSource() == button[2]) {
            this.saveFile();
        } else if (e.getSource() == exit) {
            System.exit(0);
        } else if (e.getSource() == colorchoice || e.getSource() == button[14]) {
            drawArea.chooseColor();
        } else if (e.getSource() == button[15]) {
            drawArea.setStroke();
        } else if (e.getSource() == button[16]) {
            drawArea.setCurrentchoice(16);
            drawArea.createNewitem();
            drawArea.repaint();
        }else if (e.getSource()==cancel){
            drawArea.ReturnIndex();
            drawArea.createNewitem();
            drawArea.repaint();
        }else if (e.getSource()==enlarge){
            drawArea.enlarge();
            drawArea.createNewitem();
            drawArea.repaint();

        }else if (e.getSource()==narrow){
            drawArea.narrow();
            drawArea.createNewitem();
            drawArea.repaint();
        }
        if (e.getSource() == bold) {
            drawArea.setFont(1, Font.BOLD);
            drawArea.createNewitem();
        }

        if (e.getSource() == italic) {
            drawArea.setFont(2, Font.ITALIC);
            drawArea.createNewitem();
        }
        if (e.getSource() == plain) {
            drawArea.setFont(3, Font.PLAIN);
            drawArea.createNewitem();
        }
    }//actionlistener接口方法
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == combox) {
            drawArea.setFontName(fontname[combox.getSelectedIndex()]);
        }
    }//itemstatechanged接口方法

    public BufferedImage createImage(DrawArea panel) {
        int width = DrawPad.this.getWidth();
        int height = DrawPad.this.getHeight();
        BufferedImage panelImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2D = panelImage.createGraphics();
        g2D.setColor(Color.white);// 填充背景色
        g2D.fillRect(0, 0, width, height);
        g2D.translate(0, 0);//画笔坐标定义为原点
        panel.paintComponent(g2D);
        g2D.dispose();// 释放部分资源
        return panelImage;
    }//将画板保存成bufferimage
    // 保存画板
    public void saveFile() {
        // 文件选择器
        JFileChooser fileChooser = new JFileChooser();
        // 设置文件显示类型为仅显示文件
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        // 文件过滤器
        JpgFilter jpg = new JpgFilter();
        BmpFilter bmp = new BmpFilter();
        PngFilter png = new PngFilter();
        // 向用户可选择的文件过滤器列表添加一个过滤器
        fileChooser.addChoosableFileFilter(jpg);
        fileChooser.addChoosableFileFilter(bmp);
        fileChooser.addChoosableFileFilter(png);
        // 返回当前的文本过滤器，并设置成当前的选择
        fileChooser.setFileFilter(fileChooser.getFileFilter());
        // 弹出一个 "Save File" 文件选择器对话框
        int result = fileChooser.showSaveDialog(DrawPad.this);
        if (result == JFileChooser.CANCEL_OPTION)
            return;

        File fileName = fileChooser.getSelectedFile();

        if (!fileName.getName().endsWith(fileChooser.getFileFilter().getDescription())) {
            String t = fileName.getPath() + fileChooser.getFileFilter().getDescription();
            fileName = new File(t);
        }
        fileName.canWrite();
        if (fileName == null || fileName.getName().equals(""))
            JOptionPane.showMessageDialog(fileChooser, "无效的文件名", "错误", JOptionPane.ERROR_MESSAGE);

        BufferedImage image = createImage(drawArea);
        try {
            ImageIO.write(image, "png", fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // 打开文件
    public void openFile() {
        // 文件选择器
        JFileChooser fileChooser = new JFileChooser();
        // 设置文件显示类型为仅显示文件
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        // 文件过滤器
        JpgFilter jpg = new JpgFilter();
        BmpFilter bmp = new BmpFilter();
        PngFilter png = new PngFilter();
        // 向用户可选择的文件过滤器列表添加一个过滤器。
        fileChooser.addChoosableFileFilter(jpg);
        fileChooser.addChoosableFileFilter(bmp);
        fileChooser.addChoosableFileFilter(png);
        // 返回当前的文本过滤器，并设置成当前的选择
        fileChooser.setFileFilter(fileChooser.getFileFilter());
        // 弹出一个 "Open File" 文件选择器对话框
        int result = fileChooser.showOpenDialog(DrawPad.this);
        if (result == JFileChooser.CANCEL_OPTION)
            return;

        // 得到选择文件的名字
        File fileName = fileChooser.getSelectedFile();
        if (!fileName.getName().endsWith(fileChooser.getFileFilter().getDescription())) {
            JOptionPane.showMessageDialog(DrawPad.this, "文件格式错误");
            return;
        }
        fileName.canRead();

        if (fileName == null || fileName.getName().equals(""))
            JOptionPane.showMessageDialog(fileChooser, "无效的文件名", "错误", JOptionPane.ERROR_MESSAGE);
        BufferedImage image;

        try {
            drawArea.index = 0;
            image = ImageIO.read(fileName);
            drawArea.image = image;

            drawArea.createNewitem();
            drawArea.repaint();

            drawArea.index++;
            drawArea.setCurrentchoice(4);
            drawArea.createNewitem();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // 新建画板
    public void newFile() {
        drawArea.setIndex(0);
        drawArea.setCurrentchoice(4);
        drawArea.setColor(Color.black);
        drawArea.setStroke(3);
        drawArea.createNewitem();
        repaint();
    }
}

class JpgFilter extends FileFilter {//jpg图片过滤
    public boolean accept(File f) {
        if (f.isDirectory())
            return true;
        return f.getName().endsWith(".jpg");
    }
    public String getDescription() {
        return ".jpg";
    }
}
class BmpFilter extends FileFilter {//bmp图片过滤
    public boolean accept(File f) {
        if (f.isDirectory())
            return true;
        return f.getName().endsWith(".bmp");
    }
    public String getDescription() {
        return ".bmp";
    }
}
class PngFilter extends FileFilter {//png图片过滤
    public boolean accept(File f) {
        if (f.isDirectory())
            return true;
        return f.getName().endsWith(".png");
    }
    public String getDescription() {
        return ".png";
    }
}