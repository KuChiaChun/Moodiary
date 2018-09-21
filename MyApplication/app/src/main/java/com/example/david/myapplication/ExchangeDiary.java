package com.example.david.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ExchangeDiary extends BaseActivity {
    private static final String TAG = ExchangeDiary.class.getSimpleName();
    private LinkedList<String> list = new LinkedList<>();
    public final static String EXTRA_PREFIX = ChatRoomActivity.class.getName();
    public final static String EXTRA_ROOM_ID = EXTRA_PREFIX + ".ROOM_ID";
    private String chatRoomId, friend;
    private RecyclerView diaryRecyclerView;
    private LinearLayoutManager dLinearLayoutManager;
    private DatabaseReference diaryReference, qualifyReference, userlistReference, exchangeReference;
    private FirebaseRecyclerAdapter<Diary, DiaryViewHolder> dFirebaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);
        final Date datesys = new Date();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String dateSystem = sdf.format(datesys);
        exchangeReference = mFirebaseDatabaseReference.child("exchange").child(getSpecifiedDayBefore(dateSystem, 1));
        userlistReference = mFirebaseDatabaseReference.child("exchange").child(getSpecifiedDayBefore(dateSystem, 1));
        userlistReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                if (dataSnapshot.child("end").getValue().equals(false)) {
                    for (DataSnapshot ds : dataSnapshot.child("users").getChildren()) {
                        list.add(ds.getValue().toString());
                    }

                    if (!list.isEmpty()) {
                        Collections.shuffle(list);
                        int count = 1;
                        String user1 = null, user2 = null;
                        for (String i : list) {
                            if (count % 2 != 0) {
                                user1 = i;
                            } else {
                                user2 = i;
                                userlistReference.child("users").child((user1)).setValue(user2);
                                userlistReference.child("users").child((user2)).setValue(user1);
                            }
                            count++;
                        }
                        userlistReference.child("end").setValue(true);
                    }

                }
                exchangeReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        friend = dataSnapshot.child("users").child(mFirebaseUser.getUid()).getValue().toString();
                        diaryReference = mFirebaseDatabaseReference.child(User.CHILD_NAME).child(friend).child("diary").child(getSpecifiedDayBefore(dateSystem, 1));
                        initView();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        chatRoomId = getIntent().getStringExtra(EXTRA_ROOM_ID);
//        exchangeReference = mFirebaseDatabaseReference.child("exchange").child(getSpecifiedDayBefore(dateSystem, 1));
//        exchangeReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                friend = dataSnapshot.child(mFirebaseUser.getUid()).getValue().toString();
//                diaryReference = mFirebaseDatabaseReference.child(User.CHILD_NAME).child(friend).child("diary").child(getSpecifiedDayBefore(dateSystem, 1));
//                initView();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        Log.d(TAG, mFirebaseUser.getUid());


        //檢查使用者前5天上傳日記是否大於3，如果有則設定qualified屬性為true，
        qualifyReference = mFirebaseDatabaseReference.child(User.CHILD_NAME).child(mFirebaseUser.getUid()).child("diary");

        qualifyReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;
                for (int minus = 0; minus <= 4; minus++) {
                    if (dataSnapshot.child(getSpecifiedDayBefore(sdf.format(datesys), minus)).getChildrenCount() > 0) {
                        count++;
                    }
                }
                if (count >= 3) {
                    mFirebaseDatabaseReference.child(User.CHILD_NAME).child(mFirebaseUser.getUid()).child("qualified").setValue(true);
                } else
                    mFirebaseDatabaseReference.child(User.CHILD_NAME).child(mFirebaseUser.getUid()).child("qualified").setValue(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        if (null == mFirebaseUser && !Room.PUBLIC_ROOM_ID.equals(chatRoomId)) {
            finish();
            return;
        }
    }

    public void userlistupdate(View v) {

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
                viewHolder.titleTextView.setText(diary.getTitle() + "  ");
                viewHolder.moodTextView.setText("心情:" + diary.getmood());
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
//        Intent intent = new Intent();
//        intent.setClass(ExchangeDiary.this, HomeActivity.class);
//        startActivity(intent);
        finish();
        return true;
    }

    public static String getSpecifiedDayBefore(String specifiedDay, int minus) {//可以用new Date().toLocalString()传递参数
        Calendar c = Calendar.getInstance();
        Date date = null;

        try {
            date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day - minus);

        String dayBefore = new SimpleDateFormat("yyyy-MM-dd").format(c
                .getTime());
        return dayBefore;
    }

}

