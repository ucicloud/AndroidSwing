package com.kidsdynamic.swing.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewTreeObserver;

import com.kidsdynamic.commonlib.utils.FileUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * WrhImageView
 * <p>
 * Created by Administrator on 2015/9/7 0007.
 */
public class CropImageView extends AppCompatImageView implements ViewTreeObserver.OnGlobalLayoutListener,
        ScaleGestureDetector.OnScaleGestureListener {

    private static final float SCALE_MAX = 3.0f;

    private static int mRadius = 240;
    private Bitmap.CompressFormat mCompressFormat;
    private int mCompressQuality;
    private boolean isCropCircle;
    private int mPaintColor;

    private Paint mPaint;
    private RectF shelterR;
    private RectF circleR;

    private float mLastX;
    private float mLastY;
    private float initScale = 1.0f;
    private int lastPointerCount;

    /**
     * 用于存放矩阵的9个值
     */
    private final float[] matrixValues = new float[9];
    private final Matrix mScaleMatrix = new Matrix();
    /**
     * OnGlobalLayout是否为第一次执行
     */
    private boolean isOnGlobalLayoutOnce = true;
    private boolean isAutoScale;
    /**
     * 双击的手势检测
     */
    private GestureDetector mGestureDetector;
    /**
     * 缩放的手势检测
     */
    private ScaleGestureDetector mScaleGestureDetector = null;
    private OnCropImageCompleteListener mOnCropImageCompleteListener;
    /**
     * 裁剪task的弱引用
     */
    private WeakReference<BitmapCroppingWorkerTask> mBitmapCroppingWorkerTask;
    private Xfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);

    public CropImageView(Context context) {
        this(context, null);
    }

    public CropImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setScaleType(ScaleType.MATRIX);
        init(context);
    }

    public void init(Context context) {
        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        mRadius = (int) (screenWidth * 0.382f);
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        mGestureDetector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        if (isAutoScale) return true;
                        float x = e.getX();
                        float y = e.getY();
                        if (getScale() < SCALE_MAX) {
                            CropImageView.this.postDelayed(
                                    new AutoScaleRunnable(SCALE_MAX, x, y), 16);
                            isAutoScale = true;
                        } else {
                            CropImageView.this.postDelayed(
                                    new AutoScaleRunnable(initScale, x, y), 16);
                            isAutoScale = true;
                        }

                        return true;
                    }
                });
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(2);
        mPaint.setAntiAlias(true);
        mPaint.setColor(mPaintColor);
    }

    private class AutoScaleRunnable implements Runnable {
        static final float BIGGER = 1.07f;
        static final float SMALLER = 0.93f;
        private float mTargetScale;
        private float tmpScale;

        /**
         * 缩放的中心
         */
        private float x;
        private float y;

        /**
         * 传入目标缩放值，根据目标值与当前值，判断应该放大还是缩小
         *
         * @param targetScale float
         * @param x           float
         * @param y           float
         */
        private AutoScaleRunnable(float targetScale, float x, float y) {
            this.mTargetScale = targetScale;
            this.x = x;
            this.y = y;
            if (getScale() < mTargetScale) {
                tmpScale = BIGGER;
            } else {
                tmpScale = SMALLER;
            }
        }

        @Override
        public void run() {
            // 进行缩放
            mScaleMatrix.postScale(tmpScale, tmpScale, x, y);
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);

            final float currentScale = getScale();
            // 如果值在合法范围内，继续缩放
            if ((tmpScale > 1f && currentScale < mTargetScale)
                    || (tmpScale < 1f && mTargetScale < currentScale)) {
                CropImageView.this.postDelayed(this, 16);
            } else
            // 设置为目标的缩放比例
            {
                final float deltaScale = mTargetScale / currentScale;
                mScaleMatrix.postScale(deltaScale, deltaScale, x, y);
                checkBorderAndCenterWhenScale();
                setImageMatrix(mScaleMatrix);
                isAutoScale = false;
            }

        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getDrawable() == null) {
            return;
        }
        getShelterRectF();
        // 画入前景圆形蒙板层
        int sc = canvas.saveLayer(shelterR, null, Canvas.ALL_SAVE_FLAG);
        canvas.drawRect(shelterR, mPaint);
        mPaint.setXfermode(xfermode);
        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, mRadius, mPaint);
        if (circleR == null) {
            circleR = new RectF(getWidth() / 2 - mRadius,
                    getHeight() / 2 - mRadius,
                    getWidth() / 2 + mRadius,
                    getHeight() / 2 + mRadius);
        }
        canvas.restoreToCount(sc);
        mPaint.setXfermode(null);
        mPaint.setColor(mPaintColor);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event))
            return true;
        mScaleGestureDetector.onTouchEvent(event);
        float x = 0, y = 0;
        // 拿到触摸点的个数
        final int pointerCount = event.getPointerCount();
        // 得到多个触摸点的x与y均值
        for (int i = 0; i < pointerCount; i++) {
            x += event.getX(i);
            y += event.getY(i);
        }
        x = x / pointerCount;
        y = y / pointerCount;
        // 每当触摸点发生变化时，重置mLasX , mLastY
        if (pointerCount != lastPointerCount) {
            mLastX = x;
            mLastY = y;
        }
        lastPointerCount = pointerCount;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = x - mLastX;
                float dy = y - mLastY;
                if (null == shelterR) {
                    return true;
                }
                if (shelterR.left + dx >= circleR.left || shelterR.right + dx <= circleR.right) {
                    dx = 0;
                }
                if (shelterR.top + dy > circleR.top || shelterR.bottom + dy <= circleR.bottom) {
                    dy = 0;
                }
                mScaleMatrix.postTranslate(dx, dy);
                setImageMatrix(mScaleMatrix);
                mLastX = x;
                mLastY = y;
                postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                lastPointerCount = 0;
                break;
        }
        return true;
    }

    @Override
    public void onGlobalLayout() {
        if (isOnGlobalLayoutOnce) {
            Drawable d = getDrawable();
            if (d == null)
                return;
            int width = getWidth();
            int height = getHeight();
            int diameter = mRadius * 2;
            int dw = d.getIntrinsicWidth();
            int dh = d.getIntrinsicHeight();
            float scale = 1.0f;
            if (dw < diameter && dh > diameter) {
                scale = diameter * 1.0f / dw;
            }
            if (dh < diameter && dw > diameter) {
                scale = diameter * 1.0f / dh;
            }
            if (dh < diameter && dw < diameter) {
                scale = Math.max(diameter * 1.0f / dw, diameter * 1.0f / dh);
            }
            initScale = scale;
            // 图片移动至屏幕中心
            mScaleMatrix.postTranslate((width - dw) / 2, (height - dh) / 2);
            mScaleMatrix.postScale(scale, scale, getWidth() / 2, getHeight() / 2);
            setImageMatrix(mScaleMatrix);
            isOnGlobalLayoutOnce = false;
        }
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scale = getScale();
        float scaleFactor = detector.getScaleFactor();

        if (getDrawable() == null)
            return true;

        // 缩放的范围控制
        if ((scale < SCALE_MAX && scaleFactor > 1.0f)
                || (scale > initScale && scaleFactor < 1.0f)) {
            // 最大值最小值判断
            if (scaleFactor * scale < initScale) {
                scaleFactor = initScale / scale;
            }
            if (scaleFactor * scale > SCALE_MAX) {
                scaleFactor = SCALE_MAX / scale;
            }
            // 设置缩放比例
            mScaleMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusX());
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);
        }
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    public void setPaintColor(int paintColor) {
        mPaintColor = paintColor;
    }

    public void setCropCircle(boolean cropCircle) {
        isCropCircle = cropCircle;
    }

    public void setCompressFormat(Bitmap.CompressFormat compressFormat) {
        mCompressFormat = compressFormat;
    }

    public void setmCompressQuality(int compressQuality) {
        mCompressQuality = compressQuality;
    }

    public void getCroppedImageAsync() {
        BitmapCroppingWorkerTask currentTask =
                mBitmapCroppingWorkerTask != null ? mBitmapCroppingWorkerTask.get() : null;
        if (currentTask != null) {
            // cancel previous cropping
            currentTask.cancel(true);
        }
        mBitmapCroppingWorkerTask = new WeakReference<>(
                new BitmapCroppingWorkerTask(this, isCropCircle, mCompressFormat,
                        mCompressQuality));
        mBitmapCroppingWorkerTask.get().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    void onImageCroppingAsyncComplete(CropResult result) {
        mBitmapCroppingWorkerTask = null;
        if (null != mOnCropImageCompleteListener) {
            mOnCropImageCompleteListener.onCropImageComplete(this, result);
        }
    }

    public void setOnCropImageCompleteListener(OnCropImageCompleteListener listener) {
        mOnCropImageCompleteListener = listener;
    }

    public interface OnCropImageCompleteListener {
        void onCropImageComplete(CropImageView view, CropResult result);
    }

    private static class BitmapCroppingWorkerTask extends AsyncTask<Void, Void, CropResult> {

        private final WeakReference<CropImageView> mCropImageViewReference;
        private final Drawable mDrawable;
        private final float sX, sY, mLeft, mTop;
        private final boolean mIsCropCircle;
        private final Bitmap.CompressFormat mSaveCompressFormat;
        private final int mSaveCompressQuality;

        BitmapCroppingWorkerTask(CropImageView cropImageView,
                                 boolean isCropCircle,
                                 Bitmap.CompressFormat saveCompressFormat,
                                 int saveCompressQuality) {
            mCropImageViewReference = new WeakReference<>(cropImageView);
            mDrawable = cropImageView.getDrawable();
            sX = cropImageView.getScale();
            sY = cropImageView.getScale();
            int dw = mDrawable.getIntrinsicWidth();
            int dh = mDrawable.getIntrinsicHeight();
            float x = cropImageView.getTranslateX() - (cropImageView.getWidth() - dw) / 2;
            float y = cropImageView.getTranslateY() - (cropImageView.getHeight() - dh) / 2;
            mLeft = -(dw / 2 - mRadius) + x;
            mTop = -(dh / 2 - mRadius) + y;
            mIsCropCircle = isCropCircle;
            mSaveCompressFormat = saveCompressFormat;
            mSaveCompressQuality = saveCompressQuality;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected CropResult doInBackground(Void... voids) {
            if (isCancelled()) {
                return null;
            }
            try {
                Bitmap bitmap = cropBitmap(mDrawable, sX, sY, mLeft, mTop, mIsCropCircle);
                File file = saveBitmap(bitmap, mSaveCompressFormat, mSaveCompressQuality);
                return new CropResult(bitmap, file, null);
            } catch (Exception e) {
                e.printStackTrace();
                return new CropResult(null, null, e);
            }
        }

        @Override
        protected void onPostExecute(CropResult result) {
            super.onPostExecute(result);
            if (null == result) {
                return;
            }
            if (isCancelled()) {
                return;
            }
            CropImageView cropImageView = mCropImageViewReference.get();
            if (null != cropImageView) {
                cropImageView.onImageCroppingAsyncComplete(result);
            }
        }

    }

    public static final class CropResult {
        public final Bitmap bitmap;
        public final File file;
        public final Exception error;

        public CropResult(Bitmap bitmap, File file, Exception error) {
            this.bitmap = bitmap;
            this.file = file;
            this.error = error;
        }
    }

    /**
     * 剪裁
     *
     * @return Bitmap
     */
    private static Bitmap cropBitmap(Drawable drawable, float sx, float sy, float left, float top,
                                     boolean isCropCircle)
            throws IOException {
        if (null == drawable) {
            return null;
        }
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        if (null == bitmap) {
            return null;
        }
        bitmap = zoomBitmap(bitmap, sx, sy);
//        Bitmap target = Bitmap.createBitmap(mRadius * 2, mRadius * 2, Bitmap.Config.ARGB_4444);
        Bitmap target = Bitmap.createBitmap(mRadius * 2, mRadius * 2, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(target);
        canvas.drawCircle(mRadius, mRadius, mRadius, paint);
        if (isCropCircle) {
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        }
        canvas.drawBitmap(bitmap, left, top, paint);
        return target;
    }

    /**
     * 位图缩放
     *
     * @param bitmap Bitmap
     * @return Bitmap
     */
    private static Bitmap zoomBitmap(Bitmap bitmap, float sx, float sy) {
        if (null == bitmap) {
            return null;
        }
        int imageWidth = bitmap.getWidth();
        int imageHeight = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(sx, sy);
        return Bitmap.createBitmap(bitmap, 0, 0, imageWidth, imageHeight, matrix, true);
    }

    private static File saveBitmap(Bitmap bitmap, Bitmap.CompressFormat compressFormat, int quality) throws IOException {
        if (null == bitmap) {
            return null;
        }
        File imgFile;
        FileOutputStream fos = null;
        ByteArrayOutputStream baos = null;

        try {
            File curFileDir = FileUtil.getAppImageFolder();
            if (!curFileDir.exists() && curFileDir.mkdirs()) {
                return null;
            }
            String fileName = FileUtil.getFileNameByTimeStamp();
            String postfix = ".jpg";
            if (Bitmap.CompressFormat.PNG == compressFormat) {
                postfix = ".png";
            } else if (Bitmap.CompressFormat.WEBP == compressFormat) {
                postfix = ".webp";
            }
            imgFile = new File(curFileDir, fileName + postfix);
            if (imgFile.exists() && !imgFile.delete()) {
                return null;
            }
            if (imgFile.createNewFile()) {
//                bitmap.setConfig(Bitmap.Config.ARGB_4444);
                bitmap.setConfig(Bitmap.Config.RGB_565);
                baos = new ByteArrayOutputStream();
                if (null == compressFormat) {
                    compressFormat = Bitmap.CompressFormat.JPEG;
                }
                if (0 == quality) {
//                    quality = 30;
                    quality = 1;
                }
                bitmap.compress(compressFormat, quality, baos);
                fos = new FileOutputStream(imgFile);
                fos.write(baos.toByteArray());
                fos.flush();
                fos.close();
            }
        } finally {
            try {
                if (null != fos) {
                    fos.flush();
                    fos.close();
                }
                if (null != baos) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return imgFile;
    }

    /**
     * 在缩放时，进行图片显示范围的控制
     */
    private void checkBorderAndCenterWhenScale() {
        RectF rect = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

        // 如果宽或高大于屏幕，则控制范围
        if (rect.width() >= width) {
            if (rect.left > 0) {
                deltaX = -rect.left;
            }
            if (rect.right < width) {
                deltaX = width - rect.right;
            }
        }
        if (rect.height() >= height) {
            if (rect.top > 0) {
                deltaY = -rect.top;
            }
            if (rect.bottom < height) {
                deltaY = height - rect.bottom;
            }
        }
        // 如果宽或高小于屏幕，则让其居中
        if (rect.width() < width) {
            deltaX = width * 0.5f - rect.right + 0.5f * rect.width();
        }
        if (rect.height() < height) {
            deltaY = height * 0.5f - rect.bottom + 0.5f * rect.height();
        }
        mScaleMatrix.postTranslate(deltaX, deltaY);
    }

    /**
     * 根据当前图片的Matrix获得图片的范围
     *
     * @return RectF
     */
    private RectF getMatrixRectF() {
        RectF rect = new RectF();
        Drawable d = getDrawable();
        if (null != d) {
            rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            mScaleMatrix.mapRect(rect);
        }
        return rect;
    }

    private float getScale() {
        mScaleMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }

    private float getTranslateX() {
        mScaleMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MTRANS_X];
    }

    private float getTranslateY() {
        mScaleMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MTRANS_Y];
    }

    /**
     * 用来保证阴影的大小足以遮住图片
     */
    private void getShelterRectF() {
        Drawable drawable = getDrawable();
        if (null == drawable) {
            return;
        }
        float x = (int) getTranslateX();
        float y = (int) getTranslateY();
        float width = drawable.getIntrinsicWidth() * getScale();
        float height = drawable.getIntrinsicHeight() * getScale();
        if (shelterR == null) {
            shelterR = new RectF(x, y, width + x, height + y);
        } else {
            shelterR.set(x, y, width + x, height + y);
        }
    }

}
