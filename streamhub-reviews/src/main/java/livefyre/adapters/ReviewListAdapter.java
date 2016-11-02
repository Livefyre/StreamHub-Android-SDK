package livefyre.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import livefyre.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import livefyre.LFSAppConstants;
import livefyre.LFUtils;
import livefyre.RoundedTransformation;
import livefyre.models.Attachments;
import livefyre.models.Content;
import livefyre.models.Vote;
import livefyre.parsers.ContentParser;

@SuppressLint("SimpleDateFormat")
public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.MyViewHolder> {

    private List<Content> ContentMap = null;
    private LayoutInflater inflater;
    Context context;

    public ReviewListAdapter(Context context, List<Content> ContentMap) {
        this.ContentMap = ContentMap;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }
    public void updateContentResult(List<Content> ContentMap) {
        this.ContentMap = ContentMap;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return ContentMap.get(position).getContentType().getValue();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = null;
        View view = null;
        view = inflater.inflate(R.layout.layout_review_list_row, parent, false);
        holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Content content = (Content) ContentMap
                .get(position);
        holder.reviewerid.setText(content.getAuthor().getDisplayName());
        holder.reviewCount.setVisibility(View.VISIBLE);

        if (content.getIsModerator() != null) {
            if (content.getIsModerator().equals("true")) {
                holder.isMod.setText("Moderator");
                holder.isMod.setVisibility(View.VISIBLE);
                holder.isMod.setTextColor(Color.parseColor("#0F98EC"));
                holder.reviewerid.setMaxWidth(330);
            } else {
                holder.isMod.setVisibility(View.GONE);
            }
        } else {
            holder.isMod.setVisibility(View.GONE);
        }

        if (content.getIsFeatured() != null) {
            if (content.getIsFeatured()) {
                holder.isMod.setText("Feature");
                holder.isMod.setTextColor(Color.parseColor("#FEB33B"));
                holder.isMod.setVisibility(View.VISIBLE);
                holder.reviewerid.setMaxWidth(330);
            }
        }
        holder.reviewedDate.setText(LFUtils.getFormatedDate(content.getCreatedAt(), LFSAppConstants.SHART));
        holder.reviewTitle.setText(content.getTitle());
        holder.reviewBody.setText(LFUtils.trimTrailingWhitespace(Html.fromHtml(content.getBodyHtml())), TextView.BufferType.SPANNABLE);
        if(content.getRating()!=null)
            holder.reviewRatingBar.setRating(Float.parseFloat(content.getRating()) / 20);

        if (content.getAuthor().getAvatar().length() > 0) {
            Picasso.with(context).load(content.getAuthor().getAvatar()).fit().transform(new RoundedTransformation(90, 0)).into(holder.reviewerImage);
        } else {
            Picasso.with(context).load(R.mipmap.profile_default).fit().transform(new RoundedTransformation(90, 0)).into(holder.reviewerImage);
        }
        if (content.getAttachments() != null) {
            if (content.getAttachments().size() > 0) {

                Attachments mAttachments = content.getAttachments().get(0);

                if (mAttachments.getType().equals("video")) {
                    if (mAttachments.getThumbnail_url() != null) {
                        if (mAttachments.getThumbnail_url().length() > 0) {
                            holder.reviewImage.setVisibility(View.VISIBLE);
                            Picasso.with(context).load(mAttachments.getThumbnail_url()).fit().into(holder.reviewImage);
                        } else {
                            holder.reviewImage.setVisibility(View.GONE);
                        }
                    } else {
                        holder.reviewImage.setVisibility(View.GONE);
                    }
                } else {
                    if (mAttachments.getUrl() != null) {
                        if (mAttachments.getUrl().length() > 0) {
                            holder.reviewImage.setVisibility(View.VISIBLE);
                            Picasso.with(context).load(mAttachments.getUrl()).fit().into(holder.reviewImage);
                        } else {
                            holder.reviewImage.setVisibility(View.GONE);
                        }
                    } else {
                        holder.reviewImage.setVisibility(View.GONE);
                    }
                }
            } else {
                holder.reviewImage.setVisibility(View.GONE);
            }
        } else {
            holder.reviewImage.setVisibility(View.GONE);
        }

        if (content.getVote() != null) {
            if (content.getVote().size() > 0)
                holder.helpful.setText(foundHelpfull(content.getVote()));
            else
                holder.helpful.setVisibility(View.GONE);
        } else
            holder.helpful.setVisibility(View.GONE);
        int visibilityCount = (ContentParser.getChildForContent(content.getId())).size();
        if (visibilityCount > 0) {
            holder.replies.setVisibility(View.VISIBLE);
            if (visibilityCount == 1)
                holder.replies.setText(visibilityCount
                        + " Reply");
            else
                holder.replies.setText(visibilityCount
                        + " Replies");
        } else
            holder.replies.setVisibility(View.GONE);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return ContentMap.size();
    }

     static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView reviewerImage, reviewImage;
        RatingBar reviewRatingBar;
        TextView reviewerid, isMod, reviewedDate, reviewTitle, reviewBody, helpful, replies;
        LinearLayout reviewCount;

        public MyViewHolder(View item) {
            super(item);
            reviewCount = (LinearLayout) item.findViewById(R.id.reviewCount);
            reviewerid = (TextView) item.findViewById(R.id.reviewerid);
            isMod = (TextView) item.findViewById(R.id.isMod);
            reviewedDate = (TextView) item.findViewById(R.id.reviewedDate);
            reviewTitle = (TextView) item.findViewById(R.id.reviewTitle);
            reviewBody = (TextView) item.findViewById(R.id.reviewBody);
            reviewerImage = (ImageView) item.findViewById(R.id.reviewerImage);
            reviewImage = (ImageView) item.findViewById(R.id.reviewImage);
            reviewRatingBar = (RatingBar) item.findViewById(R.id.reviewRatingBar);
            helpful = (TextView) item.findViewById(R.id.helpful);
            replies = (TextView) item.findViewById(R.id.replies);
        }
    }

    String foundHelpfull(List<Vote> v) {
        int count = 0;
        for (int i = 0; i < v.size(); i++) {
            if (v.get(i).getValue().equals("1"))
                count++;
        }
        return count + " of " + v.size() + " found helpful";
    }
}
