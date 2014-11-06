package com.ivali.gamebox.util;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.ivali.gamebox.R;

public class RoundedImageView extends ImageView {
    private int mBorderThickness = 4;
    private Context mContext;
    private int mBorderColor = 0xffFFFFff;
    private Path path;
    private boolean showTextBackground;
    private Paint paint;
	private PaintFlagsDrawFilter mDrawFilter;
	private boolean blackWhiteMode = false;
    private ColorMatrix blackWhiteCM = null;
    private ColorMatrixColorFilter blackWhiteCCF = null;

    public RoundedImageView(Context context) {
        super(context);
        mContext = context;
        path = new Path();

    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        path = new Path();
        mContext = context;
        setCustomAttributes(attrs);
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        path = new Path();
        setCustomAttributes(attrs);
    }

    private void setCustomAttributes(AttributeSet attrs) {
        TypedArray a = mContext.obtainStyledAttributes(attrs,
                R.styleable.others_rounded_imageview);
        mBorderThickness = a.getDimensionPixelSize(R.styleable.others_rounded_imageview_border_thickness,mBorderThickness);
        mBorderColor = a.getColor(R.styleable.others_rounded_imageview_border_color,mBorderColor);
        showTextBackground = a.getBoolean(R.styleable.others_rounded_imageview_show_text_background, false);
        a.recycle();
    }

    protected void onDrawf(Canvas canvas) {
        Drawable drawable = getDrawable();

        if (drawable == null) {
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        this.measure(0, 0);
        if(drawable.getClass() == NinePatchDrawable.class)
            return;
        if(paint == null)paint = new Paint();
        path.reset();
        int w = canvas.getWidth();
        int h = canvas.getHeight();
        ///canvas.drawARGB(0,0,0,0);
        if(mDrawFilter == null)mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
        canvas.setDrawFilter(mDrawFilter);
        path.addCircle(w/2.0f, h/2.0f, Math.min(w/2.0f, h/2.0f), Path.Direction.CCW);
        canvas.clipPath(path);
        //super.onDraw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Drawable drawable = getDrawable();

        if (drawable == null) {
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        this.measure(0, 0);
        if(drawable.getClass() == NinePatchDrawable.class)
            return;

        if(mDrawFilter == null)mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
        canvas.setDrawFilter(mDrawFilter);
        Bitmap b = ((BitmapDrawable) drawable).getBitmap();

        if(b == null)return;
        if(paint == null)paint = new Paint();

        Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);

        int w = getWidth();
        int h = getHeight();

        int radius = Math.min(w,h)/2 - Math.max(mBorderThickness/4,1);
        Bitmap roundBitmap = getCroppedBitmap(bitmap, radius);
        // roundBitmap=ImageUtils.setCircularInnerGlow(roundBitmap, 0xFFBAB399,
        // 4, 1);
        //canvas.drawBitmap(roundBitmap, w / 2 - radius, 8, null);

        canvas.drawBitmap(roundBitmap, w / 2 - radius, h /2 - radius, null);

        if(mBorderThickness >0){
            paint.reset();
            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);
            paint.setDither(true);
            paint.setColor(mBorderColor);
            paint.setStrokeWidth(mBorderThickness);
        	paint.setAntiAlias(true);
        	paint.setStyle(Style.STROKE);
	        canvas.drawCircle(w / 2,
	                h / 2, radius - Math.max(mBorderThickness/4,1), paint);
        }
        paint.reset();
    }

    private  Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
        Bitmap scaledSrcBmp;
        int diameter = radius * 2;
        float scale_size = (diameter*1.0f/Math.min(bmp.getWidth(), bmp.getHeight()));
        if (bmp.getWidth() != diameter || bmp.getHeight() != diameter)
            scaledSrcBmp = Bitmap.createScaledBitmap(bmp, (int) (bmp.getWidth()*scale_size), (int)(bmp.getHeight()*scale_size), false);
        else
            scaledSrcBmp = bmp;
        Bitmap output = Bitmap.createBitmap(diameter, diameter,
                Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.setDrawFilter(mDrawFilter);
        if(showTextBackground){
            Canvas canvast = new Canvas(scaledSrcBmp);
            paint.setColor(Color.parseColor("#B2000000"));
             int y = scaledSrcBmp.getHeight() - dip2px(getContext(), 3.0f)-(scaledSrcBmp.getHeight()-diameter)/2;
             canvast.drawRect(0,y-dip2px(getContext(), 16),
                     scaledSrcBmp.getWidth(),y,  paint);
        }
        paint.reset();
        final Rect rect = new Rect(0, 0, diameter, diameter);

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle(radius,radius, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        int sx = Math.max(0,scaledSrcBmp.getWidth()-diameter)/2;
        int sy = scaledSrcBmp.getHeight()/2-radius;

        //黑白模式
    	if(blackWhiteMode){
	        if(blackWhiteCM == null) blackWhiteCM = new ColorMatrix();
	        blackWhiteCM.setSaturation(0);
	        if(blackWhiteCCF == null)blackWhiteCCF = new ColorMatrixColorFilter(blackWhiteCM);
	        paint.setColorFilter(blackWhiteCCF);
    	}
        canvas.drawBitmap(scaledSrcBmp,new Rect(sx,sy,sx+diameter,sy+diameter), rect, paint);

        return output;
    }

    public void setShowTextBackground(Boolean showTextBackground){
        this.showTextBackground = showTextBackground;
    }

	public boolean isBlackWhiteMode() {
		return blackWhiteMode;
	}

	public void setBlackWhiteMode(boolean blackWhiteMode) {
		this.blackWhiteMode = blackWhiteMode;
		this.postInvalidate();
	}
	
	public  int dip2px(Context context, float dipValue)
	{
        final float scale = context.getResources().getDisplayMetrics().density; 
        return (int)(dipValue * scale + 0.5f); 
	} 
	
}
