package livefyre.streamhub;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import android.net.Uri;
import android.net.Uri.Builder;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

/**
 * @author Kvana Dev
 */
public class AdminClient {
    /**
     * Performs a network request on a different thread and delivers a message to the callback.
     * A JSON object with the results will be bound to the message.
     * It is necessary to provide either a collectionId or a siteId combined with an articleId.
     *
     * @param userToken    The lftoken representing a user.
     * @param collectionId The Id of the collection to auth against.
     * @param articleId    The Id of the collection's article.
     * @param siteId       The Id of the article's site.
     * @param handler      Implement "handleMessage" for this callback.
     * @throws UnsupportedEncodingException
     * @throws MalformedURLException
     */
    public static void authenticateUser(String userToken,
                                        String collectionId,
                                        String articleId,
                                        String siteId,
                                        JsonHttpResponseHandler handler)
            throws UnsupportedEncodingException {
        final String authEndpoint =
                generateAuthEndpoint(userToken, collectionId, articleId, siteId);
        HttpClient.client.get(authEndpoint, handler);
    }

    /**
     * Generates an auth endpoint with the specified parameters.
     *
     * @param userToken    The lftoken representing a user.
     * @param collectionId The Id of the collection to auth against.
     * @param articleId    The Id of the collection's article.
     * @param siteId       The Id of the article's site.
     * @return The auth endpoint with the specified parameters.
     * @throws UnsupportedEncodingException
     * @throws MalformedURLException
     */
    public static String generateAuthEndpoint(String userToken,
                                              String collectionId,
                                              String articleId,
                                              String siteId)
            throws UnsupportedEncodingException {
        Builder uriBuilder = new Uri.Builder()
                .scheme(LivefyreConfig.scheme)
                .authority(LivefyreConfig.adminDomain + "." + LivefyreConfig.getConfiguredNetworkID())
                .appendPath("api")
                .appendPath("v3.0")
                .appendPath("auth")
                .appendPath("");

        if (collectionId != null) {
            uriBuilder
                    .appendQueryParameter("collectionId", collectionId)
                    .appendQueryParameter("lftoken", userToken);
        } else {
            final String article64 = Helpers.generateBase64String(articleId);
            uriBuilder
                    .appendQueryParameter("siteId", siteId)
                    .appendQueryParameter("articleId", article64)
                    .appendQueryParameter("lftoken", userToken);
        }
        Log.d("Admin URL", "" + uriBuilder.toString());
        return uriBuilder.toString();
    }
}