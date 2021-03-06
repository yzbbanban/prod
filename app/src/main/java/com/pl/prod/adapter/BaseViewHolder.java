package com.pl.prod.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by brander on 2017/7/11.
 */

public class BaseViewHolder extends RecyclerView.ViewHolder{
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;


    public BaseViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(new OnClickLis());
        itemView.setOnLongClickListener(new OnLongClickLis());
    }

    private class OnClickLis implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    private class OnLongClickLis implements View.OnLongClickListener {

        @Override
        public boolean onLongClick(View v) {
            if (mOnItemLongClickListener != null) {
                mOnItemLongClickListener.onItemLongClick(v, getAdapterPosition());
                return true;
            }
            return false;
        }
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        mOnItemClickListener = l;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener l) {
        mOnItemLongClickListener = l;
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }
}
