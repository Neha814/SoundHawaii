package lazyadapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.example.soundhawaiiapp.R;

public class ImageLoader {
    
    static MemoryCache memoryCache=new MemoryCache();
    static  FileCache fileCache;
    private Map<ImageView, String> imageViews=Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService;
    Handler handler=new Handler();//handler to display images in UI thread
    
    public ImageLoader(Context context){
        fileCache=new FileCache(context);
        executorService=Executors.newFixedThreadPool(5);
    }
    
    final int stub_id=R.drawable.circle_loading;
    final int no_image=R.drawable.noimage;
   // final int stub_id=R.drawable.f_artist96;
    public void DisplayImage(String url, ImageView imageView,ScaleType scaleType)
    {
        imageViews.put(imageView, url);
        Bitmap bitmap=memoryCache.get(url);
        imageView.destroyDrawingCache();
        
        if(bitmap!=null)
        {
        	imageView.setScaleType(scaleType);
            imageView.setImageBitmap(bitmap);
        }
        else
        {
        	imageView.setScaleType(scaleType);
        	imageView.setImageResource(stub_id);
            queuePhoto(url, imageView,scaleType);
        }
    }
 /*   public void DisplayAlbumImage(String url,ImageView imageView){
    	imageViews.put(imageView, url);
        Bitmap bitmap=memoryCache.get(url);
        imageView.destroyDrawingCache();
        
        if(bitmap!=null)
        {
        	imageView.setScaleType(ScaleType.FIT_CENTER);
            imageView.setImageBitmap(bitmap);
        }
        else
        {
        	imageView.setScaleType(ScaleType.FIT_CENTER);
        	imageView.setImageResource(stub_id);
            queuePhoto(url, imageView);
        }
    }*/
        
    private void queuePhoto(String url, ImageView imageView,ScaleType type)
    {
        PhotoToLoad p=new PhotoToLoad(url, imageView,type);
        executorService.submit(new PhotosLoader(p));
    }
    
    private Bitmap getBitmap(String url) 
    {
        File f=fileCache.getFile(url);
        
        //from SD cache
        Bitmap b = decodeFile(f);
        if(b!=null)
            return b;
        
        //from web
        try {
            Bitmap bitmap=null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is=conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            Utils.CopyStream(is, os);
            os.close();
            conn.disconnect();
            bitmap = decodeFile(f);
            return bitmap;
        } catch (Throwable ex){
           ex.printStackTrace();
           if(ex instanceof OutOfMemoryError)
               memoryCache.clear();
           return null;
        }
    }

    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f){
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream stream1=new FileInputStream(f);
            BitmapFactory.decodeStream(stream1,null,o);
            stream1.close();
            
            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE=300;
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true){
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale*=2;
            }
            
            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            FileInputStream stream2=new FileInputStream(f);
            Bitmap bitmap=BitmapFactory.decodeStream(stream2, null, o2);
            stream2.close();
            return bitmap;
        } catch (FileNotFoundException e) {
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    //Task for the queue
    private class PhotoToLoad
    {
        public String url;
        public ImageView imageView;
        public ScaleType scaletype;
        public PhotoToLoad(String u, ImageView i,ScaleType type){
            url=u; 
            scaletype=type;
            imageView=i;
        }
    }
    
    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;
        PhotosLoader(PhotoToLoad photoToLoad){
            this.photoToLoad=photoToLoad;
        }
        
        @Override
        public void run() {
            try{
                if(imageViewReused(photoToLoad))
                    return;
                Bitmap bmp=getBitmap(photoToLoad.url);
                memoryCache.put(photoToLoad.url, bmp);
                if(imageViewReused(photoToLoad))
                    return;
                BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad,photoToLoad.scaletype);
                handler.post(bd);
            }catch(Throwable th){
                th.printStackTrace();
            }
        }
    }
    
    boolean imageViewReused(PhotoToLoad photoToLoad){
        String tag=imageViews.get(photoToLoad.imageView);
        if(tag==null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }
    
    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;
        ScaleType scaletype;
        public BitmapDisplayer(Bitmap b, PhotoToLoad p,ScaleType type){bitmap=b;photoToLoad=p;scaletype=type;}
        public void run()
        {
        	photoToLoad.imageView.setScaleType(scaletype);
            if(imageViewReused(photoToLoad))
                return;
            if(bitmap!=null)
                photoToLoad.imageView.setImageBitmap(bitmap);
            else
                photoToLoad.imageView.setImageResource(no_image);
        }
    }

    public static void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }
	public static Bitmap getRoundedRectBitmap(Bitmap bitmap) {
		Bitmap result = null;
	try {
			result = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
					Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(result);

			int color = 0xff424242;
			Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, bitmap.getWidth(),
					bitmap.getHeight());
			final RectF rectF = new RectF(rect);
//
			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
			canvas.drawOval(rectF, paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(bitmap, rect, rect, paint);
			clearCache();
//			if (bitmap != null && !bitmap.isRecycled()) {
//				recycle();
//				bitmap = null; 
//			}
		//	bitmap.recycle();
		} catch (NullPointerException e) {
			
		} 
		catch (OutOfMemoryError o) {
			
		}
		return result;
	}
}
