package test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.TreeSet;

import javax.swing.JPanel;

/*import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;*/

public class ResidualAnal extends JPanel{
	double[] prey;
	double [] residual;
	double [] stdres;
	double []y;
	double [] cooksd;
	double[] prey2;
	double [] residual2;
	double [] stdres2;
	double []y2;
	double [] cooksd2;
	String[] namey;
	double [][] xminmax;
	double minprey , maxprey , minres , maxres ;
	double minstdres , maxstdres , miny , maxy;
	double mincooksd , maxcooksd ;
	int wd = 900;
	int ht = 600;
	int m = 50;
	int r = 40;
	int ptsize = 5 ;
	int n; int p; int ht1; int wd1;
	double [][] A;
	double midprey;
	public ResidualAnal(double [] prey, double [] residual,  double [] stdres, double []y, double [] cooksd, String[] namey, int n){
		//예측값대 잔차, 표준화잔차, 관측값 , cooks'd 그래프를 띄움
		//분산 분석표 객체를 불러와서 객체의 예측값, 잔차 , 표준화 잔차 , 관측값, cooks'd 값을 가져옴
		this.prey = prey; this.residual = residual ;
		this.stdres =stdres; this.y = y;
		this.cooksd = cooksd;this.namey = namey; this.n = n;
		prey2 = new double[n];
		residual2 = new double[n];
		stdres2 = new double[n];
		y2 = new double[n];
		cooksd2 = new double[n];
		for(int i =0 ; i < n ; i++){
			prey2[i] = prey[i];
			residual2[i] = residual[i];
			stdres2[i] = stdres[i];
			y2[i] = y[i];
			cooksd2[i] = cooksd[i];
		}
		
		for(int i=0; i<n; i++){
			for(int j=i+1; j< n; j++){
				double k = residual2[i];
				if(residual2[j]<residual2[i]){
					residual2[i] = residual2[j];
					residual2[j] = k;
				}
			}
			for(int j=i+1; j< n; j++){
				double k = stdres2[i];
				if(stdres2[j]<stdres2[i]){
					stdres2[i] = stdres2[j];
					stdres2[j] = k;
				}
			}
			for(int j=i+1; j< n; j++){
				double k = y2[i];
				if(y2[j]<y2[i]){
					y2[i] = y2[j];
					y2[j] = k;
				}
			}
			for(int j=i+1; j< n; j++){
				double k = cooksd2[i];
				if(cooksd2[j]<cooksd2[i]){
					cooksd2[i] = cooksd2[j];
					cooksd2[j] = k;
				}
			}
			for(int j=i+1; j< n; j++){
				double k = prey2[i];
				if(prey2[j]<prey2[i]){
					prey2[i] = prey2[j];
					prey2[j] = k;
				}
			}
		}
		minprey = prey2[0]; maxprey=prey2[n-1];
		minres = residual2[0]; maxres = residual2[n-1];
		minstdres = stdres2[0]; maxstdres = stdres2[n-1];
		miny = y2[0];  maxy = y2[n-1];
		mincooksd = cooksd2[0];  maxcooksd = cooksd2[n-1];
		midprey = (minprey + maxprey)/2;
		setPreferredSize(new Dimension(wd,ht));
		
	}
	public void paint(Graphics g){
		super.paint(g);
		int xpxl, ypxl;
		wd = this.getWidth();
		ht = this.getHeight();
		g.setColor(Color.BLACK);
		
		wd1 = (wd-2*m-r)/(2);
		ht1 = (ht-2*m-r)/(2);
		
		Font font = new Font("Serif",Font.BOLD,16);g.setFont(font);
		g.setFont(font);
		g.drawString("F i t    D i a g n o s t i c s    f o r  ", wd/100*33, m/5*2);
		g.drawString(namey[0], wd/100*60, m/5*2);
		Font font1 = new Font("Serif",Font.LAYOUT_LEFT_TO_RIGHT,13);
		g.setFont(font1);
		for(int j =0 ; j<2; j++){//상자를 네개 그린다
			for(int i =0 ; i< 2; i++){
				g.drawRect(m+(wd1+r)*i, m+(ht1+r)*j, wd1, ht1);
			}
		}
		
		for(int i =0 ; i< 2; i++){//예측값대 잔차 분석 상자의 중간에 라인을 그린다
			for(int k =0 ; k< 21; k++){
				g.drawLine(m+(wd1+r)*i+wd1/20*k, m+ht1/20+(ht1/20*18)/2, m+(wd1+r)*i+wd1/20*k+2,  m+ht1/20+(ht1/20*18)/2);
			}
		}
		
		String [][] xname = {{"Predicted Value","Predicted Value"},{"Predicted Value","Observation"}};
		String [][] yname = {{"Residual","Rstudent"},{namey[0],"Cook's d"}};
		double [][] xvalue = {{minprey,midprey,maxprey},{minprey,midprey,maxprey},{minprey,midprey,maxprey}};
		double [][] yvalue = {{-2,-1,0,1,2},{-2,-1,0,1,2},{maxy, miny},{maxcooksd,mincooksd}};
		
		
		for(int i=0;i<n; i++){//예측값대 잔차
			xpxl = m + wd/100 + ((int)((wd1*0.9)*(prey[i]-minprey)/(maxprey-minprey)));
			ypxl = m+ ht/100+(int)((ht1*0.9)*(maxres-residual[i])/(maxres-minres));
			g.fillOval(xpxl, ypxl, ptsize, ptsize);
		}
		
		for(int i=0;i<n; i++){//예측값대 표준화잔차
			xpxl = m + wd/100 + r + wd1 +((int)((wd1*0.9)*(prey[i]-minprey)/(maxprey-minprey)));
			ypxl = m+ ht/100+(int)((ht1*0.9)*(maxstdres-stdres[i])/(maxstdres-minstdres));
			g.fillOval(xpxl, ypxl, ptsize, ptsize);
		}
		
		for(int i=0;i<n; i++){//예측값대 반응변수
			xpxl = m + wd/100 + ((int)((wd1*0.95)*(prey[i]-minprey)/(maxprey-minprey)));
			ypxl = r+ht1+ m+ ht/100+(int)((ht1*0.95)*(maxy-y[i])/(maxy-miny));
			g.fillOval(xpxl, ypxl, ptsize, ptsize);
		}
		
		g.drawLine(m,ht-m,m+wd1,ht-m-ht1);//line 그리기
		
		
		for(int i=0;i<n; i++){//관측값대 cooks
			ypxl =(int)((ht1/100*98)*(cooksd[i]-mincooksd)/(maxcooksd-mincooksd));
			g.drawLine(m+wd1+r+5+(wd1/(n))*(i+1),ht-m,m+wd1+r+5+(wd1/(n))*(i+1),ht-m-ypxl);
			g.drawOval(m+wd1+r+5+(wd1/(n))*(i+1) - ptsize/2, ht-m-ypxl-ptsize ,ptsize,ptsize);
			ptsize =5;
			if((i+1)%5==0){
				g.drawString(String.valueOf(i+1),m+wd1+r+5+(wd1/(n))*(i+1),ht-m+20);
			}
		} 
		
	
		//x축과 y축에 이름 넣기
		Font name = new Font("Serif",Font.BOLD,12);
		g.setFont(name);
		
		for(int j =0 ; j<2; j++){
			for(int i =0 ; i< 2; i++){
				g.drawString(xname[i][j], m+wd1/3 + (wd1+r)*i, m+ht1+r/10*8+(ht1+r)*j);
				int q = yname[j][i].trim().length();
				for(int k =0 ; k<q; k++){
					String tmp = yname[j][i].trim().substring(k, k+1);
					g.drawString(tmp, m/3+(wd1+2/3*m+r)*i, m+ht1/10+(ht1+r)*j+ht1/18*k);
				}
				for(int k =0 ; k<5; k++){//y축 눈금그리기
					g.drawLine(m-8+(wd1+r)*i, m+(ht1+r)*j+(ht1/20*18)/4*k+ ht1/20,m-2+(wd1+r)*i,m+(ht1+r)*j+(ht1/20*18)/4*k+ ht1/20);
				}
				for(int k =0 ; k<3; k++){//x축 눈금그리기
					g.drawLine(m+(wd1+r)*i+(wd1/20*18)/2*k+ wd1/20, m+ht1+2+(ht1+r)*j,m+(wd1+r)*i+(wd1/20*18)/2*k+ wd1/20,m+ht1+8+(ht1+r)*j);
				}
				
			}
		}
		
		
		for(int i = 0; i < 5; i ++ ){g.drawString(String.format("%.0f", minres + i*(maxres-minres)/4), m -5*ptsize, m + 10 + ht1/20*18 / 4 *(4-i));}
		for(int i = 0; i < 5; i ++ ){g.drawString(String.format("%.0f", minstdres + i*(maxstdres-minstdres)/4), m + wd1 + r + -3*ptsize, m + 10 + ht1/20*18 / 4 *(4-i));}
		for(int i = 0; i < 5; i ++ ){g.drawString(String.format("%.0f", miny + i*(maxy-miny)/4), m -5*ptsize, m + 10 + ht1/20*18 / 4 *(4-i) + r + ht1);}
		
		for(int i = 0; i < 3; i ++ ) {g.drawString(String.format("%.0f", minprey + i*(maxprey-minprey)/2), m  + 3*ptsize + i*(wd1/20*18)/2 , m + ht1 + r/2);}
		for(int i = 0; i < 3; i ++ ) {g.drawString(String.format("%.0f", minprey + i*(maxprey-minprey)/2), m  + 3*ptsize + i*(wd1/20*18)/2 + wd1 + r , m + ht1 + r/2);}
		for(int i = 0; i < 3; i ++ ) {g.drawString(String.format("%.0f", minprey + i*(maxprey-minprey)/2), m  + 3*ptsize + i*(wd1/20*18)/2 , m + 2*ht1 + r/2*3 );}
		
		
		for(int k=0; k<21;k++){
			//g.drawLine(m-8+(wd1+r) + wd1/20*k , m+ (int)(ht1/20*18*(maxstdres-2)/(maxstdres-minstdres)),m-8+(wd1+r) + wd1/20*k + 2, m+ (int)(ht1/20*18*(maxstdres-2)/(maxstdres-minstdres)));
			//g.drawLine(m-8+(wd1+r) + wd1/20*k , m+ (int)(ht1/20*18*(maxstdres+2)/(maxstdres-minstdres)),m-8+(wd1+r) + wd1/20*k + 2, m+ (int)(ht1/20*18*(maxstdres+2)/(maxstdres-minstdres)));
		}
		g.setFont(font1);
	
	}
}
