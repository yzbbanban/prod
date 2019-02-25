package com.pl.prod.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * recyclerView封装类
 * Created by brander on 2017/7/11.
 */

public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {
    protected Context mContext;
    protected List<T> mList;
    protected RecyclerView mRecyclerView;

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public BaseRecyclerViewAdapter(Context context) {
        mContext = context;
    }

    @Override
    public abstract BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.setOnItemClickListener(new BaseViewHolder.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, position);
                }
            }
        });
        holder.setOnItemLongClickListener(new BaseViewHolder.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                if (mOnItemLongClickListener != null) {
                    mOnItemLongClickListener.onItemLongClick(view, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public int getItemViewHeight() {
        return 0;
    }

    public void setList(List<T> list) {
        this.mList = list;
    }

    public List<T> getList() {
        return mList;
    }

    public void setList(T[] list) {
        ArrayList<T> arrayList = new ArrayList<T>(list.length);
        for (T t : list) {
            arrayList.add(t);
        }
        setList(arrayList);
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
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


