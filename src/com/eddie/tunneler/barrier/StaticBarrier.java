package com.eddie.tunneler.barrier;

import android.graphics.Canvas;

import android.graphics.Path;
import android.graphics.Matrix;

import com.eddie.tunneler.barrier.Barrier;

public class StaticBarrier extends Barrier {
	public StaticBarrier(){
		super();
		bType = gen.nextInt(2);
		switch(bType){
		case 0:
			bShape.addCircle(0,0,0.5f,Path.Direction.CW);
			bShape.addCircle(0,0,0.3f,Path.Direction.CW);
			bColor = 0xff0080ff;
			bFillColor = 0xff003366;
			break;
		case 1:
			bShape.addCircle(0, 0, 0.35355339059f, Path.Direction.CW);
			bColor = 0xff00ff00;
			bFillColor = 0xff006600;
			break;
		}
		bFillPaint.setColor(bFillColor);
		bPaint.setColor(bColor);
	}
	@Override
	public void draw(Canvas c, float x, float y, int cw, int ch, int mindim, int maxdim){//x and y range from -0.5 to +0.5
		//compute center
		float xposb = cw/2f - maxdim*x/(2f*(dist+bThickness));
		float yposb = ch/2f - maxdim*y/(2f*(dist+bThickness));
		//not bothering with perspective because i'm lazy
		Path bPath = new Path(bShape);
		Matrix bMatrix = new Matrix();
		//scaling
		bMatrix.setScale(maxdim/(2f*(dist+bThickness)), maxdim/(2f*(dist+bThickness)),0,0);
		bPath.transform(bMatrix);
		//translation
		bMatrix.setTranslate(xposb,yposb);
		bPath.transform(bMatrix);
		//fill type
		bPath.setFillType(Path.FillType.EVEN_ODD);
		//drawing
		c.drawPath(bPath,bFillPaint);
		c.drawPath(bPath,bPaint);
		if(dist > 0){
			float xpos = cw/2f - maxdim*x/(2f*dist);
			float ypos = ch/2f - maxdim*y/(2f*dist);
			Path fPath = new Path(bShape);
			Matrix fMatrix = new Matrix();
			fMatrix.setScale(maxdim/(2f*dist), maxdim/(2f*dist));
			fPath.transform(fMatrix);
			fMatrix.setTranslate(xpos,ypos);
			fPath.transform(fMatrix);
			fPath.setFillType(Path.FillType.EVEN_ODD);
			c.drawPath(fPath,bFillPaint);
			c.drawPath(fPath,bPaint);
		}
	}
	
	public StaticBarrier(float dist){
		this();
		this.dist = dist;
	}
	@Override
	public boolean hit (float x, float y){
		if (dist > 0) return false;
		float rdist = (float)Math.sqrt(x*x+y*y);
		y*= -1;
		//collision detection
		switch(bType){
		case 0:
			return rdist >= 0.3;
		case 1:
			return rdist <= 0.35355339059f;
		}
		return false;
	}
}
