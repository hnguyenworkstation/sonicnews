package me.hnguyenuml.spyday.UI;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import me.hnguyenuml.spyday.Fragments.StickerItemFragment;

/**
 * Created by Hung Nguyen on 10/23/2016.
 */

public class EmojiChatAdapter extends FragmentStatePagerAdapter {
    public EmojiChatAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return StickerItemFragment.newInstance("EmojiChat", "Sticker");
    }

    @Override
    public int getCount() {
        return 20;
    }

    public String getPageTitle(int i) {
        return "Sticker: " + (i + 1);
    }

    public interface StickerClickListener {
        public void onStickerClick(int page, int position);
    }
}
