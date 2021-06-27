package tz.co.xhcodes.com;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by iwachu on 7/27/17.
 */

public class CustomScrollView extends ScrollView {
    private Paint paint;
    private Bitmap bitmap;
    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.main_img);
        paint.setShader(new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
        setWillNotDraw(false);
    }
    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        canvas.drawPaint(paint);
    }
}
