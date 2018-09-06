package com.example.david.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class ExchangeDiary extends BaseActivity {
    private static final String TAG = ExchangeDiary.class.getSimpleName();
    public final static String EXTRA_PREFIX = ChatRoomActivity.class.getName();
    public final static String EXTRA_ROOM_ID = EXTRA_PREFIX + ".ROOM_ID";
    private String chatRoomId;
    private RecyclerView diaryRecyclerView;
    private LinearLayoutManager dLinearLayoutManager;
    private DatabaseReference diaryReference;
    private FirebaseRecyclerAdapter<Diary, DiaryViewHolder> dFirebaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);

        chatRoomId = getIntent().getStringExtra(EXTRA_ROOM_ID);
        diaryReference = mFirebaseDatabaseReference.child(User.CHILD_NAME).child("ypBlp4kiPIYjDyGkrCwptQdaTVp1").child("diary");
        Log.d(TAG, "Room ID:" + chatRoomId);

        initView();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (null == mFirebaseUser && !Room.PUBLIC_ROOM_ID.equals(chatRoomId)) {
            finish();
            return;
        }
    }

    private void initView() {
        diaryRecyclerView = (RecyclerView) findViewById(R.id.diary_recycler_view);
        // Initialize ProgressBar and RecyclerView.
//        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        dLinearLayoutManager = new LinearLayoutManager(this);
        dLinearLayoutManager.setStackFromEnd(true);
        diaryRecyclerView.setLayoutManager(dLinearLayoutManager);

        dFirebaseAdapter = new FirebaseRecyclerAdapter<Diary, ExchangeDiary.DiaryViewHolder>(
                Diary.class,
                ExchangeDiary.DiaryViewHolder.layoutResId,
                ExchangeDiary.DiaryViewHolder.class,
                diaryReference) {

            @Override
            protected void populateViewHolder(ExchangeDiary.DiaryViewHolder viewHolder,
                                              Diary diary, int position) {
//                mProgressBar.setVisibility(ProgressBar.INVISIBLE);

                viewHolder.dateTextView.setText(diary.getdate());
                viewHolder.contentTextView.setText(diary.getContent());
                viewHolder.titleTextView.setText(diary.getTitle()+"  ");
                viewHolder.moodTextView.setText("心情:"+diary.getmood());
            }
        };

        dFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = dFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        dLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    diaryRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        diaryRecyclerView.setLayoutManager(dLinearLayoutManager);
        diaryRecyclerView.setAdapter(dFirebaseAdapter);

    }

    public static class DiaryViewHolder extends RecyclerView.ViewHolder {

        public final static int layoutResId = R.layout.item_exchange_diary;

        public TextView dateTextView;
        public TextView contentTextView;
        public TextView titleTextView;
        public TextView moodTextView;
//        public CircleImageView messengerImageView;

        public DiaryViewHolder(View v) {
            super(v);
            dateTextView = (TextView) itemView.findViewById(R.id.main_d_date);
            contentTextView = (TextView) itemView.findViewById(R.id.main_d_content);
            titleTextView = itemView.findViewById(R.id.main_d_title);
            moodTextView = itemView.findViewById(R.id.main_d_mood);
//            messengerImageView = (CircleImageView) itemView.findViewById(R.id.messenger_thumb);
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Intent intent = new Intent();
        intent.setClass(ExchangeDiary.this, HomeActivity.class);
        startActivity(intent);
        finish();
        return true;
    }

}

