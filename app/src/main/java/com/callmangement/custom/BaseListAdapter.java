package com.callmangement.custom;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public abstract class BaseListAdapter<T extends BaseListAdapter.ViewHolder, DT/* extends Comparable*/> extends SelectableAdapter<T> {
    private boolean mAnimationEnabled = true;

    public interface OnItemClickedListener {
        void onItemClicked(int position);
    }

    public interface OnItemLongClickedListener {
        void onItemClicked(int position);
    }

    public interface OnItemActionListener {
        void onItemAction(int position, int action);
    }

    private List<DT> mDataList;

    private final Context mContext;
    private int resID;
    private OnItemClickedListener onItemClickedListener;
    private OnItemLongClickedListener onItemLongClickedListener;
    public OnItemActionListener onItemActionListener;

    public void setOnItemLongClickedListener(OnItemLongClickedListener onItemLongClickedListener) {
        this.onItemLongClickedListener = onItemLongClickedListener;
    }

    public void setOnItemClickedListener(OnItemClickedListener onItemClickedListener) {
        this.onItemClickedListener = onItemClickedListener;
    }

    public void setOnItemActionListener(OnItemActionListener onItemActionListener) {
        this.onItemActionListener = onItemActionListener;
    }

    public BaseListAdapter(Context context, List<DT> mDataList, int resID) {
        this.mDataList = mDataList;
        mContext = context;
        this.resID = resID;
    }

    public BaseListAdapter(Context context, List<DT> mDataList) {
        this.mDataList = mDataList;
        mContext = context;
    }

    public List<DT> getDataList() {
        return mDataList;
    }

    public Context getContext() {
        return mContext;
    }

    public DT getData(int position) {
        return mDataList.get(position);
    }

    public View inflateView(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(resID, parent, false);
    }

    public View inflateView(ViewGroup parent, int id) {
        return LayoutInflater.from(parent.getContext()).inflate(id, parent, false);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public void addData(DT data) {
        mDataList.add(data);
        // notifyDataSetChanged();
        notifyItemInserted(mDataList.size() - 1);
    }

    public void addData(int pos, DT data) {
        mDataList.add(pos, data);
        // notifyDataSetChanged();
        notifyItemInserted(pos);
    }

    public void addDataList(List<DT> dataList) {
        int start = mDataList.size();
        mDataList.addAll(dataList);
        int count = dataList.size();
        if (start == 0) notifyDataSetChanged();
        else notifyItemRangeInserted(start, count);
    }

    public void updateDataList(List<DT> dataList) {
        mDataList = dataList;
        notifyDataSetChanged();
    }

    public void removeItem(int pos) {
        mDataList.remove(pos);
        notifyItemRemoved(pos);
    }

    public void changeData(int index, DT data) {
        mDataList.set(index, data);
        notifyDataSetChanged();
    }

    public void cleanUp() {
        mDataList.clear();
        notifyDataSetChanged();
    }

    public void setAnimationEnabled(boolean animationEnabled) {
        mAnimationEnabled = animationEnabled;
    }

    public String bold(String str) {
        return "<b>" + str + "</b>";
    }

    public Spanned htmlString(String key, String value) {
        return Html.fromHtml(bold(key) + " " + value);
    }

    public String underline(String str) {
        return "<u>" + str + "</u>";
    }

    public Spanned htmlString(String key) {
        return Html.fromHtml(underline(key));
    }

    public String textSmall(String str) {
        return "<small><font color=#949494>" + str + "</font></small>";
    }

    public Spanned htmlStringSmall(String key) {
        return Html.fromHtml(textSmall(key));
    }

    public void setAnimation(final View view, int position) {
        if (mAnimationEnabled) {
            view.setScaleX(0f);
            view.setScaleY(0f);
            view.animate().scaleX(1f).scaleY(1f).setDuration(300).setInterpolator(new DecelerateInterpolator());
        }
    }

    /* ViewHolder for RecyclerView */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        protected Context context;
        protected View mainView;

        public ViewHolder(Context context, View itemView) {
            super(itemView);
            this.context = context;
            this.mainView = itemView;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public View getMainView() {
            return mainView;
        }

        @Override
        public void onClick(View view) {
            int position = getLayoutPosition();
            if (onItemClickedListener != null) onItemClickedListener.onItemClicked(position);
        }

        @Override
        public boolean onLongClick(View v) {
            int position = getLayoutPosition();
            if (onItemLongClickedListener != null)
                onItemLongClickedListener.onItemClicked(position);
            return true;
        }
    }
}
