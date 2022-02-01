package test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.TreeSet;

import javax.swing.JPanel;

public class ResidualAnal2 extends JPanel{
	double [][] x;
	double [][] xminmax;
	String[] namex = {};
	String[] namey = {};
	double minx =0, miny = 0, maxx = 0, maxy =0;
	int wd = 900;
	int ht = 300;
	int m = 50;
	int r = 40;
	int ptsize = 4 ;
	int n; int p; int ht1; int wd1;
	double [][] A;
	public ResidualAnal2(double [][]x,  int n , int p, String[] namex, String[] namey){
		//1번째 열의 값은 y 관측값, 설명변수의 개수만큼만 산점도를 그려주기 위해 p, 자료값의 개수만큼 점을 찍어주기위해 n 을 매개변수로
		this.x = x;this.n = n ; this.p =p; this.namex = namex; this.namey = namey;//p는 설명변수개수 의미
		xminmax = new double [3][p]; 
		A = new double [n][p];
		for(int j=0; j<p;j++){
			for(int i =0 ; i<n; i++){
				A[i][j] = x[i][j];
			}
		}
		for(int k=0; k<p;k++){
			for(int i=0;i<n;i++){
				for(int j=i+1;j<n; j++){
					double test = A[i][k];
					if(A[j][k]<test){
						A[i][k] = A[j][k];
						A[j][k] = test;
					}
				}
			}
		}
		for(int j=0; j<p ; j++){
				xminmax[0][j] = A[0][j];
				xminmax[1][j] = A[n-1][j];
		}
		
		setPreferredSize(new Dimension(wd,ht));
	}
	public void paint(Graphics g){
		super.paint(g);//p는 종속 + 설명 // 총변수의 개수 의미
		int xpxl, ypxl;
		wd = this.getWidth();
		ht = this.getHeight();
		wd1 = (wd-2*m-(p-2)*r)/(p-1);
		ht1 = (ht-2*m);
		g.setColor(Color.BLACK); 
		Font font = new Font("Serif",Font.BOLD,16);
		g.setFont(font);
		g.drawString("Residual by Regressors for ", (wd-2*m)/100*40, m/2);
		g.drawString(namey[0], (wd-2*m)/100*70, m/2);
		
		Font font1 = new Font("Serif",Font.LAYOUT_LEFT_TO_RIGHT,13);
		g.setFont(font1);
		for(int i =0 ; i<p-1; i++){
			g.drawRect(m+(wd1+r)*i, m, wd1, ht1);
			g.drawString(namex[i],wd1/2 +(wd1+r)*i,m+ht1+m/2);
			for(int j = 0 ; j < 2; j++){//y축 눈금표시
				g.drawLine(m-8+(wd1+r)*i, m+ ht1*j, m-4+(wd1+r)*i, m+ ht1*j);
			}
			for(int j = 0 ; j < 2; j++){//x축 눈금표시
				g.drawLine(m+(wd1)*j+(wd1+r)*i, ht-m+4, m+(wd1)*j+(wd1+r)*i,ht-m+8);
			}
			for(int k=0; k<21;k++){
				//g.drawLine(m+(wd1+r)*i + wd1/20*k, m+(int)(ht1*(xminmax[1][0]-200)/(xminmax[1][0]-xminmax[0][0])), m+(wd1+r)*i+wd1/20*(k)+2,  m+(int)(ht1*(xminmax[1][0]-200)/(xminmax[1][0]-xminmax[0][0])));
				g.drawLine(m+(wd1+r)*i + wd1/20*k, m+(int)(ht1*(xminmax[1][0]-0)/(xminmax[1][0]-xminmax[0][0])), m+(wd1+r)*i+wd1/20*(k)+2,  m+(int)(ht1*(xminmax[1][0]-0)/(xminmax[1][0]-xminmax[0][0])));
				//if(xminmax[0][0] < -200) {g.drawLine(m+(wd1+r)*i + wd1/20*k, m+(int)(ht1*(xminmax[1][0]+200)/(xminmax[1][0]-xminmax[0][0])), m+(wd1+r)*i+wd1/20*(k)+2,  m+(int)(ht1*(xminmax[1][0]+200)/(xminmax[1][0]-xminmax[0][0])));}
			}		
		}
		
			
	
		
		for(int j =1 ; j<p; j++){
			for(int i=0; i<n ; i++){
				xpxl = m + ptsize+ (wd1 + r)*(j-1) +(int)((wd1-ptsize*3)*(x[i][j]-xminmax[0][j])/(xminmax[1][j]-xminmax[0][j]));
				ypxl = m + ptsize + (int)((ht1-ptsize*3)*(xminmax[1][0]-x[i][0])/(xminmax[1][0]-xminmax[0][0]));
				g.fillOval(xpxl, ypxl, ptsize, ptsize);
			}
		}
		
		Font name = new Font("Serif",Font.BOLD,12);
		g.setFont(name);
		String s = "Residual";
		int q = s.trim().length();
		for(int k =0 ; k<q; k++){
			String tmp = s.trim().substring(k, k+1);
			g.drawString(tmp, m/3, m+ht1/3+ht1/18*k);
		}
		double tmp = 200; double tmp1 = 0; double tmp2 = -200;
		for(int i =0 ; i<p-1; i++){
			g.drawString(String.format("%.0f", xminmax[1][0]), m -4*ptsize+ (wd1+r)*i, m);
			g.drawString(String.format("%.0f", xminmax[0][0]), m -5*ptsize+ (wd1+r)*i, m+ht1-ptsize);
			//g.drawString(String.format("%.0f", tmp), m -4*ptsize+ (wd1+r)*i,   m+2*ptsize+(int)(ht1*(xminmax[1][0]-200)/(xminmax[1][0]-xminmax[0][0])));
			g.drawString(String.format("%.0f", tmp1), m -4*ptsize+ (wd1+r)*i,   m+ptsize+(int)(ht1*(xminmax[1][0])/(xminmax[1][0]-xminmax[0][0])));
			//g.drawString(String.format("%.0f", tmp2), m -4*ptsize - 5+ (wd1+r)*i,   m-2*ptsize+(int)(ht1*(xminmax[1][0]+200)/(xminmax[1][0]-xminmax[0][0])));
		}
		for(int i=0;i < p-1 ;i++){
			for(int j = 0 ; j < 2; j++){//x자료
				g.drawString(String.format("%.0f", xminmax[j][i+1]), m+(wd1-3*ptsize)*j+(wd1+r)*i,ht-m+13);
			}
		}
	
	}
}