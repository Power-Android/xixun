package com.power.travel.xixuntravel.pic;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.app.BaseActivity;
import com.power.travel.xixuntravel.utils.ToastUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


/**
 *	图片二级页面
 * @author fan
 *
 */
public class ImageGridActivity extends BaseActivity {
	public static final String EXTRA_IMAGE_LIST = "imagelist";

	// ArrayList<Entity> dataList;//
	List<ImageItem> dataList;
	GridView gridView;
	ImageGridAdapter adapter;//
	AlbumHelper helper;
	Button bt;
	TextView quxiao;
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				ToastUtil.showToast(getApplicationContext(), "最多选择9张图片");
				break;

			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_image_grid);

		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());

		dataList = (List<ImageItem>) getIntent().getSerializableExtra(
				EXTRA_IMAGE_LIST);

		initView();
		quxiao=(TextView)findViewById(R.id.erji_quxiao);
		quxiao.setOnClickListener(this);
		bt = (Button) findViewById(R.id.wancheng);
		bt.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				ArrayList<String> list = new ArrayList<String>();
				Collection<String> c = adapter.map.values();
				Iterator<String> it = c.iterator();
				for (; it.hasNext();) {
					list.add(it.next());
				}

				if (Bimp.act_bool) {
//					Intent intent = new Intent(ImageGridActivity.this,
//							PublishedActivity.class);
//					startActivity(intent);
					finish();
					Bimp.act_bool = false;
				}
				for (int i = 0; i < list.size(); i++) {
					if (Bimp.drr.size() < 9) {
						Bimp.drr.add(list.get(i));
					}
				}
				finish();
			}

		});
	}


	private void initView() {
		gridView = (GridView) findViewById(R.id.gridview);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new ImageGridAdapter(ImageGridActivity.this, dataList,
				mHandler);
		gridView.setAdapter(adapter);
		adapter.setTextCallback(new ImageGridAdapter.TextCallback() {
			public void onListen(int count) {
				bt.setText("完成" + "(" + count + ")");
			}
		});

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
				/**
				 * 闁哄秷顬冨畵涔竜sition闁告瑥鍊归弳鐔兼晬鐏炶棄璁插ù鐘劥楠炲繐顕ュΔ鍕拷?GridView闁汇劌瀚悺姗礽ew闁烩晛鎽滅划锔撅拷濮樿鲸鐣遍悗鍦仒缂嶅鐚炬导娆戠闁绘帟娉涢幃妤呭冀鐟欏嫬绁﹂悗鐟板暟濞堟吔sSelected闁绘锟�
				 * 閿熶粙鏁嶉敓锟介柡澶堝劚閸ㄤ粙寮鐔感﹂柛姘鹃檮濡绮堝ú顏庢嫹濞戞搩鍘介弲銉╁几濠у尅锟�闁煎嘲鍘栫花顒勬焻婢跺鍘柡浣哥墛閻忓鎯冮崟顕咃拷?闁告帗鐟辩槐婵囩▔鐎ｎ喗妗ㄩ梺顐拷?閸樸倝宕抽妸褎鐣卞ù鐙呯悼閻栨粍绋夐婵堢獥闁哄牆顦块鈺呭及閿燂拷
				 */
				// if(dataList.get(position).isSelected()){
				// dataList.get(position).setSelected(false);
				// }else{
				// dataList.get(position).setSelected(true);
				// }
				/**
				 * 闂侇偅姘ㄩ悡锟犳焻閸岀偛甯抽柛锝庣厜缁辨繄绱掗幋婵堟毎闁汇劌瀚弳鐔煎箲椤旂厧绲洪柣銏㈠枍缁繝寮ㄩ悷鏉跨秮闁挎稑鑻花鑼躲亹閹惧啿鐓曢柡鍌涘椤懘宕堕敓锟�				 */
				adapter.notifyDataSetChanged();
			}

		});

	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		if(v==quxiao){
			finish();
		}
	}
	
	
	
}
