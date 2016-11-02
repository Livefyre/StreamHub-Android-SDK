package livefyre.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.livefyre.streamhub_android_sdk.network.WriteClient;
import com.livefyre.streamhub_android_sdk.util.LFSActions;
import com.livefyre.streamhub_android_sdk.util.LFSConstants;
import com.livefyre.streamhub_android_sdk.util.LFSFlag;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import livefyre.AppSingleton;
import livefyre.LFSAppConstants;
import livefyre.LFSConfig;
import livefyre.LFUtils;
import livefyre.LivefyreApplication;
import livefyre.R;
import livefyre.RoundedTransformation;
import livefyre.activities.EditReview;
import livefyre.activities.ReplyReview;
import livefyre.models.Content;
import livefyre.models.Vote;
import livefyre.parsers.ContentParser;

public class ReviewInDetailAdapter extends RecyclerView.Adapter<ReviewInDetailAdapter.MyViewHolder> {
    private static final int VIEW_COUNT = 3;
    private static final int DELETED = -1;
    private static final int PARENT = 0;
    private static final int CHILD = 1;
    OnClickListener notificationHandler;

    private ProgressDialog dialog;
    String mainReviewId;
    int helpfulFlag = 0;
    private LayoutInflater inflater;
    Context context;
    Activity activity;
    private LivefyreApplication application;

    private List<Content> ContentArray;

    public ReviewInDetailAdapter(Activity activity, Context context,
                                 List<Content> ContentArray, OnClickListener notificationHandler, String mainReviewId) {
        this.ContentArray = ContentArray;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.notificationHandler = notificationHandler;
        this.mainReviewId = mainReviewId;
        this.activity = activity;
        application = AppSingleton.getInstance().getApplication();
    }

    public void updateReviewInDetailAdapter(List<Content> ContentCollection) {
        this.ContentArray = ContentCollection;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return ContentArray.get(position).getContentType().getValue();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = null;
        View view = null;
        switch (viewType) {
            case PARENT:
                view = inflater.inflate(R.layout.layout_parent_row, parent, false);
                holder = new MyViewHolder(view);
                break;
            case CHILD:
                view = inflater.inflate(R.layout.layout_child_list_row, parent, false);
                holder = new MyViewHolder(view);

                break;
            case DELETED:
                view = inflater.inflate(R.layout.deleted_item, parent, false);
                holder = new MyViewHolder(view);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        int viewType = ContentArray.get(position).getContentType().getValue();
        final Content content = (Content) ContentArray.get(position);
        switch (viewType) {
            case PARENT:
                if (content.getNewReplyCount() > 0) {
                    if (content.getNewReplyCount() == 1)
                        holder.notifMsgTV.setText(content
                                .getNewReplyCount() + " New Reply");
                    else
                        holder.notifMsgTV.setText(content
                                .getNewReplyCount() + " New Replies");
                    holder.parentNotifRV.setVisibility(View.VISIBLE);
                } else {
                    holder.parentNotifRV.setVisibility(View.GONE);
                }
                //moderator
                if (content.getIsModerator() != null) {
                    if (content.getIsModerator().equals("true")) {
                        holder.isParentMod.setText("Moderator");
                        holder.isParentMod
                                .setTextColor(Color.parseColor("#0F98EC"));
                        holder.isParentMod.setVisibility(View.VISIBLE);
                        holder.mainReviewerDisplayName.setMaxWidth(330);
                    } else {
                        holder.isParentMod.setVisibility(View.GONE);
                    }
                } else {
                    holder.isParentMod.setVisibility(View.GONE);
                }
                //feature
                if (content.getIsFeatured() != null) {
                    if (content.getIsFeatured()) {
                        holder.isParentMod.setText("Feature");
                        holder.isParentMod
                                .setTextColor(Color.parseColor("#FEB33B"));
                        holder.isParentMod.setVisibility(View.VISIBLE);
                        holder.mainReviewerDisplayName.setMaxWidth(380);
                    }
                }
                if (content.getAuthor().getAvatar().length() > 0) {
                    Picasso.with(context).load(content.getAuthor().getAvatar()).fit().transform(new RoundedTransformation(90, 0)).into(holder.mainReviewerImage);
                } else {
                    Picasso.with(context).load(R.mipmap.profile_default).fit().transform(new RoundedTransformation(90, 0)).into(holder.mainReviewerImage);
                }
                if (content.getVote() != null) {// know helpful value and set color
                    if (content.getVote().size() > 0) {
                        helpfulFlag = 0;
                        helpfulFlag = knowHelpfulValue(
                                application
                                        .getDataFromSharedPreferences(LFSAppConstants.ID),
                                content.getVote());

                        if (helpfulFlag == 1) {
                            holder.parentHelpfulImg
                                    .setImageResource(R.mipmap.helpful);
                            holder.parentHelpfulTv.setTextColor(Color
                                    .parseColor("#ff0000"));
                            holder.parentHelpfulTv.setText("HELPFUL");
                        } else if (helpfulFlag == 2) {
                            holder.parentHelpfulImg
                                    .setImageResource(R.mipmap.unhelpful);
                            holder.parentHelpfulTv.setTextColor(Color
                                    .parseColor("#848484"));
                            holder.parentHelpfulTv.setText("UNHELPFUL");
                        } else {
                            holder.parentHelpfulImg
                                    .setImageResource(R.mipmap.help_initial);
                            holder.parentHelpfulTv.setTextColor(Color
                                    .parseColor("#0F98EC"));
                            holder.parentHelpfulTv.setText("HELPFUL?");
                        }
                    } else {
                        holder.parentHelpfulImg
                                .setImageResource(R.mipmap.help_initial);
                        holder.parentHelpfulTv.setTextColor(Color
                                .parseColor("#0F98EC"));
                    }

                } else {
                    holder.parentHelpfulImg
                            .setImageResource(R.mipmap.help_initial);
                    holder.parentHelpfulTv
                            .setTextColor(Color.parseColor("#0F98EC"));
                }

                holder.ratingBarInDetailView.setRating(Float.parseFloat(content
                        .getRating()) / 20);
                holder.mainReviewerDisplayName.setText(content.getAuthor()
                        .getDisplayName());

                holder.mainReviewDate.setText(LFUtils.getFormatedDate(
                        content.getCreatedAt(), LFSAppConstants.DETAIL));

                holder.mainReviewBody.setText(LFUtils.trimTrailingWhitespace(Html
                                .fromHtml(content.getBodyHtml())),
                        TextView.BufferType.SPANNABLE);

                holder.parentNotifRV.setOnClickListener(notificationHandler);
                holder.parentReplyImg.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent replyView = new Intent(context, ReplyReview.class);
                        replyView.putExtra("id", content.getId());
                        replyView.putExtra("isEdit", false);
                        activity.startActivityForResult(replyView, 1);
                    }
                });
                holder.parentReplyTv.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent replyView = new Intent(activity, ReplyReview.class);
                        replyView.putExtra("id", content.getId());
                        replyView.putExtra("isEdit", false);
                        activity.startActivityForResult(replyView, 1);
                    }
                });

                holder.parentsHelpfulImg.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        helpfulDialog(
                                knowHelpfulValue(
                                        application
                                                .getDataFromSharedPreferences(LFSAppConstants.ID),
                                        content.getVote()), content.getId());
                    }
                });
                holder.parentHelpfulTv.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        helpfulDialog(
                                knowHelpfulValue(
                                        application
                                                .getDataFromSharedPreferences(LFSAppConstants.ID),
                                        content.getVote()), content.getId());
                    }
                });
                holder.parentMoreOptions.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (content.getIsFeatured() != null) {
                            if (content.getIsFeatured()) {
                                moreDialog(content.getId(), true, true);
                            } else {
                                moreDialog(content.getId(), false, true);
                            }
                        } else {
                            moreDialog(content.getId(), false, true);
                        }
                    }
                });
                break;
            case CHILD:
                float density = context.getResources().getDisplayMetrics().density;
                int px = (int) (40 * density);
                int depthValue = 0;
                if (content.getParentPath() != null) {
                    depthValue = content.getParentPath().size();
                }

                switch (depthValue) {
                    case 1:
                        holder.childMain.setPadding(px * 0, 16, 16, 16);
                        break;
                    case 2:
                        holder.childMain.setPadding(px * 1, 16, 16, 16);
                        break;
                    case 3:
                        holder.childMain.setPadding(px * 2, 16, 16, 16);
                        break;
                    default:
                        holder.childMain.setPadding(px * 3, 16, 16, 16);
                        break;
                }

                if (content.getIsModerator() != null) {
                    if (content.getIsModerator().equals("true")) {
                        holder.isChildMod.setText("Moderator");
                        holder.isChildMod.setVisibility(View.VISIBLE);
                        holder.isChildMod.setTextColor(Color.parseColor("#0F98EC"));
                        holder.childReviewerDisplayName.setMaxWidth(330);
                    } else {
                        holder.isChildMod.setVisibility(View.GONE);
                    }
                } else {
                    holder.isChildMod.setVisibility(View.GONE);
                }

                if (content.getIsFeatured() != null) {
                    if (content.getIsFeatured()) {
                        holder.isChildMod.setText("Feature");

                        holder.isChildMod.setTextColor(Color.parseColor("#FEB33B"));
                        holder.isChildMod.setVisibility(View.VISIBLE);
                        holder.childReviewerDisplayName.setMaxWidth(380);

                    }
                }
                holder.childReviewerDisplayName.setText(content.getAuthor()
                        .getDisplayName());
                if (content.getAuthor().getAvatar().length() > 0) {
                    Picasso.with(context).load(content.getAuthor().getAvatar()).fit().transform(new RoundedTransformation(90, 0)).into(holder.childReviewerImage);
                } else {
                }
                holder.childReviewedDate.setText(LFUtils.getFormatedDate(
                        content.getCreatedAt(), LFSAppConstants.SHART));

                if (content.getVote() != null) {
                    if (content.getVote().size() > 0) {
                        holder.childReviewHelpful.setText(foundHelpfull(content
                                .getVote()));

                    } else
                        holder.childReviewHelpful.setVisibility(View.GONE);
                } else
                    holder.childReviewHelpful.setVisibility(View.GONE);
                if (content.getVote() != null) {// know helpful value and set color
                    if (content.getVote().size() > 0) {
                        helpfulFlag = knowHelpfulValue(
                                application
                                        .getDataFromSharedPreferences(LFSAppConstants.ID),
                                content.getVote());

                        if (helpfulFlag == 1)
                            holder.childHelpfulImg
                                    .setImageResource(R.mipmap.helpful);
                        else if (helpfulFlag == 2)
                            holder.childHelpfulImg
                                    .setImageResource(R.mipmap.unhelpful);
                        else
                            holder.childHelpfulImg
                                    .setImageResource(R.mipmap.help_initial);
                    } else {
                        holder.childHelpfulImg
                                .setImageResource(R.mipmap.help_initial);
                    }

                } else {
                    holder.childHelpfulImg
                            .setImageResource(R.mipmap.help_initial);
                }

                holder.childReviewBody.setText(LFUtils.trimTrailingWhitespace(Html
                                .fromHtml(content.getBodyHtml())),
                        TextView.BufferType.SPANNABLE);
                holder.childReplyImg.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent replyView = new Intent(activity, ReplyReview.class);
                        replyView.putExtra("id", content.getId());
                        replyView.putExtra("isEdit", false);
                        activity.startActivityForResult(replyView, 1);
                    }
                });

                holder.childHelpfulImg.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        helpfulDialog(
                                knowHelpfulValue(
                                        application
                                                .getDataFromSharedPreferences(LFSAppConstants.ID),
                                        content.getVote()), content.getId());
                    }
                });
                holder.childMoreOptions.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (content.getIsFeatured() != null) {
                            if (content.getIsFeatured()) {
                                moreDialog(content.getId(), true, false);
                            } else {
                                moreDialog(content.getId(), false, false);
                            }
                        } else {
                            moreDialog(content.getId(), false, false);
                        }
                    }
                });
                break;
            case DELETED:
                if (content.getId().equals(mainReviewId)) {
                    showToast("This review is no longer visible.");
                    activity.finish();
                }

                float densityDtl = context.getResources().getDisplayMetrics().density;
                int pxDtl = (int) (40 * densityDtl);
                depthValue = 0;
                if (content.getParentPath() != null) {
                    depthValue = content.getParentPath().size();
                }
                switch (depthValue) {
                    case 1:
                        holder.deletedCell.setPadding(pxDtl * 0, 16, 16, 16);
                        break;
                    case 2:
                        holder.deletedCell.setPadding(pxDtl * 1, 16, 16, 16);
                        break;
                    case 3:
                        holder.deletedCell.setPadding(pxDtl * 2, 16, 16, 16);
                        break;
                    default:
                        holder.deletedCell.setPadding(pxDtl * 3, 16, 16, 16);
                        break;
                }
                break;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return ContentArray.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        RatingBar ratingBarInDetailView;
        TextView mainReviewerDisplayName, mainReviewDate, mainReviewBody, isParentMod, parentReplyTv, parentHelpfulTv, notifMsgTV;
        ImageView parentMoreOptions, parentHelpfulImg, parentsHelpfulImg, parentReplyImg, mainReviewerImage;
        LinearLayout parentNotifRV;
        // child
        RelativeLayout childMain;
        ImageView childReviewerImage, childHelpfulImg, childReplyImg, childMoreOptions;
        TextView childReviewerDisplayName, childReviewedDate, childReviewHelpful, childReviewBody, isChildMod;
        // deleted
        LinearLayout deletedCell;

        public MyViewHolder(View item) {
            super(item);
            //parent views
            ratingBarInDetailView = (RatingBar) item.findViewById(R.id.ratingBarInDetailView);
            mainReviewerImage = (ImageView) item.findViewById(R.id.mainReviewerImage);
            mainReviewerDisplayName = (TextView) item.findViewById(R.id.mainReviewerDisplayName);
            mainReviewDate = (TextView) item.findViewById(R.id.mainReviewDate);
            mainReviewBody = (TextView) item.findViewById(R.id.mainReviewBody);
            isParentMod = (TextView) item.findViewById(R.id.isParentMod);
            parentHelpfulImg = (ImageView) item.findViewById(R.id.parentsHelpfulImg);
            parentHelpfulTv = (TextView) item.findViewById(R.id.parentsHelpfulTv);
            parentReplyTv = (TextView) item.findViewById(R.id.parentReplyTv);
            parentMoreOptions = (ImageView) item.findViewById(R.id.parentMoreOptions);
            notifMsgTV = (TextView) item.findViewById(R.id.notifMsgTV);
            parentsHelpfulImg = (ImageView) item.findViewById(R.id.parentsHelpfulImg);
            parentReplyImg = (ImageView) item.findViewById(R.id.parentReplyImg);
            parentNotifRV = (LinearLayout) item.findViewById(R.id.parentNotifRV);
            //child views
            childMain = (RelativeLayout) item.findViewById(R.id.childMain);
            childReviewerImage = (ImageView) item.findViewById(R.id.childReviewerImage);
            childReviewerDisplayName = (TextView) item.findViewById(R.id.childReviewerDisplayName);
            childReviewedDate = (TextView) item.findViewById(R.id.childReviewedDate);
            childReviewHelpful = (TextView) item.findViewById(R.id.childReviewHelpful);
            childReviewBody = (TextView) item.findViewById(R.id.childReviewBody);
            isChildMod = (TextView) item.findViewById(R.id.isChildMod);
            childHelpfulImg = (ImageView) item.findViewById(R.id.childHelpfulImg);
            childReplyImg = (ImageView) item.findViewById(R.id.childReplyImg);
            childMoreOptions = (ImageView) item.findViewById(R.id.childMoreOptions);
            //delete view
            deletedCell = (LinearLayout) item.findViewById(R.id.deletedCell);
        }
    }

    String foundHelpfull(List<Vote> v) {
        int count = 0;
        for (int i = 0; i < v.size(); i++) {

            if (v.get(i).getValue().equals("1"))// for Count
                count++;

        }
        return count + " of " + v.size() + " found helpful";
    }

    int knowHelpfulValue(String authorId, List<Vote> v) {

        int helpfulValue = 0;
        if (v != null)
            for (int i = 0; i < v.size(); i++) {
                if (v.get(i).getAuthor().equals(authorId)) { // helpful or not
                    // helpful

                    if (v.get(i).getValue().equals("1"))
                        helpfulValue = 1;
                    else
                        helpfulValue = 2;
                    break;
                }
            }

        return helpfulValue;
    }

    private void helpfulDialog(final int HFVal, final String id) {
        final Dialog dialog = new Dialog(activity,
                android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setTitle("");
        dialog.setContentView(R.layout.helpfull_dialog);
        dialog.setCancelable(true);

        LinearLayout emptyDialogSpace = (LinearLayout) dialog
                .findViewById(R.id.emptyDialogSpace);
        emptyDialogSpace.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        LinearLayout helpful = (LinearLayout) dialog.findViewById(R.id.helpful);
        helpful.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (HFVal == 1) {
                    RequestParams parameters = new RequestParams();
                    parameters.put("value", "0");
                    parameters.put(LFSConstants.LFSPostUserTokenKey,
                            LFSConfig.USER_TOKEN);
                    parameters.put("message_id", id);

                    WriteClient.postAction(LFSConfig.COLLECTION_ID, id,
                            LFSConfig.USER_TOKEN, LFSActions.VOTE, parameters,
                            new helpfulCallback());
                    dialog.dismiss();
                } else {
                    RequestParams parameters = new RequestParams();
                    parameters.put("value", "1");
                    parameters.put(LFSConstants.LFSPostUserTokenKey,
                            LFSConfig.USER_TOKEN);
                    parameters.put("message_id", id);

                    WriteClient.postAction(LFSConfig.COLLECTION_ID, id,
                            LFSConfig.USER_TOKEN, LFSActions.VOTE, parameters,
                            new helpfulCallback());
                    dialog.dismiss();

                }

            }
        });

        LinearLayout notHelpful = (LinearLayout) dialog
                .findViewById(R.id.notHelpful);
        notHelpful.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showProgress();
                if (HFVal == 2) {
                    RequestParams parameters = new RequestParams();
                    parameters.put("value", "0");
                    parameters.put(LFSConstants.LFSPostUserTokenKey,
                            LFSConfig.USER_TOKEN);
                    parameters.put("message_id", id);

                    WriteClient.postAction(LFSConfig.COLLECTION_ID, id,
                            LFSConfig.USER_TOKEN, LFSActions.VOTE, parameters,
                            new helpfulCallback());
                    dialog.dismiss();
                } else {
                    RequestParams parameters = new RequestParams();
                    parameters.put("value", "2");
                    parameters.put(LFSConstants.LFSPostUserTokenKey,
                            LFSConfig.USER_TOKEN);
                    parameters.put("message_id", id);

                    WriteClient.postAction(LFSConfig.COLLECTION_ID, id,
                            LFSConfig.USER_TOKEN, LFSActions.VOTE, parameters,
                            new helpfulCallback());
                    dialog.dismiss();

                }

            }
        });
        dialog.show();

    }

    private void moreDialog(final String id, final Boolean isFeatured,
                            final Boolean isChild) {
        Content mBean = ContentParser.ContentMap.get(id);

        final Dialog dialog = new Dialog(activity,
                android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.more);
        dialog.setCancelable(true);
        if (isFeatured) {
            ((TextView) dialog.findViewById(R.id.alreadyFeatured))
                    .setText("Unfeature");
        } else {
            ((TextView) dialog.findViewById(R.id.alreadyFeatured))
                    .setText("Feature");
        }
        LinearLayout emptyDialogSpace = (LinearLayout) dialog
                .findViewById(R.id.emptyDialogSpace);
        emptyDialogSpace.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

        LinearLayout edit = (LinearLayout) dialog.findViewById(R.id.edit);
        LinearLayout feature = (LinearLayout) dialog.findViewById(R.id.feature);
        LinearLayout flag = (LinearLayout) dialog.findViewById(R.id.flag);
        LinearLayout bozo = (LinearLayout) dialog.findViewById(R.id.bozo);
        LinearLayout banUser = (LinearLayout) dialog.findViewById(R.id.banUser);
        LinearLayout delete = (LinearLayout) dialog.findViewById(R.id.delete);

        View moreLine = dialog.findViewById(R.id.moreLine);
        if ("yes".equals(application
                .getDataFromSharedPreferences(LFSAppConstants.ISMOD))
                && mBean.getIsModerator().equals("true")) {
            edit.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
            feature.setVisibility(View.VISIBLE);
            moreLine.setVisibility(View.VISIBLE);

        } else if ("yes".equals(application
                .getDataFromSharedPreferences(LFSAppConstants.ISMOD))) {
            edit.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
            moreLine.setVisibility(View.VISIBLE);
            feature.setVisibility(View.VISIBLE);
            flag.setVisibility(View.VISIBLE);
            bozo.setVisibility(View.VISIBLE);
            banUser.setVisibility(View.VISIBLE);
            moreLine.setVisibility(View.VISIBLE);

        } else if (mBean.getAuthorId().equals(
                application.getDataFromSharedPreferences(LFSAppConstants.ID))) {
            edit.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
        } else {
            flag.setVisibility(View.VISIBLE);
        }
        if (mBean.getIsFeatured()) {
            flag.setVisibility(View.GONE);
        }

        edit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isChild) {
                    Intent editView = new Intent(context, EditReview.class);
                    editView.putExtra("id", id);
                    editView.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(editView);
                } else {
                    Intent replyView = new Intent(context, ReplyReview.class);
                    replyView.putExtra("id", id);
                    replyView.putExtra("isEdit", true);
                    replyView.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(replyView);
                }

                dialog.dismiss();

            }
        });

        feature.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showProgress();
                if (isFeatured) {
                    try {
                        WriteClient.featureMessage("unfeature", id,
                                LFSConfig.COLLECTION_ID, LFSConfig.USER_TOKEN,
                                null, new helpfulCallback());// same as helpful
                        // call back
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                } else {

                    try {
                        WriteClient.featureMessage("feature", id,
                                LFSConfig.COLLECTION_ID, LFSConfig.USER_TOKEN,
                                null, new helpfulCallback());// same as helpful
                        // call back
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }

                dialog.dismiss();

            }
        });

        flag.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                flagDialog(id);
                dialog.dismiss();
            }
        });

        bozo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showProgress();
                RequestParams parameters = new RequestParams();
                parameters.put(LFSConstants.LFSPostUserTokenKey,
                        LFSConfig.USER_TOKEN);
                parameters.put("message_id", id);

                WriteClient.postAction(LFSConfig.COLLECTION_ID, id,
                        LFSConfig.USER_TOKEN, LFSActions.BOZO, parameters,
                        new actionCallback());
                dialog.dismiss();
            }
        });

        banUser.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showProgress();
                RequestParams parameters = new RequestParams();
                parameters.put("network", LFSConfig.NETWORK_ID);
                parameters.put(LFSConstants.LFSPostUserTokenKey,
                        LFSConfig.USER_TOKEN);
                parameters.put("retroactive", "0");
                WriteClient.flagAuthor(ContentParser.ContentMap.get(id)
                                .getAuthorId(), LFSConfig.USER_TOKEN, parameters,
                        new actionCallback());

                dialog.dismiss();

            }
        });

        delete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showProgress();
                RequestParams parameters = new RequestParams();
                parameters.put(LFSConstants.LFSPostUserTokenKey,
                        LFSConfig.USER_TOKEN);
                parameters.put("message_id", id);

                WriteClient.postAction(LFSConfig.COLLECTION_ID, id,
                        LFSConfig.USER_TOKEN, LFSActions.DELETE, parameters,
                        new actionCallback());

                dialog.dismiss();

            }
        });
        if (edit.getVisibility() == View.GONE
                && feature.getVisibility() == View.GONE
                && delete.getVisibility() == View.GONE
                && banUser.getVisibility() == View.GONE
                && bozo.getVisibility() == View.GONE
                && flag.getVisibility() == View.GONE) {

        } else {
            dialog.show();
        }
    }

    private void flagDialog(final String id) {
        final Dialog dialog = new Dialog(activity,
                android.R.style.Theme_Translucent_NoTitleBar);

        dialog.setTitle("");
        dialog.setContentView(R.layout.flag);
        dialog.setCancelable(true);
        LinearLayout emptyDialogSpace = (LinearLayout) dialog
                .findViewById(R.id.emptyDialogSpace);
        emptyDialogSpace.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        TextView spam = (TextView) dialog.findViewById(R.id.spam);
        spam.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                RequestParams parameters = new RequestParams();
                parameters.put(LFSConstants.LFSPostUserTokenKey,
                        LFSConfig.USER_TOKEN);
                parameters.put("message_id", id);

                WriteClient.flagContent(LFSConfig.COLLECTION_ID, id,
                        LFSConfig.USER_TOKEN, LFSFlag.SPAM, parameters,
                        new flagCallback());
                dialog.dismiss();

            }
        });

        TextView offensive = (TextView) dialog.findViewById(R.id.offensive);
        offensive.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                RequestParams parameters = new RequestParams();
                parameters.put(LFSConstants.LFSPostUserTokenKey,
                        LFSConfig.USER_TOKEN);
                parameters.put("message_id", id);

                WriteClient.flagContent(LFSConfig.COLLECTION_ID, id,
                        LFSConfig.USER_TOKEN, LFSFlag.OFFENSIVE, parameters,
                        new flagCallback());
                dialog.dismiss();

            }
        });

        TextView offtopic = (TextView) dialog.findViewById(R.id.offtopic);
        offtopic.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                RequestParams parameters = new RequestParams();
                parameters.put(LFSConstants.LFSPostUserTokenKey,
                        LFSConfig.USER_TOKEN);
                parameters.put("message_id", id);

                WriteClient.flagContent(LFSConfig.COLLECTION_ID, id,
                        LFSConfig.USER_TOKEN, LFSFlag.OFF_TOPIC, parameters,
                        new flagCallback());
                dialog.dismiss();

            }
        });

        TextView disagree = (TextView) dialog.findViewById(R.id.disagree);
        disagree.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                RequestParams parameters = new RequestParams();
                parameters.put(LFSConstants.LFSPostUserTokenKey,
                        LFSConfig.USER_TOKEN);
                parameters.put("message_id", id);

                WriteClient.flagContent(LFSConfig.COLLECTION_ID, id,
                        LFSConfig.USER_TOKEN, LFSFlag.DISAGREE, parameters,
                        new flagCallback());
                dialog.dismiss();

            }
        });

        dialog.show();
    }

    private class actionCallback extends JsonHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            Log.d("action ClientCall", "success" + response);
            if (!response.isNull("data")) {
                try {
                    JSONObject data = response.getJSONObject("data");
                    if (!data.isNull("messageId")) {
                        if (data.getString("messageId").equals(mainReviewId)) {
                            if (dialog.isShowing())
                                dismissProgress();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (dialog.isShowing())
                dismissProgress();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            dismissProgress();
            Log.d("action ClientCall", throwable + "");
            showToast("Something went wrong.");
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            dismissProgress();
            Log.d("action ClientCall", throwable + "");
            showToast("Something went wrong.");
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
            dismissProgress();
            Log.d("action ClientCall", throwable + "");
            showToast("Something went wrong.");
        }

    }

    private class helpfulCallback extends JsonHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            super.onSuccess(statusCode, headers, response);
            dismissProgress();
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            super.onSuccess(statusCode, headers, responseString);
            dismissProgress();
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            dismissProgress();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            dismissProgress();
            showToast("Something went wrong.");
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            dismissProgress();
            showToast("Something went wrong.");
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
            dismissProgress();
            showToast("Something went wrong.");

        }

    }

    private class flagCallback extends JsonHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            showToast("Content flagged successfully");
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            super.onSuccess(statusCode, headers, response);
            showToast("Content flagged successfully");

        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            super.onSuccess(statusCode, headers, responseString);
            showToast("Content flagged successfully");

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            dismissProgress();
            showToast("Something went wrong.");
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            dismissProgress();
            showToast("Something went wrong.");
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
            dismissProgress();
            showToast("Something went wrong.");
        }

    }

    protected void showProgress() {
        dialog = new ProgressDialog(activity);
        dialog.setMessage("Please wait." + "\n"
                + "Your request is being processed..");
        dialog.setCancelable(false);
        dialog.show();
    }

    protected void dismissProgress() {
        try {
            dialog.dismiss();
        } catch (Exception e) {
        }
    }

    public void showToast(String toastText) {
        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
    }
}
