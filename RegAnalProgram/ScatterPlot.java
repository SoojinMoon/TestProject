package test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ScatterPlot extends JPanel{
	double [] xx, yy;
	int nn;
	double minx =0, miny = 0, maxx = 0, maxy =0;
	int wd = 800;
	int ht = 600;
	int m = 30;
	int ptsize = 10 ;
	Color myColor;
	
	public ScatterPlot(double []xx, double []yy, Color myColor){
		this.xx= xx; this.yy = yy; this.myColor = myColor;
		nn = xx.length;
		for(int i=0; i<nn; i++){
			if(xx[i]<minx) minx = xx[i];
			if(xx[i]>maxx) maxx = xx[i];
			if(yy[i]<miny) miny = yy[i];
			if(yy[i]>maxy) maxy = yy[i];
		}
		setPreferredSize(new Dimension(wd,ht));
	}
	
	
	public static void main(String[] args) {
		double [] x= new double[100];
		double [] y =new double[100];
		double rho = -0.8;
		Random rd = new Random();
		for(int i=0; i<x.length ; i++){
			x[i] = rd.nextGaussian();
			y[i] = Math.sqrt(1-rho*rho)*rd.nextGaussian()+rho*x[i];
			
		}
		Color aColor = new Color(255,0,0);
		ScatterPlot sp = new ScatterPlot(x,y,aColor);
		JFrame frame = new JFrame("Normal");
		frame.add(sp);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		
	}
	
	
	public void paint(Graphics g){
		
		super.paint(g);
		int xpxl, ypxl;
		g.setColor(Color.BLACK);
		g.drawLine(wd/2, m, wd/2, ht-m);
		g.drawLine(m,ht/2, wd-m , ht/2);
		g.setColor(myColor);
		for(int i=0;i<nn; i++){
			xpxl = m +(int)((wd-2*m)*(xx[i]-minx)/(maxx-minx));
			ypxl = m +(int)((ht-2*m)*(maxy-yy[i])/(maxy-miny));
			g.fillOval(xpxl, ypxl, ptsize, ptsize);
		}
	}

}
