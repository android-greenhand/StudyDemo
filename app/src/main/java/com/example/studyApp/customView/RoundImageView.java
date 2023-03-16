package com.example.studyApp.customView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;


public class RoundImageView extends ImageView {
    private Paint paint;
    private Xfermode xfermode;

    public RoundImageView(Context context) {
        super(context);
        init();
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        paint = new Paint();
        xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            super.onDraw(canvas);
            return;
        }
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        int width = getWidth();
        int height = getHeight();

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas tempCanvas = new Canvas(output);
        RectF rectF = new RectF(0, 0, width, height);
        tempCanvas.drawRoundRect(rectF, 50, 50, paint);

        paint.setXfermode(xfermode);
        tempCanvas.drawBitmap(bitmap, 0, 0, paint);
        paint.setXfermode(null);
        canvas.drawBitmap(output, 0, 0, paint);
    }
}
