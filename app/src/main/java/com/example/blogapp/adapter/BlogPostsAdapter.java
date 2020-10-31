package com.example.blogapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blogapp.R;
import com.example.blogapp.Utils;
import com.example.blogapp.model.Post;
import com.example.blogapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.annotation.Nullable;

public class BlogPostsAdapter extends RecyclerView.Adapter<BlogPostsAdapter.ViewHolder>
{
    private Context context;
    private List<Post>postList;
    private FirebaseFirestore firebaseFirestore;

    public BlogPostsAdapter(Context context, List<Post> postList)
    {
        this.context = context;
        this.postList = postList;
    } //

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView imageViewUserProfile,imageViewPostImage;
        private TextView textViewUserName , textViewTimesStamp,textViewPostDescription;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            imageViewUserProfile = itemView.findViewById(R.id.imageViewBlogPostRecyclerUserProfile);
            imageViewPostImage = itemView.findViewById(R.id.imageViewBlogPostRecyclerPostImage);
            textViewUserName = itemView.findViewById(R.id.textViewBlogPostRecyclerUserName);
            textViewTimesStamp = itemView.findViewById(R.id.textViewBlogPostRecyclerTimesStamp);
            textViewPostDescription = itemView.findViewById(R.id.textViewBlogPostRecyclerPostDescription);

            firebaseFirestore = FirebaseFirestore.getInstance();

        } /// constructor closed
    } // viewHolder class closed

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_post_recycle_view_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position)
    {
        Post bean = postList.get(position);
        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(bean.getTimestamp().getSeconds()*1000);


        firebaseFirestore.collection(Utils.USERS).document(bean.getCurrentUserId()).addSnapshotListener(new EventListener<DocumentSnapshot>()
        {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e)
            {
                if(documentSnapshot.exists() && documentSnapshot!=null)
                {
                    User user =  documentSnapshot.toObject(User.class);
                    Picasso.get().load(user.getImageUrl()).placeholder(R.drawable.profile_image).into(holder.imageViewUserProfile);
                    holder.textViewUserName.setText(user.getName());
                }
            }
        });

        Picasso.get().load(bean.getPostImageUrl()).placeholder(R.drawable.profile_image).into(holder.imageViewPostImage);
        holder.textViewTimesStamp.setText(timeAgo);
        holder.textViewPostDescription.setText(bean.getPostDescription());


    }

    @Override
    public int getItemCount()
    {
        return postList.size();
    }

} // adapter class closed  closed
