package me.hnguyenuml.spyday.Fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import me.hnguyenuml.spyday.R;
import me.hnguyenuml.spyday.UI.EmojiAdapter;

public class StickerItemFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private GridView mGridView;
    private EmojiAdapter mEmojiAdapter;
    private int mLastVisiblePos;
    private boolean isGridViewScrolling;

    private String mParam1;
    private String mParam2;

    private OnStickerItemFragmentInteractionListener mListener;
    private OnChildInteractionListener mChildListener;

    public StickerItemFragment() {

    }

    public static StickerItemFragment newInstance(String param1, String param2) {
        StickerItemFragment fragment = new StickerItemFragment();
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
        return inflater.inflate(R.layout.sticker_item_gridview, container,
                false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mGridView = (GridView) view.findViewById(R.id.sticker_gridview);
        mEmojiAdapter = new EmojiAdapter(getActivity(),
                R.layout.fragment_sticker_item);
        mGridView.setAdapter(mEmojiAdapter);

        mLastVisiblePos = mGridView.getFirstVisiblePosition();
        isGridViewScrolling = false;
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                isGridViewScrolling = true;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                int currentLastPos = view.getFirstVisiblePosition();
                if (!isGridViewScrolling) {
                    mLastVisiblePos = currentLastPos;
                    return;
                }
                if (mListener != null) {
                    mChildListener.resetCountUpAnimation();
                }
                if (firstVisibleItem == 0) {
                    if (currentLastPos > mLastVisiblePos) {
                        if (mListener != null) {
                            mChildListener.onScrollDown(true);
                        }
                    }
                    mLastVisiblePos = currentLastPos;
                    return;
                }

                if (currentLastPos > mLastVisiblePos) {
                    if (mListener != null) {
                        mChildListener.onScrollDown(true);
                    }
                }
                if (currentLastPos < mLastVisiblePos) {
                    if (mListener != null) {
                        mChildListener.onScrollDown(false);
                    }
                }
                mLastVisiblePos = currentLastPos;
            }
        });

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mChildListener.clickOnItem(i);
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnStickerItemFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        try {
            mChildListener = (OnChildInteractionListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnChildInteractionListener");
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnStickerItemFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public interface OnChildInteractionListener {
        public void onScrollDown(boolean isScrollDown);
        public void resetCountUpAnimation();
        public void clickOnItem(int i);
    }
}
