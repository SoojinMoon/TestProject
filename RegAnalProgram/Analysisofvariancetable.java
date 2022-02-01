package test;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Random;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


public class Analysisofvariancetable extends JPanel{
	double [][] x;
	double []y ;
	int wd = 800;
	int ht = 1000;
	int m = 30;
	int ptsize = 10 ;
	double SSR, SSE, SST, MSR, MSE, F, pvalue1, rsquare,rootMSE, AdjR, depmy, stdy, coeffvar = 0;
	double [][] transx ;
	double [][] multix ;
	double [][] gausemultix ;
	double [][] commonx ;
	double [][] H ;
	double [][] J ;
	double [][] I ;
	double [][] R ;
	double [][] E ;
	double [] multiR ;
	double [] multiE ;
	double [] beta ;
	double []ss ;
	double []t ;
	double []pvalue2 ;
	double [][] detx ;
	double pivot;
	double []obj ;
	double [] prey ;
	double [] residual ;
	double [] semp ;
	double [] ser;
	double [] cooksd;
	double [] stdres;
	int n ; int p; int info =0 ;
	String [] namex ;
	String [] namey ;
	String pvalue3;
	
	public Analysisofvariancetable(double [][]x, double []y,  int n , int p, String[] namex, String[] namey){
		//행렬 X를 만들 때 첫째 열은 1, 둘째 열부터설명변수 , 
		//자료의 개수와 행렬 X의 열개수 = 모수의 개수를 매개변수로 
		this.p = p;	this.n=n;
		this.namex = namex;
		this.namey = namey;
		transx = new double[p][n];
		multix = new double[p][p];
		commonx = new double [p][n];
		H = new double [n][n];
		J = new double [n][n];
		I = new double [n][n];
		R = new double [n][n];
		E = new double [n][n];
		multiR = new double [n];
		multiE = new double[n];
		beta = new double [p];
		ss = new double[p];
		t = new double [p];
		pvalue2 =new double [p];
		detx = new double[p][p]; 
		double pivot;
		obj = new double [n];
		prey = new double [n];
		residual = new double[n];
		semp = new double [n];
		cooksd = new double [n];
		stdres = new double [n];
		ser = new double [n];
		double sum;
		this.y= y; this.x = x;
		
		for(int j =0 ; j<n; j++){
			for(int i =0; i<n; i++){
				J[j][i] = (double)1/n; 
				if(i==j){
					I[i][j] = 1;
				}
				else{
					I[i][j] =0;
				}
			}
		}

		for(int j=0 ; j<p; j++){
			for(int i=0;i<p;i++){
				if(i==j){
					detx[i][j] = 1;
				}
				else{
					detx[i][j] =0;
				}
			}
		}
		
		for(int j =0 ; j<p ;j++){
			for(int i=0; i<n; i++){
				transx[j][i] = x[i][j];
			}
		}
		
		for(int k=0 ; k<p; k++){
			for(int j=0 ;j<p; j++){
				sum = 0 ;
				for(int i=0; i<n; i++){
					sum += transx[k][i]*x[i][j];
				}
				multix[k][j] = sum;
			}
		}

		for(int k=0; k<p-1; k++){
			for(int j=k+1; j<p; j++){
				pivot = -multix[j][k]/multix[k][k];
				for(int i=k; i<p;i++){
					multix[j][i] = multix[j][i] + multix[k][i] * pivot;
				}
				for(int i=0; i<p;i++){
					detx[j][i] = detx[j][i] + detx[k][i] * pivot;
				}
			}
		}

		for(int k=p-1; k>=0; k--){
			double r = multix[k][k];
			for(int i=0; i<p; i++){
				multix[k][i]= multix[k][i]/r;
				detx[k][i] = detx[k][i]/r;
			}
			for(int j=k-1; j>=0; j--){
				pivot = -multix[j][k];
				for(int i=0; i<p;i++){
					multix[j][i] = multix[j][i] + multix[k][i] * pivot;
					detx[j][i] = detx[j][i] + detx[k][i] * pivot;
				}
			}
		}
		
		for(int k=0 ; k<p; k++){
			for(int j=0 ;j<n; j++){
				sum = 0 ;
				for(int i=0; i<p; i++){
					sum += detx[k][i]*transx[i][j];
				}
				commonx[k][j] = sum;
			}
		}
		
		for(int j=0 ;j<p; j++){
			sum = 0 ;
			for(int i=0; i<n; i++){
				sum += commonx[j][i]*y[i];
			}
			beta[j]=sum;
		}
		
		
		for(int k=0 ; k<n; k++){
			for(int j=0 ;j<n; j++){
				sum = 0 ;
				for(int i=0; i<p; i++){
					sum += x[k][i]*commonx[i][j];
				}
				H[k][j] = sum;
			}
		}
		
		for(int j =0; j<n ; j++){
			for(int i =0; i<n; i++){
				R[j][i] = H[j][i]-J[j][i];
				E[j][i] = I[j][i]-H[j][i];
			}
		}

		for(int j=0; j<n;j++){
			sum =0 ; double sum1 =0 ;
			for(int i=0; i<n; i++){
				sum += y[i]*R[i][j];
				sum1 += y[i]*E[i][j];
			}
			multiR[j] = sum ; 
			multiE[j] = sum1;
		}
		
		SSR=0; SSE=0;
		for(int i =0; i<n; i++){
			SSR+= multiR[i]*y[i];
			SSE+= multiE[i]*y[i];
		}
		
		SST = SSR + SSE;
		MSR = SSR / (p-1);
		MSE = SSE / (n-p);
		F = MSR/MSE;
		rootMSE = Math.sqrt(MSE);
		rsquare = SSR/SST;
		AdjR =1-(n-1)*MSE/SST;
		for(int i =0; i<p; i++){
			ss[i] = Math.sqrt(detx[i][i]*MSE);
			t[i] =beta[i]/ss[i];
		}
		
		sum=0;
		for(int i=0 ; i<n;i++){
			obj[i] = i  +1;
			sum+= y[i];
		}
		depmy = sum / n;
		stdy = rootMSE/depmy*100;
		
		for(int k=0 ; k<n; k++){
			for(int j=0 ;j<p; j++){
				sum = 0 ;
				for(int i=0; i<p; i++){
					sum += x[k][i]*beta[i];
				}
				prey[k] = sum;
			}
		}
		for(int i =0; i<n;i++){
			residual[i] = y[i]-prey[i];
		}
		
		for(int i =0; i<n; i++){
			cooksd[i] = H[i][i]/((1-H[i][i])*(1-H[i][i]))*residual[i]*residual[i]/p/MSE;
		}
		
		for(int i =0; i<n ; i++){
			stdres[i] = residual[i]/(rootMSE*Math.sqrt(1-H[i][i]));
		}
		
		for(int i =0; i<n;i++){
			ser[i] = H[i][i];
		}

		StatLibrary one = new StatLibrary();
		pvalue1 = one.f_cdf(F, (p-1), (n-p), info);
		for(int i =0 ; i<p;i++){
			pvalue2[i] = one.t_cdf(-Math.abs(t[i]), 1, info);
		}
		setPreferredSize(new Dimension(wd,ht));
	}
	
	public void paint(Graphics g){
		super.paint(g);
		wd = this.getWidth();
		ht= this.getHeight();
		
		System.out.println();
		Font font1 = new Font("Serif",Font.LAYOUT_LEFT_TO_RIGHT,13);
		Font font = new Font("Serif",Font.BOLD,16);
		g.setFont(font);
		g.drawString("    Dependent Variabe :	 ", wd/3, ht/40);g.drawString(namey[0], wd/10*6, ht/40);
		g.drawString("Analysis of Variance", wd/10*4, ht/20);
		g.drawRect(m,ht/15,wd-2*m,ht/6);

		
		g.setFont(font1);
		String [] labelname = {"Source","Model","Error","Corrected Total"};
		for(int i=0; i<labelname.length; i++){
			g.drawString(String.valueOf(labelname[i]),wd/60*4, ht/10+18*i);
		}
		
		String [] labelname2 = {"df",String.valueOf(p-1),String.valueOf(n-p),String.valueOf(n-1)};
		for(int i=0; i<labelname2.length; i++){
			g.drawString(String.valueOf(labelname2[i]),wd/40*10, ht/10+18*i);
		}
		
		String [] labelname3 = {"Sum  of  Squares",String.format("%.2f",SSR),String.format("%.2f",SSE),String.format("%.2f",SST)};
		for(int i=0; i<labelname3.length; i++){
			g.drawString(String.valueOf(labelname3[i]),wd/50*19, ht/10+18*i);
		}
		
		String [] labelname4 = {"Mean  Squares",String.format("%.2f",MSR),String.format("%.2f",MSE)};
		for(int i=0; i<labelname4.length; i++){
			g.drawString(String.valueOf(labelname4[i]),wd/50*28, ht/10+18*i);
		}


		String [] labelname6 = {"F-value",String.format("%.2f",F)};
		for(int i=0; i<labelname6.length; i++){
			g.drawString(String.valueOf(labelname6[i]),wd/50*37, ht/10+18*i);
		}
		pvalue1 = 1-pvalue1;
		if(pvalue1 < 0.0001){
			pvalue1 = 0.0000001;
		}
		String [] labelname7 = {"P r  >  F ",String.valueOf(pvalue1)};
		for(int i=0; i<labelname7.length; i++){
			g.drawString(String.valueOf(labelname7[i]),wd/50*44, ht/10+18*i);
		}
	
		
		String [] labelname8 = {"Root MSE","Dependent Mean","Coeff Var"};
		for(int i=0; i<labelname8.length; i++){
			g.drawString(String.valueOf(labelname8[i]),m*30/10, ht/6+ht/9/5+18*i);
		}
		
		String [] labelname9 = {String.format("%.4f",rootMSE),String.format("%.4f",depmy),String.format("%.4f",stdy)};
		for(int i=0; i<labelname9.length; i++){
			g.drawString(String.valueOf(labelname9[i]),wd/50*19, ht/6+ht/9/5+18*i);
		}
		String [] labelname10 = {"R-square","Adj R-sq"};
		for(int i=0; i<labelname10.length; i++){
			g.drawString(String.valueOf(labelname10[i]),wd/50*28, ht/6+ht/9/5+18*i);
		}
		
		String [] labelname11 = {String.format("%.4f",rsquare),String.format("%.4f",AdjR)};
		for(int i=0; i<labelname11.length; i++){
			g.drawString(String.valueOf(labelname11[i]),wd/50*37, ht/6+ht/9/5+18*i);
		}
	
		
		g.setFont(font);
		g.drawString("Parameter Estimates", wd/10*4,ht/100*25);
		g.drawRect(m,ht/100*25+10,wd-2*m,ht/6);
		g.setFont(font1);

		String [] basicstring2 = {"Variable",  "D F","Parameter Estimate","Standard Error","t value","P r  >  ㅣtㅣ "};
		
		
		int r= namex.length;
			g.drawString(basicstring2[0],wd/60*7,ht/100*30);
			g.drawString(basicstring2[1],wd/60*18,ht/100*30);
			g.drawString(basicstring2[2],wd/60*24,ht/100*30);
			g.drawString(basicstring2[3],wd/60*34,ht/100*30);
			g.drawString(basicstring2[4], wd/60*43,ht/100*30);
			g.drawString(basicstring2[5], wd/60*52,ht/100*30);

		for(int i=0; i<r; i++){
			g.drawString(String.valueOf(namex[i]),wd/60*7,ht/100*30+18*(i+1));
		}
		for(int i =0 ;i<r ; i++){
			g.drawString("1",wd/60*18,ht/100*30+18*(i+1));
		}
		for(int i=0 ;i< r; i++){
			g.drawString(String.format("%.4f",beta[i]),wd/60*24,ht/100*30+18*(i+1));
		}
		for(int i=0 ;i< r; i++){
			g.drawString(String.format("%.4f",ss[i]),wd/60*34,ht/100*30+18*(i+1));
		}
		for(int i=0 ;i< r; i++){
			g.drawString(String.format("%.2f",t[i]), wd/60*43,ht/100*30+18*(i+1));
		}
		for(int i=0 ;i< r; i++){
			g.drawString(String.format("%.4f",pvalue2[i]), wd/60*52,ht/100*30+18*(i+1));
		}
		
		
		g.setFont(font);
		g.drawString("Output Statistics", wd/10*4,ht/100*45);
		g.drawRect(m,ht/100*46,wd-2*m,ht-ht/100*46-m/4);
		g.setFont(font1);
		
		
		g.drawString("obs", wd/40*4, ht/100*48);
		for(int i=0; i< obj.length; i++){
			g.drawString(String.valueOf(obj[i]), wd/40*4, ht/100*48+17*(i+1));
		}
		
		
		g.drawString("Dependent Variable", wd/40*9, ht/100*48);
		for(int i=0; i< n; i++){
			g.drawString(String.format("%.4f",y[i]), wd/40*9,ht/100*48+17*(i+1));
		}
		
		g.drawString("Predicted Value", wd/40*15, ht/100*48);
		for(int i=0; i<n; i++){
			g.drawString(String.format("%.4f",prey[i]), wd/40*15,ht/100*48+17*(i+1));
		}
		
	
		
		g.drawString("Residual", wd/40*21, ht/100*48);
		for(int i=0; i< n; i++){
			g.drawString(String.format("%.4f",residual[i]), wd/40*21, ht/100*48+17*(i+1));
		}
	
		g.drawString("Student Residual", wd/40*27, ht/100*48);
		for(int i=0; i< n; i++){
			g.drawString(String.format("%.4f",stdres[i]), wd/40*27, ht/100*48+17*(i+1));
		}
		
		g.drawString("Cook's D",wd/40*33, ht/100*48);
		for(int i=0; i< n; i++){
			g.drawString(String.format("%.4f",cooksd[i]), wd/40*33, ht/100*48+17*(i+1));
		}
	}

}
