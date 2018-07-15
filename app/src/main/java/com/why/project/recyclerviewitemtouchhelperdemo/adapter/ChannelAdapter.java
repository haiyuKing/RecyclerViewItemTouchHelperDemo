package com.why.project.recyclerviewitemtouchhelperdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.why.project.recyclerviewitemtouchhelperdemo.R;
import com.why.project.recyclerviewitemtouchhelperdemo.bean.ChannelBean;

import java.util.ArrayList;

/**
 * Created by HaiyuKing
 * Used 频道列表适配器
 */

public class ChannelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
	/**上下文*/
	private Context myContext;
	/**频道集合*/
	private ArrayList<ChannelBean> listitemList;

	/**
	 * 构造函数
	 */
	public ChannelAdapter(Context context, ArrayList<ChannelBean> itemlist) {
		myContext = context;
		listitemList = itemlist;
	}

	/**
	 * 获取总的条目数
	 */
	@Override
	public int getItemCount() {
		return listitemList.size();
	}

	/**
	 * 创建ViewHolder
	 */
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(myContext).inflate(R.layout.channel_list_item, parent, false);
		ItemViewHolder itemViewHolder = new ItemViewHolder(view);
		return itemViewHolder;
	}

	/**
	 * 声明grid列表项ViewHolder*/
	static class ItemViewHolder extends RecyclerView.ViewHolder
	{
		public ItemViewHolder(View view)
		{
			super(view);

			listItemLayout = (LinearLayout) view.findViewById(R.id.listitem_layout);
			mChannelName = (TextView) view.findViewById(R.id.tv_channelName);
		}

		LinearLayout listItemLayout;
		TextView mChannelName;
	}

	/**
	 * 将数据绑定至ViewHolder
	 */
	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int index) {

		//判断属于列表项还是上拉加载区域
		if(viewHolder instanceof ItemViewHolder){
			ChannelBean channelBean = listitemList.get(index);
			final ItemViewHolder itemViewHold = ((ItemViewHolder)viewHolder);

			itemViewHold.mChannelName.setText(channelBean.getChannelName());

			//如果设置了回调，则设置点击事件
			if (mOnItemClickLitener != null)
			{
				itemViewHold.listItemLayout.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						int position = itemViewHold.getLayoutPosition();//在增加数据或者减少数据时候，position和index就不一样了
						mOnItemClickLitener.onItemClick(itemViewHold.listItemLayout, position);
					}
				});
				//长按事件
				itemViewHold.listItemLayout.setOnLongClickListener(new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View view) {
						int position = itemViewHold.getLayoutPosition();//在增加数据或者减少数据时候，position和index就不一样了
						mOnItemClickLitener.onItemLongClick(itemViewHold.listItemLayout, position);
						return false;
					}
				});
			}

		}
	}

	/**
	 * 添加Item--用于动画的展现*/
	public void addItem(int position,ChannelBean listitemBean) {
		listitemList.add(position,listitemBean);
		notifyItemInserted(position);
	}
	/**
	 * 删除Item--用于动画的展现*/
	public void removeItem(int position) {
		listitemList.remove(position);
		notifyItemRemoved(position);
	}

	/*=====================添加OnItemClickListener回调================================*/
	public interface OnItemClickLitener
	{
		void onItemClick(View view, int position);
		void onItemLongClick(View view, int position);
	}

	private OnItemClickLitener mOnItemClickLitener;

	public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
	{
		this.mOnItemClickLitener = mOnItemClickLitener;
	}
}
