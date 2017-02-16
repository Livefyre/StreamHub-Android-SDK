package livefyre.ImagesCache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	private int inSampleSize = 0;

	private String imageUrl;

	private BaseAdapter adapter;

	private ImagesCache cache;

	private Bitmap image = null;

	private ImageView ivImageView;

	public DownloadImageTask(BaseAdapter adapter) {
		this.adapter = adapter;

		this.cache = ImagesCache.getInstance();

	}

	public DownloadImageTask(ImagesCache cache, ImageView ivImageView) {
		this.cache = cache;

		this.ivImageView = ivImageView;

	}

	@Override
	protected Bitmap doInBackground(String... params) {
		imageUrl = params[0];

		return getImage(imageUrl);
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		super.onPostExecute(result);

		if (result != null) {
			cache.addImageToWarehouse(imageUrl, result);

			if (ivImageView != null) {
				ivImageView.setImageBitmap(result);
			} else {

			}

			if (adapter != null) {
				adapter.notifyDataSetChanged();
			}
		}
	}

	private Bitmap getImage(String imageUrl) {
		if (cache.getImageFromWarehouse(imageUrl) == null) {
			BitmapFactory.Options options = new BitmapFactory.Options();

			options.inJustDecodeBounds = true;

			options.inSampleSize = inSampleSize;

			try {
				URL url = new URL(imageUrl);

				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();

				InputStream stream = connection.getInputStream();

				image = BitmapFactory.decodeStream(stream, null, options);

				int imageWidth = options.outWidth;

				int imageHeight = options.outHeight;

				options.inJustDecodeBounds = false;

				connection = (HttpURLConnection) url.openConnection();

				stream = connection.getInputStream();

				image = BitmapFactory.decodeStream(stream, null, options);
				Log.d("image", image.toString());
				return image;
			}

			catch (Exception e) {
				Log.e("getImage", e.toString());
			}
		}

		return image;
	}

	public static void getRoundedShape(ImageView scaleBitmapImageview) {
		Drawable scaleDrawable = scaleBitmapImageview.getDrawable();
		BitmapDrawable bitmapDrawable = ((BitmapDrawable) scaleDrawable);
		Bitmap bitmap = bitmapDrawable.getBitmap();
		Bitmap targetBitmap = null;
		try {
			int targetWidth = 100;
			int targetHeight = 100;
			targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight,
					Bitmap.Config.ARGB_8888);

			Canvas canvas = new Canvas(targetBitmap);
			Path path = new Path();
			path.addCircle(
					((float) targetWidth - 1) / 2,
					((float) targetHeight - 1) / 2,
					(Math.min(((float) targetWidth), ((float) targetHeight)) / 2),
					Path.Direction.CCW);

			canvas.clipPath(path);
			Bitmap sourceBitmap = bitmap;
			canvas.drawBitmap(
					sourceBitmap,
					new Rect(0, 0, sourceBitmap.getWidth(), sourceBitmap
							.getHeight()), new Rect(0, 0, targetWidth,
							targetHeight), null);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		scaleBitmapImageview.setImageBitmap(targetBitmap);
		// return targetBitmap;
	}

}