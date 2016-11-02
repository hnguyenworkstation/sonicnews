package me.hnguyenuml.spyday.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.google.firebase.database.DatabaseReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import me.hnguyenuml.spyday.BasicApp.SpyDayApplication;
import me.hnguyenuml.spyday.R;
import me.hnguyenuml.spyday.Adapters.MessageAdapter;
import me.hnguyenuml.spyday.Static.Endpoint;
import me.hnguyenuml.spyday.UI.CustomScrollingHandler;
import me.hnguyenuml.spyday.UserContent.Message;

public class ListMessagesFragment extends Fragment implements AbsListView.OnItemClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private View rootView;

    private RecyclerView mRecyclerView;
    private MessageAdapter mMessageAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Message> messageList = new ArrayList<>();
    private boolean mEmptyConversation;
    private boolean mIsClearNoConversation;
    private int mPreviousPositionItemClick = -1;

    private final DatabaseReference chatRoomRef = SpyDayApplication.getInstance()
            .getPrefManager().getFirebaseDatabase().child(Endpoint.DB_CHATROOM);
    private final int duration = 1000;
    private final CustomScrollingHandler mLayoutManager = new CustomScrollingHandler(getActivity(),
            LinearLayoutManager.VERTICAL, false, duration);
    private final SpyDayApplication mInstance = SpyDayApplication.getInstance();

    public ListMessagesFragment() {
    }

    public static ListMessagesFragment newInstance(String param1, String param2) {
        ListMessagesFragment fragment = new ListMessagesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_list_messages, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.listmessagefrag_recycler);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mEmptyConversation = false;
//        for (int i = 0; i < 20; i++) {
//            Message item = new Message();
//            if (i == 10 || i == 17) {
//                item.setMessageType(Message.TYPE_MESSAGE_DATE);
//            } else if (i % 3 == 0) {
//                item.setMessageType(Message.TYPE_MESSAGE_FROM_ME);
//                item.setMessageText("String abc  abc long long long long  abc long long long long  abc long long long long  abc long long long long " + i);
//            } else {
//                item.setMessageType(Message.TYPE_MESSAGE_FROM_FRIEND);
//                item.setMessageText("UUU String abc  abc long long long long  abc long long long long  abc long long long long  abc long long long long  abc long long long long  abc long long long long " + i);
//            }
//            messageList.add(item);
//        }

        mMessageAdapter = new MessageAdapter(messageList);

        mMessageAdapter.setOnChatScreenClickListener(new MessageAdapter.OnClickChatScreenListener() {
            @Override
            public void onErrorMessageClick(View view) {
            }

            @Override
            public void onMessageClick(View view, int position) {
                showStatus(position);
                Message item = messageList.get(position);
                if (item != null) {
                    boolean isSet = item.getVisibilityDate();
                    item.setVisibilityDate(!isSet);
                    mMessageAdapter.notifyDataSetChanged();
                }
                mPreviousPositionItemClick = position;
            }

            @Override
            public void onStickerMessageClick(View view, int position) {
                showStatus(position);
                Message item = messageList.get(position);
                if (item != null) {
                    boolean isSet = item.getVisibilityDate();
                    item.setVisibilityDate(!isSet);
                    mMessageAdapter.notifyDataSetChanged();
                }
                mPreviousPositionItemClick = position;
            }
        });

        mMessageAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver(){
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mMessageAdapter.getItemCount();
                int lastVisiblePosition = mLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        mRecyclerView.setAdapter(mMessageAdapter);
        mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.listmessagefrag_swiperef);
        swipeRefreshLayout.setColorSchemeResources(R.color.orange1, R.color.green1, R.color.blue1);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setupAdapter();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2500);
            }
        });

        return rootView;
    }

    private void showStatus(int currentPos) {
        if (currentPos != mPreviousPositionItemClick &&
                mPreviousPositionItemClick != -1 && messageList.size() > mPreviousPositionItemClick) {
            Message previousItem = messageList.get(mPreviousPositionItemClick);
            if (previousItem != null)
                previousItem.setVisibilityDate(false);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public void setupAdapter() {
        if (!mEmptyConversation) {
//            for (int i = 0; i < 2; i++) {
//                Message item = new Message();
//                if (i % 2 == 0) {
//                    item.setMessageType(Message.TYPE_MESSAGE_FROM_ME);
//                    item.setMessageText("String abc long long long long  abc long long long long  abc long long long long  abc long long long long " + i);
//                } else {
//                    item.setMessageType(Message.TYPE_MESSAGE_FROM_FRIEND);
//                    item.setMessageText("UUU String abc  abc long long long long  abc long long long long  abc long long long long  abc long long long long  abc long long long long  abc long long long long " + i);
//                }
//                messageList.add(item);
//            }
//            mMessageAdapter.notifyDataSetChanged();
        }
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount());
            }
        }, 100);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    public void addMessage(String message, String roomID) {
        if (mEmptyConversation && !mIsClearNoConversation) {
            mIsClearNoConversation = true;
            messageList.clear();
        }

        if (messageList.size() > 0) {
            messageList.get(messageList.size() - 1).setVisibilityStatus(false);
        }

        Message item = new Message();
        item.setMessageType(Message.TYPE_MESSAGE_FROM_ME);
        item.setMessageText(message);
        item.setIsWarning(true);
        item.setVisibilityStatus(true);
        messageList.add(item);

        mMessageAdapter.notifyDataSetChanged();
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount());
            }
        }, 100);

        postPlainMessageToServer(message, roomID);
    }

    private void postPlainMessageToServer(String message, String roomId) {
        Message model = new Message(roomId,
                mInstance.getPrefManager().getFirebaseAuth().getCurrentUser().getUid(),
                message, Calendar.getInstance().getTime().toString());
        Map<String, Object> map = new HashMap<>();
        map.put(chatRoomRef.child(roomId).child(Endpoint.DB_CHATROOM_LISTMESSAGES).push().getKey(), model);
        chatRoomRef.child(roomId).child(Endpoint.DB_CHATROOM_LISTMESSAGES).updateChildren(map);
    }

    public void sendMessage(boolean isSticker, boolean isMe) {
        if (mEmptyConversation && !mIsClearNoConversation) {
            mIsClearNoConversation = true;
            messageList.clear();
        }

        if (messageList.size() > 0) {
            messageList.get(messageList.size() - 1).setVisibilityStatus(false);
        }

        Message item = new Message();
        if (isSticker && isMe) {
            item.setIsWarning(true);
            item.setMessageType(Message.TYPE_STICKER_FROM_ME);
        } else {
            if (isSticker) {
                item.setMessageType(Message.TYPE_STICKER_FROM_FRIEND);
            }
        }
        item.setVisibilityStatus(true);
        messageList.add(item);

        mMessageAdapter.notifyDataSetChanged();
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount());
            }
        }, 100);
    }

    public void scrollToLast() {
        mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public static String getTimeStamp(String dateStr) {
        String today =  String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = "";
        today = today.length() < 2 ? "0" + today : today;

        try {
            Date date = format.parse(dateStr);
            SimpleDateFormat todayFormat = new SimpleDateFormat("dd");
            String dateToday = todayFormat.format(date);
            format = dateToday.equals(today) ? new SimpleDateFormat("hh:mm a") : new SimpleDateFormat("dd LLL, hh:mm a");
            String tempdate = format.format(date);
            timestamp = tempdate.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timestamp;
    }
}
