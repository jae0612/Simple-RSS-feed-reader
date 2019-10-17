package com.rssfeeder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.rometools.rome.feed.atom.Feed;
import com.rssfeeder.R;
import com.rssfeeder.VO.FeedVO;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.DataFormatException;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;


public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    private List<FeedVO> articles;
    private Context mContext;
    private WebView articleView; // to show an article inside a dialog

    // new object created whenever the articles list updated
    //callback GetRssTask -> MainViewModel -> ArticleAdapter
    public ArticleAdapter(List<FeedVO> list, Context context) {
        this.articles = list;
        this.mContext = context;
    }

    // to clear the article list when refreshed
    public List<FeedVO> getArticleList() {
        return articles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row, viewGroup, false);
        return new ViewHolder(v);
    }

    // manages a view for articles
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {

        final FeedVO currentArticle = articles.get(position);

        // publish date
        String pubDateString = currentArticle.getPubDate().toString();
        //Edit to change date time format
        /*
        try {
            String sourceDateString = currentArticle.getPubDate();
            SimpleDateFormat sourceSdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
            Date date = sourceSdf.parse(sourceDateString);
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
            pubDateString = sdf.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
            pubDateString = currentArticle.getPubDate();
        }
        */

        // show title
        viewHolder.title.setText(currentArticle.getTitle());


        // show thumbnail
        Picasso.get()
                .load(currentArticle.getImageUrl())
                //.placeholder(R.drawable.placeholder)
                .into(viewHolder.image);


        // show publish date
        viewHolder.pubDate.setText(pubDateString);

        //Edit to add categories
        StringBuilder categories = new StringBuilder();
        if(currentArticle.getCategories()!= null)
            for (int i = 0; i < currentArticle.getCategories().size(); i++) {
                if (i == currentArticle.getCategories().size() - 1) {
                    categories.append(currentArticle.getCategories().get(i));
                } else {
                    categories.append(currentArticle.getCategories().get(i)).append(", ");
                }
            }
        //viewHolder.category.setText("categories");


        viewHolder.category.setText(categories);

        // popup the article when clicked
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public void onClick(View view) {

                //show article content inside a dialog
                articleView = new WebView(mContext);

                articleView.getSettings().setLoadWithOverviewMode(true);

                String title = articles.get(viewHolder.getAdapterPosition()).getTitle();
                String content = articles.get(viewHolder.getAdapterPosition()).getDescription();
                String image = articles.get(viewHolder.getAdapterPosition()).getImageUrl();

                articleView.getSettings().setJavaScriptEnabled(true);
                articleView.setHorizontalScrollBarEnabled(false);
                articleView.setWebChromeClient(new WebChromeClient());
                articleView.loadDataWithBaseURL(null, "<style>img{display: block; height: auto; max-width: 100%; margin: 0 auto; padding: 0} "
                        + "</style>\n" + "<img src ='"+ image +"' /><br>" + "<style>iframe{ height: auto; width: auto;}" + "</style>\n <h4>" + content + "</h4>", null, "utf-8", null);

                androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(mContext).create();
                alertDialog.setTitle(title);
                alertDialog.setView(articleView);
                alertDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

                ((TextView) alertDialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
            }
        });


        // Check favorite
        viewHolder.favorite.setChecked(currentArticle.isFavorite());
        viewHolder.favorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // update your model (or other business logic) based on isChecked
                articleView = new WebView(mContext);
                articleView.getSettings().setLoadWithOverviewMode(true);
                FeedVO fvo = articles.get(viewHolder.getAdapterPosition());
                fvo.setFavorite(isChecked);
            }
        });

    }

    @Override
    public int getItemCount() {
        return articles == null ? 0 : articles.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView pubDate;
        ImageView image;
        TextView category;
        CheckBox favorite;


        public ViewHolder(View itemView) {

            super(itemView);
            title = itemView.findViewById(R.id.title);
            pubDate = itemView.findViewById(R.id.pubDate);
            image = itemView.findViewById(R.id.image);
            favorite = itemView.findViewById(R.id.favorite);
            //category = itemView.findViewById(R.id.categories);
        }
    }
}