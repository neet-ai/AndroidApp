/**
 * Copyright 2015-present Yukari Sakurai
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package info.neet_ai.machi_kiku;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

public class CircleImageView extends ImageView {
    private int canvasSize;
    private Bitmap image;
    private Paint paint;

    public CircleImageView(Context context) {
        super(context, null);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setAntiAlias(true);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        BitmapDrawable drawable = (BitmapDrawable) getDrawable();

        if (drawable == null) return;
        if (getWidth() == 0 || getHeight() == 0) return;

        Bitmap srcBmp = drawable.getBitmap();
        if (srcBmp == null) return;

        Bitmap image = getSquareBitmap(srcBmp);

        canvasSize = canvas.getWidth();
        if (canvas.getHeight() < canvasSize)
            canvasSize = canvas.getHeight();

        BitmapShader shader = new BitmapShader(Bitmap.createScaledBitmap(image, canvasSize, canvasSize, false), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(shader);

        int circleCenter = canvasSize / 2;
        canvas.drawCircle(circleCenter, circleCenter, circleCenter - 1, paint);
    }

    private Bitmap getSquareBitmap(Bitmap srcBmp) {
        if (srcBmp.getWidth() == srcBmp.getHeight()) return srcBmp;

        //Rectangle to square. Equivarent to ScaleType.CENTER_CROP
        int dim = Math.min(srcBmp.getWidth(), srcBmp.getHeight());
        Bitmap dstBmp = Bitmap.createBitmap(dim, dim, Bitmap.Config.ARGB_4444);

        Canvas canvas = new Canvas(dstBmp);
        float left = srcBmp.getWidth() > dim ? (dim - srcBmp.getWidth()) / 2 : 0;
        float top = srcBmp.getHeight() > dim ? ((dim - srcBmp.getHeight()) / 2) : 0;
        canvas.drawBitmap(srcBmp, left, top, null);

        return dstBmp;
    }

    /*private Bitmap resizeBitmap(Drawable bitmapfile){

        InputStream inputStream = getContentResolver().openInputStream(data.getData());

        // 画像サイズ情報を取得する
        BitmapFactory.Options imageOptions = new BitmapFactory.Options();
        imageOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, imageOptions);
        Log.v("image", "Original Image Size: " + imageOptions.outWidth + " x " + imageOptions.outHeight);

        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // もし、画像が大きかったら縮小して読み込む
        //  今回はimageSizeMaxの大きさに合わせる
        Bitmap bitmap;
        int imageSizeMax = 50;
        inputStream = getContentResolver().openInputStream(data.getData());
        float imageScaleWidth = (float)imageOptions.outWidth / imageSizeMax;
        float imageScaleHeight = (float)imageOptions.outHeight / imageSizeMax;

        // もしも、縮小できるサイズならば、縮小して読み込む
        if (imageScaleWidth > 2 && imageScaleHeight > 2) {
            BitmapFactory.Options imageOptions2 = new BitmapFactory.Options();

            // 縦横、小さい方に縮小するスケールを合わせる
            int imageScale = (int)Math.floor((imageScaleWidth > imageScaleHeight ? imageScaleHeight : imageScaleWidth));

            // inSampleSizeには2のべき上が入るべきなので、imageScaleに最も近く、かつそれ以下の2のべき上の数を探す
            for (int i = 2; i <= imageScale; i *= 2) {
                imageOptions2.inSampleSize = i;
            }

            bitmap = BitmapFactory.decodeStream(inputStream, null, imageOptions2);
            Log.v("image", "Sample Size: 1/" + imageOptions2.inSampleSize);
        } else {
            bitmap = BitmapFactory.decodeStream(inputStream);
        }

        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // The parent has determined an exact size for the child.
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            // The child can be as large as it wants up to the specified size.
            result = specSize;
        } else {
            // The parent has not imposed any constraint on the child.
            result = canvasSize;
        }

        return result;
    }

    private int measureHeight(int measureSpecHeight) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpecHeight);
        int specSize = MeasureSpec.getSize(measureSpecHeight);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            // The child can be as large as it wants up to the specified size.
            result = specSize;
        } else {
            // Measure the text (beware: ascent is a negative number)
            result = canvasSize;
        }

        return (result + 2);
    }
}