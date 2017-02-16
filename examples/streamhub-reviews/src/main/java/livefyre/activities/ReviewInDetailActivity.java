package livefyre.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import livefyre.BaseActivity;
import livefyre.LFSAppConstants;
import livefyre.LFUtils;
import livefyre.R;
import livefyre.adapters.ReviewInDetailAdapter;
import livefyre.models.Attachments;
import livefyre.models.Content;
import livefyre.parsers.ContentParser;

public class ReviewInDetailActivity extends BaseActivity {
    ImageView image_header;
    static ReviewInDetailAdapter reviewInDetailAdapter;
    Content selectedReviews;
    static String reviewId;
    static List<Content> reviewCollectiontoBuild;
    RecyclerView recyclerView;
    static List<Content> childMap;
    private RecyclerView.LayoutManager mLayoutManager;
    private WebView webView;

    @Override
    protected void onResume() {
        super.onResume();
        populateData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_indetail);

        init();
        getDataFromIntent();
        setData();
        buildList();
    }

    View.OnClickListener notificationHandler = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            populateData();
            int position = 0;
            List<Content> tempList = new ArrayList(reviewCollectiontoBuild);
            if (tempList.size() > 0) {
                Collections.sort(tempList, new Comparator<Content>() {
                    @Override
                    public int compare(Content p2, Content p1) {
                        return Integer.parseInt(p1.getCreatedAt())
                                - Integer.parseInt(p2.getCreatedAt());
                    }
                });
                String latestContentId = tempList.get(0).getId();

                for (int i = 0; i < reviewCollectiontoBuild.size(); i++) {
                    Content b = reviewCollectiontoBuild.get(i);
                    if (b.getId().equals(latestContentId)) {
                        position = i;
                    }
                }
            }
            recyclerView.smoothScrollToPosition(position + 1);
        }
    };

    private void init() {
        image_header = (ImageView) findViewById(R.id.image_header);
        recyclerView = (RecyclerView) findViewById(R.id.commentsRV);
        webView = (WebView) findViewById(R.id.videoPlayer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (!isNetworkAvailable()) {
            showToast("Network Not Available");
            return;
        }
    }

    private void populateData() {
        reviewCollectiontoBuild = new ArrayList();
        Content mContentBean = ContentParser.ContentMap
                .get(reviewId);
        mContentBean.setNewReplyCount(0);
        reviewCollectiontoBuild.add(0, mContentBean);
        childMap = ContentParser.getChildForContent(reviewId);
        if (childMap != null)
            for (Content content : childMap) {
                reviewCollectiontoBuild.add(content);
            }
        if (reviewInDetailAdapter != null) {
            reviewInDetailAdapter.updateReviewInDetailAdapter(reviewCollectiontoBuild);
        }
    }

    public static void notifyDatainDetail() {
        if (reviewInDetailAdapter != null)
            reviewInDetailAdapter.notifyDataSetChanged();
        if (reviewCollectiontoBuild != null) {
            childMap = ContentParser.getChildForContent(reviewId);
            List<Content> childTemp = new ArrayList<Content>();
            if (childMap != null)
                for (Content content : childMap) {
                    childTemp.add(content);
                }
            int newReplyCount = childTemp.size()
                    - (reviewCollectiontoBuild.size() - 1);
            Log.d("New Count", "" + childTemp.size());
            Log.d("Old Count", "" + reviewCollectiontoBuild.size());

            if (newReplyCount > 0) {
                Content mContentBean = ContentParser.ContentMap
                        .get(reviewId);
                mContentBean.setNewReplyCount(newReplyCount);
            }
        }
    }

    private void buildList() {
        populateData();
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        reviewInDetailAdapter = (ReviewInDetailAdapter) recyclerView.getAdapter();

        if (reviewInDetailAdapter != null) {

            reviewInDetailAdapter.updateReviewInDetailAdapter(reviewCollectiontoBuild);
        } else {
            reviewInDetailAdapter = new ReviewInDetailAdapter(ReviewInDetailActivity.this, getApplicationContext(), reviewCollectiontoBuild, notificationHandler, selectedReviews.getId());
            recyclerView.setAdapter(reviewInDetailAdapter);
        }
    }

    void getDataFromIntent() {
        Intent intent = getIntent();
        reviewId = intent.getStringExtra(LFSAppConstants.ID);
    }

    void setData() {

        selectedReviews = ContentParser.ContentMap.get(reviewId);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(selectedReviews.getTitle());
        List<Attachments> img = selectedReviews.getAttachments();
        if (img != null) {
            if (img.size() > 0) {
                Attachments mAttachments = selectedReviews.getAttachments().get(0);
                if (mAttachments.getType().equals("video")) {
                    if (mAttachments.getThumbnail_url() != null) {
                        if (mAttachments.getThumbnail_url().length() > 0) {
                            application.printLog(true, "comment.getAttachments()", selectedReviews.getAttachments() + " URL");
                            image_header.setVisibility(View.GONE);
                            webView.setVisibility(View.VISIBLE);
                            webView.setWebViewClient(new WebViewClient());
                            webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
                            webView.setInitialScale(140);
                            webView.getSettings().setJavaScriptEnabled(true);
                            webView.getSettings().setDomStorageEnabled(true);
                            if (mAttachments.getProvider_name().equals("YouTube")) {
                                int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, this.getResources().getDisplayMetrics());
                                webView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, height));
                                String youtubeId = LFUtils.getYoutubeVideoId(mAttachments.getUrl());
                                webView.loadUrl("https://www.youtube.com/embed/" + youtubeId);
                            } else {
                                webView.loadDataWithBaseURL(mAttachments.getLink(), mAttachments.getHTML(), "text/html", "UTF-8", "");
                            }
                        }
                    }
                } else {
                    if (mAttachments.getUrl() != null) {
                        if (mAttachments.getUrl().length() > 0) {
                            image_header.setVisibility(View.VISIBLE);
                            webView.setVisibility(View.GONE);
                            application.printLog(true, "comment.getAttachments()", selectedReviews.getAttachments() + " URL");
                            Picasso.with(getApplication()).load(mAttachments.getUrl()).fit().into(image_header);
                        }
                    }
                }
            } else {
                image_header.setImageResource(R.mipmap.img_bac);
            }
        } else {
            image_header.setImageResource(R.mipmap.img_bac);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("result");
                Log.d("A", "RESULT_OK");
                populateData();

                int position = 0;
                List<Content> tempList = new ArrayList(reviewCollectiontoBuild);
                if (tempList.size() > 0) {
                    Collections.sort(tempList, new Comparator<Content>() {
                        @Override
                        public int compare(Content p2, Content p1) {
                            return Integer.parseInt(p1.getCreatedAt())
                                    - Integer.parseInt(p2.getCreatedAt());
                        }
                    });
                    String latestContentId = tempList.get(0).getId();

                    for (int i = 0; i < reviewCollectiontoBuild.size(); i++) {
                        Content b = reviewCollectiontoBuild.get(i);
                        if (b.getId().equals(latestContentId)) {

                            position = i;
                        }
                    }
                }
                recyclerView.smoothScrollToPosition(position + 1);
            }
            if (resultCode == RESULT_CANCELED) {
                Log.d("A", "RESULT_CANCELED");
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
