package com.kidsdynamic.swing.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kidsdynamic.commonlib.utils.ObjectUtils;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.domain.UserManager;
import com.kidsdynamic.swing.model.KidsEntityBean;
import com.kidsdynamic.swing.utils.GlideHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.carbs.android.avatarimageview.library.AvatarImageView;

public class KidsListBottomPopWindow extends PopupWindow {

    public KidsListBottomPopWindow(Context context) {
        super(context);
    }

    public static class Builder {

        private Context mContext;
        private List<KidsEntityBean> mKidsItemList = new ArrayList<>();
//        private List<KidsEntityBean> mSharedKidsItemList = new ArrayList<>();
        private List<Integer> mImgItemList = new ArrayList<>();

        private OnWhichClickListener mOnWhichClickListener;

        private int textColor;
        private boolean isShowHtml = false;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder setItemTextColor(int color) {
            textColor = color;
            return this;
        }

        public Builder showHtml(boolean isShowHtml) {
            this.isShowHtml = isShowHtml;
            return this;
        }

        /*public Builder setItems(int itemsId, final OnWhichClickListener listener) {
            CharSequence[] itemArray = mContext.getResources().getTextArray(itemsId);
            setItems(itemArray, listener);
            return this;
        }*/

       /* public Builder setItems(KidsEntityBean[] itemArray, final OnWhichClickListener listener) {
            for (KidsEntityBean kidsEntityBean : itemArray) {
                mKidsItemList.add(kidsEntityBean);
            }
            mOnWhichClickListener = listener;
            return this;
        }*/

        public Builder setItems(List<KidsEntityBean> myKidsItemList,
                                final OnWhichClickListener listener) {
            mKidsItemList = myKidsItemList;
//            mSharedKidsItemList = sharedKidsItemList;

            mOnWhichClickListener = listener;
            return this;
        }

        public Builder setItems(Integer[] itemArray, final OnWhichClickListener listener) {
            mImgItemList = Arrays.asList(itemArray);
            mOnWhichClickListener = listener;
            return this;
        }

        public Builder setItems(ArrayList<Integer> itemList, final OnWhichClickListener listener) {
            mImgItemList = itemList;
            mOnWhichClickListener = listener;
            return this;
        }

        public interface OnWhichClickListener {
            void onWhichClick(View v, long kidsId);
        }

        @SuppressLint("InflateParams")
        public KidsListBottomPopWindow create() {
            final KidsListBottomPopWindow popWindow = new KidsListBottomPopWindow(mContext);

            LayoutInflater inflater = LayoutInflater.from(mContext);
            View layout = inflater.inflate(R.layout.kids_list_bottom_pop_window, null);
            final LinearLayout linearContent_myKids = layout.findViewById(R.id.linear_my_kids_items);
            LinearLayout linear_sharedKids = layout.findViewById(R.id.linear_shared_kids_items);
            final LinearLayout linearContent =  layout.findViewById(R.id.layout_content);

            //加载kids信息
            loadKidsInfos(linearContent_myKids,inflater,linear_sharedKids,popWindow);

            popWindow.setContentView(layout);
            popWindow.setWidth(LayoutParams.MATCH_PARENT);
            popWindow.setHeight(LayoutParams.WRAP_CONTENT);
            popWindow.setFocusable(true);
            popWindow.setTouchable(true);
            popWindow.setOutsideTouchable(true);
            popWindow.setBackgroundDrawable(new ColorDrawable(mContext.getResources().
                    getColor(R.color.pop_window_background)));

            Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.push_bottom_in);
            linearContent.startAnimation(anim);


            // Click out of layout
            layout.setOnTouchListener(new OnTouchListener() {

                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int height = v.findViewById(R.id.layout_content).getTop();
                    int y = (int) event.getY();
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (y < height) {
                            Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.push_bottom_out);
                            linearContent.startAnimation(anim);
                            linearContent.postDelayed(new Runnable() {

                                @Override
                                public void run() {

                                    Log.w("pop", "onTouch");
                                    popWindow.dismiss();
                                }
                            }, 300);
                        }
                    }

                    return true;
                }
            });

            return popWindow;
        }

        private void loadKidsInfos(LinearLayout linearContent_myKids, LayoutInflater inflater,
                                   LinearLayout linear_sharedKids,
                                   final KidsListBottomPopWindow popWindow) {

            //my kids
            if(!ObjectUtils.isListEmpty(mKidsItemList)){
                KidsEntityBean kidsEntityBean1 = mKidsItemList.get(0);
                mKidsItemList.add(kidsEntityBean1);

                KidsEntityBean kidsEntityBean2 = mKidsItemList.get(0);
                KidsEntityBean kidsEntityBean3 = new KidsEntityBean();
                kidsEntityBean3.setKidsId(kidsEntityBean2.getKidsId());
                kidsEntityBean3.setName(kidsEntityBean2.getName());
                kidsEntityBean3.setShareType(KidsEntityBean.shareType_from_other);

                mKidsItemList.add(kidsEntityBean3);

                for (KidsEntityBean kidsEntityBean : mKidsItemList) {

                    View kidsInfoItem = null;

                    if(kidsEntityBean.getShareType() == KidsEntityBean.shareType_from_other){
                        kidsInfoItem = initKidsInfoItem(linear_sharedKids, inflater, kidsEntityBean);
                    }else {
                        kidsInfoItem = initKidsInfoItem(linearContent_myKids, inflater, kidsEntityBean);
                    }

                    kidsInfoItem.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mOnWhichClickListener != null) {
                                mOnWhichClickListener.onWhichClick(v, ((Long) v.getTag()));
                            }
                            popWindow.dismiss();
                        }
                    });
                }
            }

            //他人分享给自己的kids
           /* if(!ObjectUtils.isListEmpty(mSharedKidsItemList)){
                for (KidsEntityBean kidsEntityBean : mSharedKidsItemList) {
                    View kidsInfoItem = initKidsInfoItem(linear_sharedKids, inflater, kidsEntityBean);

                    kidsInfoItem.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mOnWhichClickListener != null) {
                                mOnWhichClickListener.onWhichClick(v, ((Long) v.getTag()));
                            }
                            popWindow.dismiss();
                        }
                    });
                }
            }*/

        }

        private View initKidsInfoItem(LinearLayout linearItems, LayoutInflater inflater,KidsEntityBean kidsEntityBean){
            View viewKids = inflater.inflate(R.layout.item_kids_info, linearItems, false);
            linearItems.addView(viewKids);

            AvatarImageView avatarImageView = viewKids.findViewById(R.id.user_avatar_iv);
            TextView tv_kidsName = viewKids.findViewById(R.id.tv_kids_name);

            tv_kidsName.setText(kidsEntityBean.getName());

            String url =  UserManager.getProfileRealUri(kidsEntityBean.getProfile());
            if(kidsEntityBean.getShareType() == KidsEntityBean.shareType_from_other){
                GlideHelper.getCircleImageViewOnlyCacheInMemory(mContext,url,avatarImageView);
            }else {
                GlideHelper.showCircleImageViewWithSignature(mContext,url,
                        kidsEntityBean.getLastUpdate()+"",avatarImageView);
            }

            //携带数据
            viewKids.setTag(kidsEntityBean.getKidsId());

            return viewKids;

        }

        private TextView initTextView(LinearLayout linearItems, LayoutInflater inflater, int i, int size) {
            if (i > 0) {
                View lineView = inflater.inflate(R.layout.bottom_pop_window_line_view, linearItems,
                        false);
                linearItems.addView(lineView);
            }
            TextView textView = (TextView) inflater.inflate(
                    R.layout.bottom_pop_window_text_view, linearItems, false);
            if (size == 1) {
                textView.setBackgroundResource(R.drawable.holder_item_round_selector);
            } else if (size >= 2) {
                if (i == 0) {
                    textView.setBackgroundResource(R.drawable.holder_item_round_top_selector);
                } else if (i == size - 1) {
                    textView.setBackgroundResource(R.drawable.holder_item_round_bottom_selector);
                } else {
                    textView.setBackgroundResource(R.drawable.holder_item_shape_selector);
                }
            }
            textView.setId(i);
            return textView;
        }

        private ImageView initImageView(LinearLayout linearItems, LayoutInflater inflater, int i, int size) {
            if (i > 0) {
                View lineView = inflater.inflate(R.layout.bottom_pop_window_line_view, linearItems,
                        false);
                linearItems.addView(lineView);
            }
            ImageView imageView = (ImageView) inflater.inflate(
                    R.layout.bottom_pop_window_image_view, linearItems, false);
            if (size == 1) {
                imageView.setBackgroundResource(R.drawable.holder_item_round_selector);
            } else if (size >= 2) {
                if (i == 0) {
                    imageView.setBackgroundResource(R.drawable.holder_item_round_top_selector);
                } else if (i == size - 1) {
                    imageView.setBackgroundResource(R.drawable.holder_item_round_bottom_selector);
                } else {
                    imageView.setBackgroundResource(R.drawable.holder_item_shape_selector);
                }
            }
            imageView.setId(i);
            return imageView;
        }

    }

}
