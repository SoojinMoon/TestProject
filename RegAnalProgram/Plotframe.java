package test;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JPanel;

public class Plotframe extends JPanel{
	
	double [][] x;
	double [][] xminmax;
	String[] namex = {};
	String[] namey = {};
	double minx =0, miny = 0, maxx = 0, maxy =0;
	int wd = 800;
	int ht = 550;
	int m = 40;
	int r = 5;
	int ptsize = 4 ;
	int n; int p; int ht1; int wd1;
	double [][] A;
	public Plotframe(double [][]x,  int n , int p, String[] namex, String[] namey){
		this.x = x;this.n = n ; this.p =p; this.namex = namex; this.namey = namey;//p는 설명변수개수 의미
		//첫째 열은 반응 변수의 자료값 , 2~p 째열 까지 설명변수의 관측값, 변수 개수만큼 산점도를 그려주고 그 안에 관측값의 개수만큼 점을 찍어주기 위해 매개변수로 받음
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
		super.paint(g);
		int xpxl, ypxl;
		wd = this.getWidth();
		ht = this.getHeight();
		g.setColor(Color.BLACK); 
		Font font = new Font("Serif",Font.BOLD,16);g.setFont(font);
		Font font2 = new Font("Serif",Font.BOLD,13);
		g.drawString("산점도 행렬", (wd-2*m)/100*46, m/2);
		Font font1 = new Font("Serif",Font.LAYOUT_LEFT_TO_RIGHT,13);
		g.setFont(font1);
		
		wd1 = (wd-2*m-(p-1)*r)/(p); // 프레임 하나당 상자의 너비와 높이
		ht1 = (ht-2*m-(p-1)*r)/(p);
		
		for(int j =0 ; j<p; j++){ // 
			for(int i =0 ; i< p; i++){
				g.drawRect(m+(wd1+r)*i, m+(ht1+r)*j, wd1, ht1);
			}
		}
		
		g.drawString(namey[0],m+wd1/4, m+ht1/2);
		for(int i =0 ; i< p-1; i++){
			g.drawString(namex[i],(int) (m+wd1/5+(r+(double)wd1/100*98)*(i+1)), (int) (m+wd1/2+(r+(double)ht1/100*94)*(i+1)));
		}
		
		
		for(int k =0; k< p-1; k++){//첫 번째 행부터 p-1번째 행까지 점을 찍어줌
			for(int j =k+1 ; j< p; j++){
				for(int i=0; i<n ; i++){
					xpxl = m +(ptsize/2)+ (wd1 + r )*j  +(int)((wd-2*m-(p-1+3)*r-(p-1)*wd1)*(x[i][j]-xminmax[0][j])/(xminmax[1][j]-xminmax[0][j]));
					ypxl = m+ (ht1 + r )*(k)+ r +(int)((ht-2*m-(p-1+2)*r-(p-1)*ht1)*(xminmax[1][k]-x[i][k])/(xminmax[1][k]-xminmax[0][k]));
					g.fillOval(xpxl, ypxl, ptsize, ptsize);
				}
			}
		}
		
	

		for(int k =p-1; k>0; k--){//p번째 행부터 2번째 행까지 점을 찍어줌
			for(int j =k-1 ; j>=0; j--){
				for(int i=0; i<n ; i++){
					xpxl = m +(ptsize/2)+ (wd1 + r )*j  +(int)((wd-2*m-(p-1+3)*r-(p-1)*wd1)*(x[i][j]-xminmax[0][j])/(xminmax[1][j]-xminmax[0][j]));
					ypxl = m + (ht1 + r)*(k)+ r +(int)((ht-2*m-(p-1+2)*r-(p-1)*ht1)*(xminmax[1][k]-x[i][k])/(xminmax[1][k]-xminmax[0][k]));
					g.fillOval(xpxl, ypxl, ptsize, ptsize);
				}
			}
		}
		
		g.setFont(font2);
		for( int j =0 ; j<p ; j++){
			for(int i =0 ; i < 2 ; i++){
				g.drawLine(m-8, m+(ht1+r)*j+ ht1*i, m-2,  m+(ht1+r)*j+ht1*i);
				g.drawLine(m+3+wd1*p+r*(p-1), m+(ht1+r)*j+ ht1*i, m+wd1*p+r*(p-1)+9,  m+(ht1+r)*j+ht1*i);
			}
		}	
	
		for( int j =0 ; j<p ; j++){
			for(int i =0 ; i < 2 ; i++){
				g.drawLine( m+(wd1+r)*j+wd1*i,ht-m+2,  m+(wd1+r)*j+wd1*i, ht-m+6);
			
			}
		}	
		
		for( int i =0 ; i<p ; i+=2){//왼쪽에 y최대 최소 표시
				g.drawString(String.format("%.0f",xminmax[1][i]), m/10*3,  m+ht1/10+(ht1+r)*i);
				g.drawString(String.format("%.0f",xminmax[0][i]), m/10*3,  m+ht1+(ht1+r)*i);
		}	
		for( int i =1 ; i<p ; i+=2){//오른쪽에 y최대 최소 표시
			g.drawString(String.format("%.0f",xminmax[1][i]), m+wd1*p+r*(p-1),  m+ht1/10+(ht1+r)*i);
			g.drawString(String.format("%.0f",xminmax[0][i]), m+wd1*p+r*(p-1),  m+ht1+(ht1+r)*i);
		}	
	
		for( int j =0 ; j<p ; j++){//x, y축 눈금 표시
			for(int i =0 ; i < 2 ; i++){
				g.drawLine(m + wd1*i +(r+wd1)*j, m-8, m + wd1*i+(r+wd1)*j,  m-2);
				g.drawLine( m+(wd1+r)*j+wd1*i,ht-m+2,  m+(wd1+r)*j+wd1*i, ht-m+10);
			}
		}	
	
		for( int i =1 ; i<p ; i+=2){//위쪽에 x최대 최소 표시
			g.drawString(String.format("%.0f",xminmax[0][i]), m+(wd1+r)*i, m);
			g.drawString(String.format("%.0f",xminmax[1][i]), m+wd1/20*17+(wd1+r)*i, m);
		}	
		for( int i =0 ; i<p ; i+=2){//아래쪽에 x최대 최소 표시
			g.drawString(String.format("%.0f",xminmax[0][i]), m+(wd1+r/5*8)*i, m+r*(p-1)+p*ht1+r*2);
			g.drawString(String.format("%.0f",xminmax[1][i]), m+wd1/20*17+(wd1+r/5*8)*i, m+r*(p-1)+p*ht1+r*2);
		}	

	
		
	
	}
	

	
}