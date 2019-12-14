import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

public class DrawArea extends JPanel {
    DrawPad drawPad;
    Shape[] list = new Shape[50000];//图形数组
    int index = 0;//绘制图形数目索引
    int R,G,B;//彩色值
    float stroke = 1.0f;//默认笔画粗细
    int bold,italic;//字体风格
    BufferedImage image;//背景图像存储
    private int currentchoice = 4;//当前状态pan。铅笔
    private Color col=Color.BLACK;//默认笔画颜色

    DrawArea(DrawPad dp){//构造函数
        this.drawPad = dp;
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));//设置光标颜色
        setBackground(Color.WHITE);
        addMouseListener(new MouseA());
        addMouseMotionListener(new MouseB());
        createNewitem();
    }//构造函数
    void draw(Graphics2D g2b,Shape i){
        i.draw(g2b);
    }//画笔传到每个类
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;

        int j = 0;
        while (j<=index){
            if (j==0){
                g.drawImage(image,0,0,this);//底图为打开的图片
            }
            draw(g2d,list[j]);
            j++;
        }
    }//绘制此容器中的每个组件
    void createNewitem(){
        if(currentchoice==16)//文本输入
            setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        else
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        switch (currentchoice){
            case 3:list[index] = new Eraser();break;
            case 4:list[index] = new pencil();break;
            case 5:list[index] = new Line();break;
            case 6:list[index] = new Rect();break;
            case 7:list[index] = new fillRect();break;
            case 8:list[index] = new Oval();break;
            case 9:list[index] = new fillOval();break;
            case 10:list[index] = new Circle();break;
            case 11:list[index] = new fillCircle();break;
            case 12:list[index] = new RoundRect();break;
            case 13:list[index] = new fillRoundRect();break;
            case 16:list[index] = new word();break;
        }
        list[index].R = R;
        list[index].G = G;
        list[index].B = B;
        list[index].stroke = stroke;
        list[index].bold=bold;
        list[index].italic=italic;
    }
    public void setIndex(int x){
        index = x;
    }//修改图形数组索引
    public void setColor(Color col){
        this.col=col;
    }//修改画板默认颜色
    public void setStroke(float f){
        this.stroke = f;
    }//设置画笔粗细
    public void chooseColor(){
        col = JColorChooser.showDialog(this,"请选择颜色",col);//选择颜色
        try{
            R = col.getRed();
            G = col.getGreen();
            B = col.getBlue();
        } catch (Exception e){
            list[index].R = 0;
            list[index].G = 0;
            list[index].B = 0;
        }
        list[index].R = R;
        list[index].G = G;
        list[index].B = B;
    }//颜色选择器
    public void setStroke(){
        String intput;
        intput = JOptionPane.showInputDialog("请输入画笔粗细");//输入框
        try{
            stroke = Float.parseFloat(intput);//分析数字
        }catch (Exception e){
            stroke = 1.0f;
        }
        list[index].stroke = stroke;
    }//使用键盘输入画笔粗细
    public void setCurrentchoice(int i){
        currentchoice = i;
    }//设置当前画板画笔状态
    public void setFont(int i,int font){
        if (i==1){
            bold=font;
        }else if (i==2){
            italic=font;
        }else {
            bold=italic=Font.PLAIN;
        }

    }//设置字体风格
    public void setFontName(String name) {
        list[index].font_familyName = name;
    }//选择字体
    public void ReturnIndex(){
        if (index>0){
            this.index--;
        }

    }//撤回
    public void enlarge(){
        for (int j = 0;j<=index;j++){
            list[j].x1=list[j].x1*2;
            list[j].y1=list[j].y1*2;
            list[j].x2=list[j].x2*2;
            list[j].y2=list[j].y2*2;
            list[j].stroke=list[j].stroke*2;
        }
    }//放大
    public void narrow(){
        for (int j = 0;j<=index;j++){
            list[j].x1=list[j].x1/2;
            list[j].y1=list[j].y1/2;
            list[j].x2=list[j].x2/2;
            list[j].y2=list[j].y2/2;
            list[j].stroke=list[j].stroke/2;
        }
    }//缩小

    class MouseA extends MouseAdapter{
        @Override
        public void mouseEntered(MouseEvent e) {
            super.mouseEntered(e);
            drawPad.setStratBar("鼠标进入在：["+e.getX()+","+e.getY()+"]");
        }

        @Override
        public void mouseExited(MouseEvent e) {
            super.mouseExited(e);
            drawPad.setStratBar("鼠标退出在：["+e.getX()+","+e.getY()+"]");
        }

        @Override
        public void mousePressed(MouseEvent e) {
            super.mousePressed(e);
            drawPad.setStratBar("鼠标按住在：["+e.getX()+","+e.getY()+"]");
            list[index].x1 = list[index].x2 = e.getX();
            list[index].y1 = list[index].y2 = e.getY();

            //Eraser pan
            if (currentchoice == 3||currentchoice == 4){
                list[index].x1 = list[index].x2 = e.getX();
                list[index].y1 = list[index].y2 = e.getY();
                index++;
                createNewitem();
            }
            if(currentchoice == 16){//文字
                list[index].x1 = e.getX();
                list[index].y1 = e.getY();
                String input ;
                input = JOptionPane.showInputDialog("请输入你要写入的文字！");
                list[index].word = input;
                index++;
                currentchoice = 16;
                createNewitem();//创建新的图形的基本单元对象
                repaint();//重绘组件
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            super.mouseReleased(e);
            drawPad.setStratBar("鼠标松开：["+e.getX()+","+e.getY()+"]");
            if (currentchoice==3||currentchoice==4){
                list[index].x1 = e.getX();
                list[index].y1 = e.getY();
            }
            list[index].x2 = e.getX();
            list[index].y2 = e.getY();
            repaint();
            index++;
            createNewitem();
        }
    }//鼠标监听类
    class  MouseB extends MouseMotionAdapter{
        @Override
        public void mouseDragged(MouseEvent e) {
            super.mouseDragged(e);
            drawPad.setStratBar("鼠标拖动在：["+e.getX()+","+e.getY()+"]");
            if (currentchoice==3||currentchoice==4){
                list[index-1].x1=list[index].x2 = list[index].x1=e.getX();
                list[index-1].y1=list[index].y2 = list[index].y1=e.getY();
                index++;
                createNewitem();
            }else {
                list[index].x2 = e.getX();
                list[index].y2 = e.getY();
            }
            repaint();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            super.mouseMoved(e);
            drawPad.setStratBar("鼠标移动在：["+e.getX()+","+e.getY()+"]");
        }
    }
}
