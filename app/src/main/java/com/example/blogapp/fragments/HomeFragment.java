package com.example.blogapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.blogapp.R;
import com.example.blogapp.Utils;
import com.example.blogapp.adapter.BlogPostsAdapter;
import com.example.blogapp.model.Post;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;


public class HomeFragment extends Fragment
{
    View view;
    private RecyclerView recyclerView;
    private FirebaseFirestore firebaseFirestore;
    private List<Post>postList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        firebaseFirestore = FirebaseFirestore.getInstance();
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.recycleViewHomeFragment);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        firebaseFirestore.collection(Utils.POSTS).addSnapshotListener(new EventListener<QuerySnapshot>()
        {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e)
            {
                postList = new ArrayList<>();
                if(queryDocumentSnapshots!=null)
                {
                    for(QueryDocumentSnapshot snapshot:queryDocumentSnapshots)
                    {
                        Post post = snapshot.toObject(Post.class);
                        postList.add(post);
                    } // for closed
                    BlogPostsAdapter blogPostsAdapter = new BlogPostsAdapter(getActivity(),postList);
                    recyclerView.setAdapter(blogPostsAdapter);
                    blogPostsAdapter.notifyDataSetChanged();
                } //
            } // onEvent closed
        }); // addSnapshot closed



        return view;
    }
}