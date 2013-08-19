package com.eddie.tunneler.barrier;

import java.util.Random;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Matrix;
import java.lang.Math;
import android.util.Log;

public class Barrier {
	//protected final int nTypes = 4;
	public int bType = 0;
	public float dist = 15.8f;//add barriers 4 tunnel lengths away by default
	protected float bThickness = 0.2f;
	public float rot;
	protected int bColor, bFillColor;
	protected Paint bPaint, bFillPaint;
	public final float fov = 45f;
	protected Path bShape;
	public boolean rrot;
	protected Random gen;
	
	public Barrier(){
		gen = new Random();
		bFillPaint = new Paint();
		bFillPaint.setStyle(Paint.Style.FILL);
		bPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		bPaint.setStyle(Paint.Style.STROKE);
		bShape = new Path();
	}
	public Barrier(float dist){
		this();
		this.dist = dist;
	}
	public void draw(Canvas c, float x, float y, int cw, int ch, int mindim, int maxdim){//x and y range from -0.5 to +0.5
		//compute center
		float xpos = cw/2f - maxdim*x/(2f*dist);
		float ypos = ch/2f - maxdim*y/(2f*dist);
		float xposb = cw/2f - maxdim*x/(2f*(dist+bThickness));
		float yposb = ch/2f - maxdim*y/(2f*(dist+bThickness));
		//not bothering with perspective because i'm lazy
		Path fPath = new Path(bShape), bPath = new Path(bShape);
		Matrix fMatrix = new Matrix(), bMatrix = new Matrix();
		//scaling
		fMatrix.setScale(maxdim/(2f*dist), maxdim/(2f*dist));
		bMatrix.setScale(maxdim/(2f*(dist+bThickness)), maxdim/(2f*(dist+bThickness)),0,0);
		fPath.transform(fMatrix);
		bPath.transform(bMatrix);
		//rotation
		if(this instanceof SpinningBarrier){
			if(rrot)fMatrix.setRotate(-rot);
			else fMatrix.setRotate(rot);
			fPath.transform(fMatrix);
			bPath.transform(fMatrix);
		}
		//translation
		fMatrix.setTranslate(xpos,ypos);
		bMatrix.setTranslate(xposb,yposb);
		fPath.transform(fMatrix);
		bPath.transform(bMatrix);
		//fill type
		fPath.setFillType(Path.FillType.EVEN_ODD);
		bPath.setFillType(Path.FillType.EVEN_ODD);
		//drawing
		c.drawPath(bPath,bFillPaint);
		c.drawPath(bPath,bPaint);
		c.drawPath(fPath,bFillPaint);
		c.drawPath(fPath,bPaint);
	}
	
	public boolean passed(){
		return dist < (-bThickness);
	}
	public boolean hit(float x, float y){
		return false;
	}
}
