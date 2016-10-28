package me.hnguyenuml.spyday.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import me.hnguyenuml.spyday.R;
import me.hnguyenuml.spyday.UI.EmojiAdapter;
import me.hnguyenuml.spyday.UI.EmojiChatAdapter;
import me.hnguyenuml.spyday.UI.SlidingTabLayout;

public class StickerKeyboardFragment extends Fragment implements
        StickerItemFragment.OnChildInteractionListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private OnClickStickerListener mStickerListener;
    private ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout;

    private Animation mAnimationOut;
    private Animation mAnimationIn;
    private View horizontalBar;
    private boolean isAnimating = false;

    private Timer timer;
    protected int increate = 0;
    private static final long TIMER_PERIOD = 500;
    private static final long TIMER_DELAY = 0;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnClickStickerListener mListener;

    public StickerKeyboardFragment() {
    }

    public static StickerKeyboardFragment newInstance(String param1, String param2) {
        StickerKeyboardFragment fragment = new StickerKeyboardFragment();
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
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_sticker_keyboard, container, false);
        mViewPager = (ViewPager) rootView.findViewById(R.id.skeyboard_viewpager);
        EmojiChatAdapter stickerAdapter = new EmojiChatAdapter(getChildFragmentManager());
        mViewPager.setAdapter(stickerAdapter);

        mSlidingTabLayout = (SlidingTabLayout) rootView.findViewById(R.id.skeyboard_scrolltab);
        mSlidingTabLayout.setCustomTabView(R.layout.emoji_sliding_tab, R.id.content);
        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                onScrollDown(false);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        initSlideBarAnimation(rootView);

        ImageView btnShop = (ImageView) rootView.findViewById(R.id.skeyboard_addbtn);
        btnShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Will Open StickerShop", Toast.LENGTH_LONG).show();
            }
        });

        return rootView;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onClickSticker(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnClickStickerListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
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

    private void initSlideBarAnimation(View v) {
        horizontalBar = v.findViewById(R.id.skeyboard_scroll);
        mAnimationIn = AnimationUtils.loadAnimation(getActivity(),
                R.anim.slide_bottom_up);
        mAnimationOut = AnimationUtils.loadAnimation(getActivity(),
                R.anim.slide_out_top);
        mAnimationIn.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                horizontalBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isAnimating = false;
                deleteTimer();
            }
        });

        mAnimationOut.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                horizontalBar.setVisibility(View.INVISIBLE);
                isAnimating = false;
                // Start timer
                setupTimer();
            }
        });

    }

    private void deleteTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    private void setupTimer() {
        if (horizontalBar.getVisibility() != View.VISIBLE) {
            deleteTimer();
            timer = new Timer();
            // Create timer task for bottom bar
            TimerTask myTimerTask = new TimerTask() {

                @Override
                public void run() {
                    if (increate >= 3) {
                        onScrollDown(false);
                        increate = 0;
                    } else {
                        increate++;
                    }
                }
            };
            timer.schedule(myTimerTask, TIMER_DELAY, TIMER_PERIOD);
        }
    }

    @Override
    public void onScrollDown(boolean isScrollDown) {
        if (isAnimating)
            return;
        if (isScrollDown) {
            if (horizontalBar.getVisibility() == View.VISIBLE) {
                isAnimating = true;
                Handler refresh = new Handler(Looper.getMainLooper());
                refresh.postDelayed(new Runnable() {
                    public void run() {
                        horizontalBar.startAnimation(mAnimationOut);
                    }
                }, 75);
            }
        } else {
            if (horizontalBar.getVisibility() != View.VISIBLE) {
                isAnimating = true;
                Handler refresh = new Handler(Looper.getMainLooper());
                refresh.postDelayed(new Runnable() {
                    public void run() {
                        horizontalBar.startAnimation(mAnimationIn);
                    }
                }, 75);
            }
        }
    }

    public interface OnClickStickerListener {
        // TODO: Update argument type and name
        public void onClickSticker(Uri uri);
    }

    @Override
    public void resetCountUpAnimation() {
        increate = 0;
    }

    @Override
    public void clickOnItem(int i) {
        if (mListener != null) {
            mListener.onClickSticker(null);
        }
    }

}
