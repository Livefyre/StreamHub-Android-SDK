package com.livefyre.comments.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.livefyre.comments.util.Constant;
import com.livefyre.comments.util.Util;
import com.livefyre.comments.R;
import com.livefyre.comments.util.RoundedTransformation;
import com.livefyre.comments.activities.CommentActivity;
import com.livefyre.comments.models.Attachments;
import com.livefyre.comments.models.Content;
import com.livefyre.comments.models.Vote;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by kvanadev5 on 02/02/15.
 */
public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.MyViewHolder> {
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<Content> contentArray = null;

    private static final int VIEW_COUNT = 3;
    private static final int DELETED = -1;
    private static final int PARENT = 0;
    private static final int CHILD = 1;

    public CommentsAdapter(Context context, List<Content> contentArray) {
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        this.contentArray = contentArray;
    }

    @Override
    public int getItemViewType(int position) {
        return contentArray.get(position).getContentType().getValue();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = null;
        View view = null;
        switch (viewType) {
            case PARENT:
            case CHILD:
                view = mLayoutInflater.inflate(R.layout.comments_list_item, parent, false);
                holder = new MyViewHolder(view);

                break;
            case DELETED:
                view = mLayoutInflater.inflate(R.layout.deleted_item, parent, false);
                holder = new MyViewHolder(view);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final Content comment = contentArray.get(position);

        int viewType = comment.getContentType().getValue();

        switch (viewType) {
            case PARENT:
            case CHILD:
                try {
                    float density = mContext.getResources().getDisplayMetrics().density;

                    int px = (int) (40 * density);
                    int depthValue = 0;
                    if (comment.getParentPath() != null) {
                        depthValue = comment.getParentPath().size();
                    }
                    switch (depthValue) {
                        case 0:
                            holder.commentsListItemLL.setPadding(16, 0, 16, 16);
                            break;
                        case 1:
                            holder.commentsListItemLL.setPadding(px * 1, 0, 16, 16);
                            break;
                        case 2:
                            holder.commentsListItemLL.setPadding(px * 2, 0, 16, 16);
                            break;
                        case 3:
                            holder.commentsListItemLL.setPadding(px * 3, 0, 16, 16);
                            break;
                        default:
                            holder.commentsListItemLL.setPadding(px * 3, 0, 16, 16);
                            break;
                    }

                    holder.bottomLine.setVisibility(View.VISIBLE);

                    //Author Name
                    holder.authorNameTv.setText(comment.getAuthor().getDisplayName());
                    //Posted Date
                    holder.postedDateOrTime.setText(Util.getFormatedDate(
                            comment.getCreatedAt(), Constant.SHART));
                    //Comment Body
                    holder.commentBody.setText(Util.trimTrailingWhitespace(Html
                                    .fromHtml(comment.getBodyHtml())),
                            TextView.BufferType.SPANNABLE);
                    //Moderator
                    if (comment.getIsModerator().equals("true")) {
                        holder.moderatorTv.setVisibility(View.VISIBLE);
                    } else {
                        holder.moderatorTv.setVisibility(View.GONE);
                    }
                    //Featured
                    if (comment.getIsFeatured()) {
                        holder.moderatorTv.setVisibility(View.GONE);
                        holder.featureLL.setVisibility(View.VISIBLE);
                    } else {
                        holder.featureLL.setVisibility(View.GONE);
                    }

                    //Liked
                    if (comment.getVote() != null) {
                        if (comment.getVote().size() > 0) {
                            holder.likesTv.setVisibility(View.VISIBLE);
                            holder.likesTv.setText(likedCount(comment.getVote()));
                        } else
                            holder.likesTv.setVisibility(View.GONE);
                    } else
                        holder.likesTv.setVisibility(View.GONE);

                    if (comment.getAuthor().getAvatar().length() > 0) {
                        Picasso.with(mContext).load(comment.getAuthor().getAvatar()).fit().transform(new RoundedTransformation(90, 0)).into(holder.avatarIv);
                    } else {
                        Picasso.with(mContext).load(R.drawable.profile_default).fit().transform(new RoundedTransformation(90, 0)).into(holder.avatarIv);
                    }
                    if (comment.getAttachments() != null) {
                        if (comment.getAttachments().size() > 0) {
                            Attachments mAttachments = comment.getAttachments().get(0);
                            if (mAttachments.getType().equals("video")) {
                                if (mAttachments.getThumbnail_url() != null) {
                                    if (mAttachments.getThumbnail_url().length() > 0) {
                                        holder.imageAttachedToCommentIv.setVisibility(View.VISIBLE);
                                        Picasso.with(mContext).load(mAttachments.getThumbnail_url()).fit().into(holder.imageAttachedToCommentIv);
                                    } else {
                                        holder.imageAttachedToCommentIv.setVisibility(View.GONE);
                                    }

                                } else {
                                    holder.imageAttachedToCommentIv.setVisibility(View.GONE);
                                }
                            } else {
                                if (mAttachments.getUrl() != null) {
                                    if (mAttachments.getUrl().length() > 0) {
                                        holder.imageAttachedToCommentIv.setVisibility(View.VISIBLE);
                                        Picasso.with(mContext).load(mAttachments.getUrl()).fit().into(holder.imageAttachedToCommentIv);
                                    } else {
                                        holder.imageAttachedToCommentIv.setVisibility(View.GONE);
                                    }

                                } else {
                                    holder.imageAttachedToCommentIv.setVisibility(View.GONE);
                                }
                            }

                        } else {
                            holder.imageAttachedToCommentIv.setVisibility(View.GONE);
                        }
                    } else {
                        holder.imageAttachedToCommentIv.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case DELETED:
                float density = mContext.getResources().getDisplayMetrics().density;

                int px = (int) (40 * density);
                int depthValue = 0;
                if (comment.getParentPath() != null) {
                    depthValue = comment.getParentPath().size();
                }

                switch (depthValue) {
                    case 0:
                        holder.deleted_item.setPadding(16, 0, 16, 16);
                        break;
                    case 1:
                        holder.deleted_item.setPadding(px * 1, 0, 16, 16);
                        break;
                    case 2:
                        holder.deleted_item.setPadding(px * 2, 0, 16, 16);
                        break;
                    case 3:
                        holder.deleted_item.setPadding(px * 3, 0, 16, 16);
                        break;
                    default:
                        holder.deleted_item.setPadding(px * 3, 0, 16, 16);
                        break;

                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return contentArray.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        View bottomLine;

        TextView authorNameTv, postedDateOrTime, commentBody, moderatorTv, likesTv;

        LinearLayout featureLL, commentsListItemLL, deleted_item;

        ImageView avatarIv, imageAttachedToCommentIv;

        public MyViewHolder(View item) {
            super(item);
            item.setOnClickListener(this);
            bottomLine = item.findViewById(R.id.bottomLine);
            commentsListItemLL = (LinearLayout) item.findViewById(R.id.commentsListItemLL);
            deleted_item = (LinearLayout) item.findViewById(R.id.deletedCell);
            authorNameTv = (TextView) item.findViewById(R.id.authorNameTv);
            postedDateOrTime = (TextView) item.findViewById(R.id.postedDateOrTime);
            commentBody = (TextView) item.findViewById(R.id.commentBody);
            likesTv = (TextView) item.findViewById(R.id.likesFullTv);
            moderatorTv = (TextView) item.findViewById(R.id.moderatorTv);
            featureLL = (LinearLayout) item.findViewById(R.id.featureLL);
            avatarIv = (ImageView) item.findViewById(R.id.avatarIv);
            imageAttachedToCommentIv = (ImageView) item.findViewById(R.id.imageAttachedToCommentIv);
        }

        @Override
        public void onClick(View v) {
            switch (contentArray.get(getLayoutPosition()).getContentType().getValue()) {
                case PARENT:
                case CHILD:
                    Intent detailViewIntent = new Intent(mContext, CommentActivity.class);
                    detailViewIntent.putExtra(Constant.ID, contentArray.get(getLayoutPosition()).getId());
                    mContext.startActivity(detailViewIntent);
                    break;
                case DELETED:
                    break;
            }
        }


    }

    private String likedCount(List<Vote> v) {
        int count = 0;
        for (int i = 0; i < v.size(); i++) {
            if (v.get(i).getValue().equals("1"))
                count++;
        }
        return "Likes " + count;
    }
}
