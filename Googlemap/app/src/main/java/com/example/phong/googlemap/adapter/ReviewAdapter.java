package com.example.phong.googlemap.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.phong.googlemap.R;
import com.example.phong.googlemap.model.Review;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Shiro on 03/12/2016.
 */

public class ReviewAdapter extends ArrayAdapter<Review> {

    private Activity context;
    private int IDLayout;
    private ArrayList<Review> reviews = null;
    public ReviewAdapter(Activity context, int resource, ArrayList<Review> reviews) {
        super(context, resource, reviews);
        this.context = context;
        this.IDLayout = resource;
        this.reviews = reviews;
    }

    public static class ViewHolder{
        TextView tvName,tvTimestamp,tvComment;
        ImageView imgRating1,imgRating2,imgRating3,imgRating4,imgRating5;
        CircleImageView imageProfile;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(IDLayout,parent,false);

            holder.imageProfile = (CircleImageView) convertView.findViewById(R.id.profile_image);
            holder.tvName = (TextView) convertView.findViewById(R.id.txt_author_review_item);
            holder.tvTimestamp = (TextView) convertView.findViewById(R.id.txt_time_ago_review_item);
            holder.tvComment = (TextView) convertView.findViewById(R.id.txt_content_review_item);
            holder.imgRating1 = (ImageView) convertView.findViewById(R.id.imv_rating_review_item1);
            holder.imgRating2 = (ImageView) convertView.findViewById(R.id.imv_rating_review_item2);
            holder.imgRating3 = (ImageView) convertView.findViewById(R.id.imv_rating_review_item3);
            holder.imgRating4 = (ImageView) convertView.findViewById(R.id.imv_rating_review_item4);
            holder.imgRating5 = (ImageView) convertView.findViewById(R.id.imv_rating_review_item5);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(reviews.size() > 0 && position >= 0){
            Review review = reviews.get(position);
            //set image profile
            Glide.with(context).load(review.getPhoto()).into(holder.imageProfile);
            //set author
            holder.tvName.setText(review.getName());
            //set content
            holder.tvComment.setText(review.getContent());
            //set time  ago
            holder.tvTimestamp.setText(review.getTimeAgo());
            //set rating imv
            //update rating
            double rating = review.getRating();
            if (rating == -1 || rating == 0) {
                holder.imgRating1.setImageResource(R.drawable.ic_star_empty);
                holder.imgRating2.setImageResource(R.drawable.ic_star_empty);
                holder.imgRating3.setImageResource(R.drawable.ic_star_empty);
                holder.imgRating4.setImageResource(R.drawable.ic_star_empty);
                holder.imgRating5.setImageResource(R.drawable.ic_star_empty);
            } else {
                if (rating == 1) {
                    holder.imgRating1.setImageResource(R.drawable.ic_star);
                    holder.imgRating2.setImageResource(R.drawable.ic_star_empty);
                    holder.imgRating3.setImageResource(R.drawable.ic_star_empty);
                    holder.imgRating4.setImageResource(R.drawable.ic_star_empty);
                    holder.imgRating5.setImageResource(R.drawable.ic_star_empty);
                } else {
                    if (rating > 1 && rating < 2) {
                        holder.imgRating1.setImageResource(R.drawable.ic_star);
                        holder.imgRating2.setImageResource(R.drawable.ic_star_half);
                        holder.imgRating3.setImageResource(R.drawable.ic_star_empty);
                        holder.imgRating4.setImageResource(R.drawable.ic_star_empty);
                        holder.imgRating5.setImageResource(R.drawable.ic_star_empty);
                    } else {
                        if (rating == 2) {
                            holder.imgRating1.setImageResource(R.drawable.ic_star);
                            holder.imgRating2.setImageResource(R.drawable.ic_star);
                            holder.imgRating3.setImageResource(R.drawable.ic_star_empty);
                            holder.imgRating4.setImageResource(R.drawable.ic_star_empty);
                            holder.imgRating5.setImageResource(R.drawable.ic_star_empty);
                        } else {
                            if (rating > 2 && rating < 3) {
                                holder.imgRating1.setImageResource(R.drawable.ic_star);
                                holder.imgRating2.setImageResource(R.drawable.ic_star);
                                holder.imgRating3.setImageResource(R.drawable.ic_star_half);
                                holder.imgRating4.setImageResource(R.drawable.ic_star_empty);
                                holder.imgRating5.setImageResource(R.drawable.ic_star_empty);
                            } else {
                                if (rating == 3) {
                                    holder.imgRating1.setImageResource(R.drawable.ic_star);
                                    holder.imgRating2.setImageResource(R.drawable.ic_star);
                                    holder.imgRating3.setImageResource(R.drawable.ic_star);
                                    holder.imgRating4.setImageResource(R.drawable.ic_star_empty);
                                    holder.imgRating5.setImageResource(R.drawable.ic_star_empty);
                                } else {
                                    if (rating > 3 && rating < 4) {
                                        holder.imgRating1.setImageResource(R.drawable.ic_star);
                                        holder.imgRating2.setImageResource(R.drawable.ic_star);
                                        holder.imgRating3.setImageResource(R.drawable.ic_star);
                                        holder.imgRating4.setImageResource(R.drawable.ic_star_half);
                                        holder.imgRating5.setImageResource(R.drawable.ic_star_empty);
                                    } else {
                                        if (rating == 4) {
                                            holder.imgRating1.setImageResource(R.drawable.ic_star);
                                            holder.imgRating2.setImageResource(R.drawable.ic_star);
                                            holder.imgRating3.setImageResource(R.drawable.ic_star);
                                            holder.imgRating4.setImageResource(R.drawable.ic_star);
                                            holder.imgRating5.setImageResource(R.drawable.ic_star_empty);
                                        } else {
                                            if (rating > 4 && rating < 5) {
                                                holder.imgRating1.setImageResource(R.drawable.ic_star);
                                                holder.imgRating2.setImageResource(R.drawable.ic_star);
                                                holder.imgRating3.setImageResource(R.drawable.ic_star);
                                                holder.imgRating4.setImageResource(R.drawable.ic_star);
                                                holder.imgRating5.setImageResource(R.drawable.ic_star_half);
                                            } else {
                                                if (rating == 5) {
                                                    holder.imgRating1.setImageResource(R.drawable.ic_star);
                                                    holder.imgRating2.setImageResource(R.drawable.ic_star);
                                                    holder.imgRating3.setImageResource(R.drawable.ic_star);
                                                    holder.imgRating4.setImageResource(R.drawable.ic_star);
                                                    holder.imgRating5.setImageResource(R.drawable.ic_star);
                                                } else {
                                                    if (rating > 0 && rating < 1) {
                                                        holder.imgRating1.setImageResource(R.drawable.ic_star_half);
                                                        holder.imgRating2.setImageResource(R.drawable.ic_star_empty);
                                                        holder.imgRating3.setImageResource(R.drawable.ic_star_empty);
                                                        holder.imgRating4.setImageResource(R.drawable.ic_star_empty);
                                                        holder.imgRating5.setImageResource(R.drawable.ic_star_empty);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return convertView;
    }
}
