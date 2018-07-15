package com.why.project.recyclerviewitemtouchhelperdemo;

import android.app.Service;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.why.project.recyclerviewitemtouchhelperdemo.adapter.ChannelAdapter;
import com.why.project.recyclerviewitemtouchhelperdemo.bean.ChannelBean;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

	private RecyclerView mRecyclerView;
	private ArrayList<ChannelBean> mChannelBeanArrayList;
	private ChannelAdapter mChannelAdapter;

	/**拖拽功能*/
	private ItemTouchHelper itemTouchHelper;
	private int currentPagePosition = -1;//当前拖拽的item的原始位置，从0开始【长按时赋值】，用来和currentPageNewPosition对比进行判断是否执行排序接口
	private int currentPageNewPosition = -1;//当前item拖拽后的位置，从0开始
	private boolean newOrder = false;//标记是否拖拽排序过，默认是false

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initViews();
		initDatas();
		initEvents();

	}

	private void initViews() {
		mRecyclerView = findViewById(R.id.recycler_view);
	}

	private void initDatas() {
		//初始化集合
		mChannelBeanArrayList = new ArrayList<ChannelBean>();
		for(int i=0; i<10;i++){
			ChannelBean channelBean = new ChannelBean();
			channelBean.setChannelId("123"+i);
			channelBean.setChannelName("频道"+i);

			mChannelBeanArrayList.add(channelBean);
		}

		//设置布局管理器
		GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3);
		mRecyclerView.setLayoutManager(gridLayoutManager);

		//设置适配器
		if(mChannelAdapter == null){
			//设置适配器
			mChannelAdapter = new ChannelAdapter(this, mChannelBeanArrayList);
			mRecyclerView.setAdapter(mChannelAdapter);
			//添加分割线
			//设置添加删除动画
			//调用ListView的setSelected(!ListView.isSelected())方法，这样就能及时刷新布局
			mRecyclerView.setSelected(true);
		}else{
			mChannelAdapter.notifyDataSetChanged();
		}

		initItemTouchHelper();
	}

	private void initItemTouchHelper() {
		//拖拽
		itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback(){

			//开启长按拖拽功能，默认为true【暂时用不到】
			//如果需要我们自定义拖拽和滑动，可以设置为false，然后调用itemTouchHelper.startDrag(ViewHolder)方法来开启！
			@Override
			public boolean isLongPressDragEnabled() {
				return true;
			}

			//开始滑动功能，默认为true【暂时用不到】
			//如果需要我们自定义拖拽和滑动，可以设置为false，然后调用itemTouchHelper.startSwipe(ViewHolder)方法来开启！
			@Override
			public boolean isItemViewSwipeEnabled() {
				return true;
			}

			/*用于设置是否处理拖拽事件和滑动事件，以及拖拽和滑动操作的方向
			比如如果是列表类型的RecyclerView，拖拽只有UP、DOWN两个方向
			而如果是网格类型的则有UP、DOWN、LEFT、RIGHT四个方向
			*/
			@Override
			public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
				int dragFlags = 0;//dragFlags 是拖拽标志
				int swipeFlags = 0;//swipeFlags是侧滑标志，我们把swipeFlags 都设置为0，表示不处理滑动操作
				if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
					dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
					swipeFlags = 0;
				} else {
					dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
					swipeFlags = 0;
				}
				Log.w("ItemTouchHelper","{getMovementFlags}dragFlags="+dragFlags+";swipeFlags="+swipeFlags);
				return makeMovementFlags(dragFlags, swipeFlags);
			}

			/*如果我们设置了非0的dragFlags ，那么当我们长按item的时候就会进入拖拽并在拖拽过程中不断回调onMove()方法
			我们就在这个方法里获取当前拖拽的item和已经被拖拽到所处位置的item的ViewHolder，
			有了这2个ViewHolder，我们就可以交换他们的数据集并调用Adapter的notifyItemMoved方法来刷新item*/
			@Override
			public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
				int fromPosition = viewHolder.getAdapterPosition();//得到拖动ViewHolder的position
				int toPosition = target.getAdapterPosition();//得到目标ViewHolder的position
				Log.w("ItemTouchHelper","{onMove}fromPosition="+fromPosition+";toPosition="+toPosition);
				if (fromPosition < toPosition) {
					for (int i = fromPosition; i < toPosition; i++) {
						Collections.swap(mChannelBeanArrayList, i, i + 1);
					}
				} else {
					for (int i = fromPosition; i > toPosition; i--) {
						Collections.swap(mChannelBeanArrayList, i, i - 1);
					}
				}
				mChannelAdapter.notifyItemMoved(fromPosition, toPosition);

				return true;
			}

			/*同理如果我们设置了非0的swipeFlags，我们在侧滑item的时候就会回调onSwiped的方法，我们不处理这个事件，空着就行了。*/
			@Override
			public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

			}
			//我们希望拖拽的Item在拖拽的过程中发生震动或者颜色变深，这样就需要继续重写下面两个方法
			//当长按选中item的时候（拖拽开始的时候）调用
			//ACTION_STATE_IDLE：闲置状态
			//ACTION_STATE_SWIPE：滑动状态
			//ACTION_STATE_DRAG：拖拽状态
			@Override
			public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
				Log.w("ItemTouchHelper","{onSelectedChanged}actionState="+actionState);
				if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
					//获取系统震动服务
					Vibrator vib = (Vibrator) MainActivity.this.getSystemService(Service.VIBRATOR_SERVICE);
					//震动70毫秒
					vib.vibrate(70);
					viewHolder.itemView.setPressed(true);
					viewHolder.itemView.setBackgroundColor(Color.parseColor("#ff0000"));//演示拖拽的时候item背景颜色加深（实际情况中去掉）
				}
				super.onSelectedChanged(viewHolder, actionState);
			}

			//当手指松开的时候（拖拽或滑动完成的时候）调用，这时候我们可以将item恢复为原来的状态（相对于背景颜色加深来说的）
			@Override
			public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
				super.clearView(recyclerView, viewHolder);
				Log.w("ItemTouchHelper","{clearView}viewHolder.getAdapterPosition="+viewHolder.getAdapterPosition());
				viewHolder.itemView.setPressed(false);
				currentPageNewPosition = viewHolder.getAdapterPosition();
				Log.w("ItemTouchHelper","{clearView}currentPagePosition="+currentPagePosition);
				Log.w("ItemTouchHelper","{clearView}currentPageNewPosition="+currentPageNewPosition);
				if(!(currentPagePosition == currentPageNewPosition)){
					newOrder = true;
					//执行其他方法，比如设置拖拽后的item为选中状态
				}

				viewHolder.itemView.setBackgroundColor(Color.parseColor("#c5c5c5"));//演示拖拽的完毕后item背景颜色恢复原样（实际情况中去掉）
			}
		});
		//设置是否可以排序
		itemTouchHelper.attachToRecyclerView(mRecyclerView);
	}

	private void initEvents() {
		//列表适配器的点击监听事件
		mChannelAdapter.setOnItemClickLitener(new ChannelAdapter.OnItemClickLitener() {
			@Override
			public void onItemClick(View view, int position) {
				Toast.makeText(MainActivity.this, mChannelBeanArrayList.get(position).getChannelName(), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onItemLongClick(View view, int position) {
				currentPagePosition = position;//拖拽用到的
				Toast.makeText(MainActivity.this, "长按", Toast.LENGTH_SHORT).show();
			}
		});
	}
}
